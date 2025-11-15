package com.upc.xantina.features.conecta.ui

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
import com.upc.xantina.features.social.domain.model.PostExtractionData
import com.upc.xantina.features.social.presentation.state.SearchUiState
import com.upc.xantina.features.social.presentation.viewmodel.SocialViewModel
import com.upc.xantina.features.social.ui.SocialFeedScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConectaScreen(
    authRepository: AuthRepository,
    onFollowRecipe: (PostExtractionData) -> Unit = {},
    viewModel: SocialViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val searchState by viewModel.searchState.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Header con buscador
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4B2E2E))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Conecta",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Comunidad cafetera",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
                
                // Botón de búsqueda
                IconButton(
                    onClick = { showSearch = !showSearch },
                    modifier = Modifier.background(
                        Color.White.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
                ) {
                    Icon(
                        imageVector = if (showSearch) Icons.Filled.Close else Icons.Filled.Search,
                        contentDescription = "Buscar",
                        tint = Color.White
                    )
                }
            }
            
            // Barra de búsqueda
            if (showSearch) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { 
                            searchQuery = it
                            scope.launch {
                                val token = authRepository.getAuthToken()
                                if (token != null && it.isNotBlank()) {
                                    viewModel.searchUsers(token, it)
                                } else if (it.isBlank()) {
                                    viewModel.searchUsers(token ?: "", "")
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Buscar usuarios o cafeterías...") },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = "Buscar")
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { 
                                    searchQuery = ""
                                    scope.launch {
                                        val token = authRepository.getAuthToken()
                                        if (token != null) {
                                            viewModel.searchUsers(token, "")
                                        }
                                    }
                                }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Limpiar")
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color(0xFF6F4E37),
                            unfocusedIndicatorColor = Color.Gray
                        ),
                        singleLine = true
                    )
                }
            }
        }
        
        // Resultados de búsqueda o Feed
        if (showSearch && searchQuery.isNotBlank()) {
            // Mostrar resultados de búsqueda
            when (val state = searchState) {
                is SearchUiState.Idle -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Escribe para buscar usuarios o cafeterías",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
                is SearchUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF4B2E2E))
                    }
                }
                is SearchUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                state.message,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                is SearchUiState.Success -> {
                    if (state.users.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No se encontraron resultados",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(state.users) { user ->
                                UserSearchResultItem(
                                    user = user,
                                    onFollowClick = {
                                        scope.launch {
                                            val token = authRepository.getAuthToken()
                                            if (token != null) {
                                                viewModel.toggleFollow(token, user.id)
                                            }
                                        }
                                    }
                                )
                                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }
                }
            }
        } else {
            // Feed Social (solo usuarios seguidos)
            SocialFeedScreen(
                authRepository = authRepository,
                onFollowRecipe = onFollowRecipe,
                useFollowingFeed = true
            )
        }
    }
}

@Composable
fun UserSearchResultItem(
    user: com.upc.xantina.features.social.domain.model.UserSearchResult,
    onFollowClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF4B2E2E)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.name.firstOrNull()?.uppercase() ?: "U",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Información del usuario
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color(0xFF2C1810)
            )
            Text(
                text = user.email,
                fontSize = 13.sp,
                color = Color.Gray
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${user.followersCount} seguidores",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                if (user.role == "cafe") {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "☕ Cafetería",
                        fontSize = 12.sp,
                        color = Color(0xFF6F4E37),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        // Botón seguir/dejar de seguir
        Button(
            onClick = onFollowClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (user.isFollowing) Color.Gray else Color(0xFF4B2E2E)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = if (user.isFollowing) "Siguiendo" else "Seguir",
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}
