package com.upc.xantina.features.social.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.upc.xantina.core.domain.repository.AuthRepository
import com.upc.xantina.features.social.domain.model.Post
import com.upc.xantina.features.social.domain.model.Comment
import com.upc.xantina.features.social.presentation.state.CommentsUiState
import com.upc.xantina.features.social.presentation.state.FeedUiState
import com.upc.xantina.features.social.presentation.state.RepliesUiState
import com.upc.xantina.features.social.presentation.viewmodel.SocialViewModel
import com.upc.xantina.shared.ui.components.XantinaButton
import com.upc.xantina.shared.ui.components.XantinaTextField
import com.upc.xantina.shared.ui.theme.XantinaPrimary
import com.upc.xantina.shared.ui.theme.XantinaTextSecondary
import kotlinx.coroutines.launch

@Composable
fun SocialFeedScreen(
    authRepository: AuthRepository,
    viewModel: SocialViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val feedState by viewModel.feedState.collectAsState()
    
    var selectedPost by remember { mutableStateOf<Post?>(null) }
    
    LaunchedEffect(Unit) {
        scope.launch {
            val token = authRepository.getAuthToken()
            if (token != null) {
                viewModel.loadFeed(token)
            }
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = feedState) {
            is FeedUiState.Idle -> {
                // Estado inicial
            }
            is FeedUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = XantinaPrimary
                )
            }
            is FeedUiState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        scope.launch {
                            val token = authRepository.getAuthToken()
                            if (token != null) {
                                viewModel.loadFeed(token, refresh = true)
                            }
                        }
                    }) {
                        Text("Reintentar")
                    }
                }
            }
            is FeedUiState.Success -> {
                if (state.posts.isEmpty()) {
                    Text(
                        text = "No hay publicaciones aún",
                        modifier = Modifier.align(Alignment.Center),
                        color = XantinaTextSecondary
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(state.posts) { post ->
                            PostCard(
                                post = post,
                                onLike = {
                                    scope.launch {
                                        val token = authRepository.getAuthToken()
                                        if (token != null) {
                                            viewModel.toggleLike(token, post.id)
                                        }
                                    }
                                },
                                onComment = {
                                    selectedPost = post
                                },
                                authRepository = authRepository
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
    
    // Diálogo de comentarios
    selectedPost?.let { post ->
        CommentsDialog(
            post = post,
            authRepository = authRepository,
            viewModel = viewModel,
            onDismiss = { selectedPost = null }
        )
    }
}

@Composable
fun PostCard(
    post: Post,
    onLike: () -> Unit,
    onComment: () -> Unit,
    authRepository: AuthRepository
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Avatar + Nombre + Fecha
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(XantinaPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.userName.firstOrNull()?.uppercase() ?: "U",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.userName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = formatDate(post.createdAt),
                        fontSize = 12.sp,
                        color = XantinaTextSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Contenido
            Text(
                text = post.content,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
            
            // Imagen (si existe)
            post.imageUrl?.let { imageUrl ->
                Spacer(modifier = Modifier.height(12.dp))
                // Aquí podrías usar Coil para cargar la imagen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Imagen: $imageUrl", fontSize = 12.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botones: Like y Comentar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Like
                Row(
                    modifier = Modifier
                        .clickable(onClick = onLike)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (post.isLikedByCurrentUser) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Filled.FavoriteBorder
                        },
                        contentDescription = "Like",
                        tint = if (post.isLikedByCurrentUser) Color.Red else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.likesCount}",
                        fontSize = 14.sp,
                        color = if (post.isLikedByCurrentUser) Color.Red else Color.Gray
                    )
                }
                
                // Botón Comentar
                Row(
                    modifier = Modifier
                        .clickable(onClick = onComment)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.MailOutline,
                        contentDescription = "Comentar",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.commentsCount}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun CommentsDialog(
    post: Post,
    authRepository: AuthRepository,
    viewModel: SocialViewModel,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val commentsState by viewModel.commentsState.collectAsState()
    var commentText by remember { mutableStateOf("") }
    var selectedComment by remember { mutableStateOf<Comment?>(null) }
    
    LaunchedEffect(Unit) {
        scope.launch {
            val token = authRepository.getAuthToken()
            if (token != null) {
                viewModel.loadComments(token, post.id)
            }
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Comentarios (${post.commentsCount})") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                when (val state = commentsState) {
                    is CommentsUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is CommentsUiState.Error -> {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                    }
                    is CommentsUiState.Success -> {
                        if (state.comments.isEmpty()) {
                            Text("No hay comentarios aún", color = XantinaTextSecondary)
                        } else {
                            LazyColumn {
                                items(state.comments) { comment ->
                                    CommentItem(
                                        comment = comment,
                                        authRepository = authRepository,
                                        viewModel = viewModel,
                                        onReply = { selectedComment = comment }
                                    )
                                }
                            }
                        }
                    }
                    CommentsUiState.Idle -> {}
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Campo para nuevo comentario
                selectedComment?.let { comment ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Respondiendo a ${comment.userName}",
                            fontSize = 12.sp,
                            color = XantinaPrimary
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = { selectedComment = null }) {
                            Text("Cancelar", fontSize = 12.sp)
                        }
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    XantinaTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        label = if (selectedComment != null) "Responder" else "Comentar",
                        placeholder = "Escribe tu ${if (selectedComment != null) "respuesta" else "comentario"}...",
                        modifier = Modifier.weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                scope.launch {
                                    val token = authRepository.getAuthToken()
                                    if (token != null) {
                                        viewModel.createComment(
                                            token,
                                            post.id,
                                            commentText,
                                            selectedComment?.id
                                        )
                                        commentText = ""
                                        selectedComment = null
                                    }
                                }
                            }
                        },
                        enabled = commentText.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Enviar",
                            tint = if (commentText.isNotBlank()) XantinaPrimary else Color.Gray
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun CommentItem(
    comment: Comment,
    authRepository: AuthRepository,
    viewModel: SocialViewModel,
    onReply: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val repliesState by viewModel.repliesState.collectAsState()
    var showReplies by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(XantinaPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = comment.userName.firstOrNull()?.uppercase() ?: "U",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = comment.userName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = comment.content,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
                
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatDate(comment.createdAt),
                        fontSize = 11.sp,
                        color = XantinaTextSecondary
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = "Responder",
                        fontSize = 12.sp,
                        color = XantinaPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = onReply)
                    )
                    
                    if (comment.repliesCount > 0) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (showReplies) "Ocultar respuestas" else "Ver ${comment.repliesCount} respuestas",
                            fontSize = 12.sp,
                            color = XantinaPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                showReplies = !showReplies
                                if (showReplies) {
                                    scope.launch {
                                        val token = authRepository.getAuthToken()
                                        if (token != null) {
                                            viewModel.loadReplies(token, comment.id)
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
                
                // Mostrar respuestas
                if (showReplies && repliesState is RepliesUiState.Success) {
                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 8.dp)
                            .fillMaxWidth()
                    ) {
                        (repliesState as RepliesUiState.Success).replies.forEach { reply ->
                            ReplyItem(reply)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReplyItem(reply: com.upc.xantina.features.social.domain.model.Comment) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(XantinaPrimary.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = reply.userName.firstOrNull()?.uppercase() ?: "U",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.width(6.dp))
        
        Column {
            Text(
                text = reply.userName,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                text = reply.content,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
            Text(
                text = formatDate(reply.createdAt),
                fontSize = 10.sp,
                color = XantinaTextSecondary
            )
        }
    }
}

fun formatDate(dateString: String): String {
    // Implementación simple - puedes mejorarla con una librería de fechas
    return try {
        val date = dateString.take(10) // YYYY-MM-DD
        date
    } catch (e: Exception) {
        "Hace un momento"
    }
}

