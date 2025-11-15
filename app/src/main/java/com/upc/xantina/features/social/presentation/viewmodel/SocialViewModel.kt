package com.upc.xantina.features.social.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upc.xantina.features.social.domain.repository.SocialRepository
import com.upc.xantina.features.social.presentation.state.CommentsUiState
import com.upc.xantina.features.social.presentation.state.FeedUiState
import com.upc.xantina.features.social.presentation.state.RepliesUiState
import com.upc.xantina.features.social.presentation.state.SearchUiState
import com.upc.xantina.features.social.presentation.state.ExtractionDataUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialViewModel @Inject constructor(
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _feedState = MutableStateFlow<FeedUiState>(FeedUiState.Idle)
    val feedState = _feedState.asStateFlow()

    // Mapa de comentarios por postId para evitar que se compartan entre posts
    private val _commentsStateMap = MutableStateFlow<Map<String, CommentsUiState>>(emptyMap())
    val commentsStateMap = _commentsStateMap.asStateFlow()

    private val _repliesState = MutableStateFlow<RepliesUiState>(RepliesUiState.Idle)
    val repliesState = _repliesState.asStateFlow()

    fun loadFeed(token: String, limit: Int = 20, offset: Int = 0, refresh: Boolean = false) {
        viewModelScope.launch {
            _feedState.update { FeedUiState.Loading }
            
            socialRepository.getFeed(token, limit, offset)
                .onSuccess { posts ->
                    // Cargar estado de "liked" para cada post
                    val postsWithLikeStatus = posts.map { post ->
                        val isLiked = socialRepository.checkUserLiked(token, post.id)
                            .getOrElse { false }
                        post.copy(isLikedByCurrentUser = isLiked)
                    }
                    _feedState.update { FeedUiState.Success(postsWithLikeStatus) }
                }
                .onFailure { error ->
                    _feedState.update { 
                        FeedUiState.Error(error.message ?: "Error al cargar el feed") 
                    }
                }
        }
    }

    fun toggleLike(token: String, postId: String) {
        viewModelScope.launch {
            socialRepository.toggleLike(token, postId)
                .onSuccess { liked ->
                    // Actualizar el estado del feed
                    if (_feedState.value is FeedUiState.Success) {
                        val currentPosts = (_feedState.value as FeedUiState.Success).posts
                        val updatedPosts = currentPosts.map { post ->
                            if (post.id == postId) {
                                post.copy(
                                    isLikedByCurrentUser = liked,
                                    likesCount = if (liked) post.likesCount + 1 else post.likesCount - 1
                                )
                            } else {
                                post
                            }
                        }
                        _feedState.update { FeedUiState.Success(updatedPosts) }
                    }
                }
                .onFailure { error ->
                    // Silently fail or show a toast/snackbar
                }
        }
    }

    fun loadComments(token: String, postId: String) {
        viewModelScope.launch {
            // Actualizar el estado de comentarios solo para este postId
            _commentsStateMap.update { currentMap ->
                currentMap + (postId to CommentsUiState.Loading)
            }
            
            socialRepository.getPostComments(token, postId)
                .onSuccess { comments ->
                    _commentsStateMap.update { currentMap ->
                        currentMap + (postId to CommentsUiState.Success(comments))
                    }
                }
                .onFailure { error ->
                    _commentsStateMap.update { currentMap ->
                        currentMap + (postId to CommentsUiState.Error(error.message ?: "Error al cargar comentarios"))
                    }
                }
        }
    }
    
    fun getCommentsForPost(postId: String): CommentsUiState {
        return _commentsStateMap.value[postId] ?: CommentsUiState.Idle
    }

    fun createComment(token: String, postId: String, content: String, parentId: String? = null) {
        viewModelScope.launch {
            socialRepository.createComment(token, postId, content, parentId)
                .onSuccess { newComment ->
                    // Actualizar la lista de comentarios para este postId específico
                    val currentState = _commentsStateMap.value[postId]
                    if (currentState is CommentsUiState.Success) {
                        val currentComments = currentState.comments
                        val updatedComments = if (parentId == null) {
                            currentComments + newComment
                        } else {
                            // Incrementar el contador de respuestas del padre
                            currentComments.map { comment ->
                                if (comment.id == parentId) {
                                    comment.copy(repliesCount = comment.repliesCount + 1)
                                } else {
                                    comment
                                }
                            }
                        }
                        _commentsStateMap.update { currentMap ->
                            currentMap + (postId to CommentsUiState.Success(updatedComments))
                        }
                    }
                    
                    // Actualizar el contador de comentarios en el feed
                    if (_feedState.value is FeedUiState.Success) {
                        val currentPosts = (_feedState.value as FeedUiState.Success).posts
                        val updatedPosts = currentPosts.map { post ->
                            if (post.id == postId) {
                                post.copy(commentsCount = post.commentsCount + 1)
                            } else {
                                post
                            }
                        }
                        _feedState.update { FeedUiState.Success(updatedPosts) }
                    }
                    
                    // Si es una respuesta, actualizar las respuestas
                    if (parentId != null) {
                        loadReplies(token, parentId)
                    }
                }
                .onFailure { error ->
                    // Handle error
                }
        }
    }

    fun loadReplies(token: String, commentId: String) {
        viewModelScope.launch {
            _repliesState.update { RepliesUiState.Loading }
            
            socialRepository.getCommentReplies(token, commentId)
                .onSuccess { replies ->
                    _repliesState.update { RepliesUiState.Success(replies) }
                }
                .onFailure { error ->
                    _repliesState.update { 
                        RepliesUiState.Error(error.message ?: "Error al cargar respuestas") 
                    }
                }
        }
    }

    fun createPost(token: String, content: String, imageUrl: String?, extractionId: String?) {
        viewModelScope.launch {
            socialRepository.createPost(token, content, imageUrl, extractionId)
                .onSuccess { newPost ->
                    // Agregar el nuevo post al principio del feed
                    if (_feedState.value is FeedUiState.Success) {
                        val currentPosts = (_feedState.value as FeedUiState.Success).posts
                        _feedState.update { FeedUiState.Success(listOf(newPost) + currentPosts) }
                    }
                }
                .onFailure { error ->
                    // Manejar error silenciosamente o mostrar un mensaje
                }
        }
    }

    fun resetCommentsState() {
        _commentsStateMap.update { emptyMap() }
        _repliesState.update { RepliesUiState.Idle }
    }
    
    fun resetCommentsForPost(postId: String) {
        _commentsStateMap.update { currentMap ->
            currentMap - postId
        }
    }
    
    // ========== FOLLOW & SEARCH ==========
    
    private val _searchState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchState = _searchState.asStateFlow()
    
    fun searchUsers(token: String, query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _searchState.update { SearchUiState.Idle }
                return@launch
            }
            
            _searchState.update { SearchUiState.Loading }
            
            socialRepository.searchUsers(token, query)
                .onSuccess { users ->
                    _searchState.update { SearchUiState.Success(users) }
                }
                .onFailure { error ->
                    _searchState.update { 
                        SearchUiState.Error(error.message ?: "Error al buscar usuarios") 
                    }
                }
        }
    }
    
    fun toggleFollow(token: String, userId: String) {
        viewModelScope.launch {
            socialRepository.toggleFollow(token, userId)
                .onSuccess { following ->
                    // Actualizar el estado de búsqueda si está activo
                    if (_searchState.value is SearchUiState.Success) {
                        val currentUsers = (_searchState.value as SearchUiState.Success).users
                        val updatedUsers = currentUsers.map { user ->
                            if (user.id == userId) {
                                user.copy(
                                    isFollowing = following,
                                    followersCount = if (following) user.followersCount + 1 else user.followersCount - 1
                                )
                            } else {
                                user
                            }
                        }
                        _searchState.update { SearchUiState.Success(updatedUsers) }
                    }
                }
                .onFailure { error ->
                    // Silently fail or show error
                }
        }
    }
    
    fun loadFollowingFeed(token: String, limit: Int = 20, offset: Int = 0) {
        viewModelScope.launch {
            _feedState.update { FeedUiState.Loading }
            
            socialRepository.getFollowingFeed(token, limit, offset)
                .onSuccess { posts ->
                    val postsWithLikeStatus = posts.map { post ->
                        val isLiked = socialRepository.checkUserLiked(token, post.id)
                            .getOrElse { false }
                        post.copy(isLikedByCurrentUser = isLiked)
                    }
                    _feedState.update { FeedUiState.Success(postsWithLikeStatus) }
                }
                .onFailure { error ->
                    _feedState.update { 
                        FeedUiState.Error(error.message ?: "Error al cargar el feed") 
                    }
                }
        }
    }
    
    // ========== EXTRACTION DATA ==========
    
    private val _extractionDataState = MutableStateFlow<ExtractionDataUiState>(ExtractionDataUiState.Idle)
    val extractionDataState = _extractionDataState.asStateFlow()
    
    fun loadPostExtractionData(token: String, postId: String) {
        viewModelScope.launch {
            _extractionDataState.update { ExtractionDataUiState.Loading }
            
            socialRepository.getPostExtractionData(token, postId)
                .onSuccess { data ->
                    _extractionDataState.update { ExtractionDataUiState.Success(data) }
                }
                .onFailure { error ->
                    _extractionDataState.update { 
                        ExtractionDataUiState.Error(error.message ?: "Error al cargar datos de extracción") 
                    }
                }
        }
    }
    
    fun resetExtractionDataState() {
        _extractionDataState.update { ExtractionDataUiState.Idle }
    }
}
