package com.upc.xantina.features.social.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upc.xantina.features.social.domain.repository.SocialRepository
import com.upc.xantina.features.social.presentation.state.CommentsUiState
import com.upc.xantina.features.social.presentation.state.FeedUiState
import com.upc.xantina.features.social.presentation.state.RepliesUiState
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

    private val _commentsState = MutableStateFlow<CommentsUiState>(CommentsUiState.Idle)
    val commentsState = _commentsState.asStateFlow()

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
            _commentsState.update { CommentsUiState.Loading }
            
            socialRepository.getPostComments(token, postId)
                .onSuccess { comments ->
                    _commentsState.update { CommentsUiState.Success(comments) }
                }
                .onFailure { error ->
                    _commentsState.update { 
                        CommentsUiState.Error(error.message ?: "Error al cargar comentarios") 
                    }
                }
        }
    }

    fun createComment(token: String, postId: String, content: String, parentId: String? = null) {
        viewModelScope.launch {
            socialRepository.createComment(token, postId, content, parentId)
                .onSuccess { newComment ->
                    // Actualizar la lista de comentarios
                    if (_commentsState.value is CommentsUiState.Success) {
                        val currentComments = (_commentsState.value as CommentsUiState.Success).comments
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
                        _commentsState.update { CommentsUiState.Success(updatedComments) }
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

    fun resetCommentsState() {
        _commentsState.update { CommentsUiState.Idle }
        _repliesState.update { RepliesUiState.Idle }
    }
}
