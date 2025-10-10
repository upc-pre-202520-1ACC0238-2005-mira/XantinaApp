package com.upc.xantina.features.conecta.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Publicacion(
    val autor: String,
    val tiempo: String,
    val titulo: String,
    val contenido: String,
    var favoritos: Int,
    val comentarios: MutableList<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConectaScreen(onProfileClick: () -> Unit) {
    var publicaciones by remember {
        mutableStateOf(
            listOf(
                Publicacion(
                    autor = "Ana Martínez",
                    tiempo = "Hace 2 horas",
                    titulo = "V60",
                    contenido = "Colombia Geisha ☕",
                    favoritos = 10,
                    comentarios = mutableListOf("¡Se ve delicioso!", "¿Qué molienda usaste?")
                ),
                Publicacion(
                    autor = "Carlos Ruiz",
                    tiempo = "Hace 5 horas",
                    titulo = "Cold Brew",
                    contenido = "Preparé un cold brew con granos etíopes y salió 🔥",
                    favoritos = 7,
                    comentarios = mutableListOf("Lo intentaré mañana", "Perfecto para el verano 😎")
                ),
                Publicacion(
                    autor = "Lucía Fernández",
                    tiempo = "Hace 1 día",
                    titulo = "Chemex",
                    contenido = "Mi primer intento con Chemex. ¿Algún consejo?",
                    favoritos = 5,
                    comentarios = mutableListOf("Agua a 92°C!", "Filtro bien enjuagado primero!")
                )
            )
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4B2E2E))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Conecta", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Comunidad cafetera", color = Color.White, fontSize = 14.sp)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onProfileClick() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Perfil",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Text("Mi Perfil", color = Color.White, fontSize = 12.sp)
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(publicaciones) { publicacion ->
                PublicacionCard(publicacion)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicacionCard(publicacion: Publicacion) {
    var isFav by remember { mutableStateOf(false) }
    var showComments by remember { mutableStateOf(false) }
    var newComment by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(publicacion.autor, fontWeight = FontWeight.Bold)
                Text(publicacion.tiempo, fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = publicacion.titulo,
                color = Color(0xFF4B2E2E),
                modifier = Modifier
                    .background(Color(0xFFF5E6D3), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(publicacion.contenido)
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    isFav = !isFav
                    if (isFav) publicacion.favoritos++ else publicacion.favoritos--
                }) {
                    Icon(
                        imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (isFav) Color.Red else Color.Gray
                    )
                }
                Text("${publicacion.favoritos}")

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "💬 ${publicacion.comentarios.size}",
                    modifier = Modifier
                        .clickable { showComments = !showComments }
                        .padding(4.dp),
                    color = Color(0xFF4B2E2E)
                )
            }

            if (showComments) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5E6D3), shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    for (comentario in publicacion.comentarios) {
                        Text("• $comentario", fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    OutlinedTextField(
                        value = newComment,
                        onValueChange = { newComment = it },
                        placeholder = { Text("Escribe un comentario...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            if (newComment.isNotBlank()) {
                                publicacion.comentarios.add(newComment)
                                newComment = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4B2E2E)),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Comentar", color = Color.White)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewConectaScreen() {
    ConectaScreen(onProfileClick = {})
}
