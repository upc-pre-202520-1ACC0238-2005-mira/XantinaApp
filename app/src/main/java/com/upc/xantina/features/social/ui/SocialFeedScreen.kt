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
import androidx.compose.material3.Divider
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
import com.upc.xantina.features.social.domain.model.PostExtractionData
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
    viewModel: SocialViewModel = hiltViewModel(),
    onFollowRecipe: (PostExtractionData) -> Unit = {},
    useFollowingFeed: Boolean = false
) {
    val scope = rememberCoroutineScope()
    val feedState by viewModel.feedState.collectAsState()
    val extractionDataState by viewModel.extractionDataState.collectAsState()
    
    LaunchedEffect(Unit) {
        scope.launch {
            val token = authRepository.getAuthToken()
            if (token != null) {
                if (useFollowingFeed) {
                    viewModel.loadFollowingFeed(token)
                } else {
                    viewModel.loadFeed(token)
                }
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
                                onComment = {}, // No se usa más el modal
                                authRepository = authRepository,
                                onFollowRecipe = onFollowRecipe
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    onLike: () -> Unit,
    onComment: () -> Unit,
    authRepository: AuthRepository,
    viewModel: SocialViewModel = hiltViewModel(),
    onFollowRecipe: (PostExtractionData) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val commentsStateMap by viewModel.commentsStateMap.collectAsState()
    val commentsState = commentsStateMap[post.id] ?: CommentsUiState.Idle
    val extractionDataState by viewModel.extractionDataState.collectAsState()
    var showAllComments by remember { mutableStateOf(false) }
    var showCommentInput by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<Comment?>(null) }
    
    LaunchedEffect(post.id) {
        scope.launch {
            val token = authRepository.getAuthToken()
            if (token != null && post.commentsCount > 0) {
                viewModel.loadComments(token, post.id)
            }
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4B2E2E)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.userName.firstOrNull()?.uppercase() ?: "U",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.userName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = Color(0xFF2C1810)
                    )
                    Text(
                        text = formatDate(post.createdAt),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Contenido
            Text(
                text = post.content,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = Color(0xFF3C3C3C)
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
            
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.LightGray.copy(alpha = 0.5f)
            )
            
            // Botones: Like, Comentar y Seguir Receta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
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
                        .clickable { showCommentInput = !showCommentInput }
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
            
            // Botón "Seguir receta" si el post tiene extractionId
            post.extractionId?.let { extractionId ->
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        scope.launch {
                            val token = authRepository.getAuthToken()
                            if (token != null) {
                                viewModel.loadPostExtractionData(token, post.id)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6F4E37)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Seguir receta",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Seguir receta",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
                
                // Mostrar datos de extracción cuando se carguen
                when (val state = extractionDataState) {
                    is com.upc.xantina.features.social.presentation.state.ExtractionDataUiState.Success -> {
                        if (state.data.recetaId == extractionId || state.data.recetaId == null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            LaunchedEffect(state.data) {
                                onFollowRecipe(state.data)
                                // Resetear el estado después de pasar los datos
                                viewModel.resetExtractionDataState()
                            }
                        }
                    }
                    else -> {}
                }
            }
            
            // Sección de comentarios
            if (commentsState is CommentsUiState.Success) {
                val comments = (commentsState as CommentsUiState.Success).comments
                val commentsToShow = if (showAllComments) comments else comments.take(3)
                
                if (comments.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = Color.LightGray.copy(alpha = 0.3f))
                    
                    commentsToShow.forEach { comment ->
                        CommentItemInline(
                            comment = comment,
                            onReply = { 
                                replyingTo = comment
                                showCommentInput = true
                            },
                            authRepository = authRepository,
                            viewModel = viewModel
                        )
                    }
                    
                    if (comments.size > 3 && !showAllComments) {
                        TextButton(
                            onClick = { showAllComments = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Ver ${comments.size - 3} comentarios más",
                                color = Color(0xFF4B2E2E),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
            
            // Input para comentar
            if (showCommentInput) {
                Divider(color = Color.LightGray.copy(alpha = 0.3f))
                
                replyingTo?.let { replying ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Respondiendo a ${replying.userName}",
                            fontSize = 12.sp,
                            color = Color(0xFF4B2E2E),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { replyingTo = null }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Cancelar",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { 
                            Text(
                                if (replyingTo != null) "Escribe una respuesta..." 
                                else "Escribe un comentario...",
                                fontSize = 14.sp
                            ) 
                        },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        singleLine = true
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
                                            replyingTo?.id
                                        )
                                        commentText = ""
                                        replyingTo = null
                                    }
                                }
                            }
                        },
                        enabled = commentText.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Enviar",
                            tint = if (commentText.isNotBlank()) Color(0xFF4B2E2E) else Color.Gray
                        )
                    }
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
    val commentsStateMap by viewModel.commentsStateMap.collectAsState()
    val commentsState = commentsStateMap[post.id] ?: CommentsUiState.Idle
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

@Composable
fun CommentItemInline(
    comment: Comment,
    onReply: () -> Unit,
    authRepository: AuthRepository,
    viewModel: SocialViewModel
) {
    val scope = rememberCoroutineScope()
    val repliesState by viewModel.repliesState.collectAsState()
    var showReplies by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4B2E2E)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = comment.userName.firstOrNull()?.uppercase() ?: "U",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = comment.userName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF2C1810)
                )
                Text(
                    text = comment.content,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFF3C3C3C)
                )
                
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatDate(comment.createdAt),
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = "Responder",
                        fontSize = 12.sp,
                        color = Color(0xFF4B2E2E),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable(onClick = onReply)
                    )
                    
                    if (comment.repliesCount > 0) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (showReplies) "Ocultar respuestas" else "${comment.repliesCount} respuestas",
                            fontSize = 12.sp,
                            color = Color(0xFF4B2E2E),
                            fontWeight = FontWeight.Medium,
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
                    val replies = (repliesState as RepliesUiState.Success).replies
                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 8.dp)
                            .fillMaxWidth()
                    ) {
                        replies.forEach { reply ->
                            ReplyItemInline(reply)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReplyItemInline(reply: Comment) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFF4B2E2E).copy(alpha = 0.7f)),
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
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = Color(0xFF2C1810)
            )
            Text(
                text = reply.content,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                color = Color(0xFF3C3C3C)
            )
            Text(
                text = formatDate(reply.createdAt),
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 2.dp)
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

