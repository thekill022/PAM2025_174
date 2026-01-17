package com.example.finalproject.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalproject.viewmodel.PenyediaViewModel
import com.example.finalproject.viewmodel.HomeViewModel

val GreenGradient = Color(0xFF00C9A7)
val BlueGradient = Color(0xFF005BEA)
val PrimaryGradient = Brush.verticalGradient(listOf(GreenGradient, BlueGradient))
val BackgroundColor = Color(0xFFF5F5F5)

@Composable
fun HomeScreen(
    navigateToItemEntry: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedItem by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.RestaurantMenu, contentDescription = "Food Log") },
                    label = { Text("Food Log", color = if (selectedItem == 0) GreenGradient else Color.Black) },
                    selected = selectedItem == 0,
                    onClick = { selectedItem = 0 },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenGradient, indicatorColor = Color.White)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Assignment, contentDescription = "AI Planner") },
                    label = { Text("AI Planner", color = if (selectedItem == 0) GreenGradient else Color.Black) },
                    selected = selectedItem == 1,
                    onClick = { selectedItem = 1 },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenGradient, indicatorColor = Color.White)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Person, contentDescription = "Profile") },
                    label = { Text("Profile", color = if (selectedItem == 0) GreenGradient else Color.Black) },
                    selected = selectedItem == 2,
                    onClick = { selectedItem = 2 },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenGradient, indicatorColor = Color.White)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(innerPadding)
        ) {
            HeaderSection(uiState, viewModel)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item {
                    val totalSarapan = uiState.listSarapan.sumOf { it.kalori * it.quantity }
                    MealCard("Sarapan", totalSarapan, Icons.Default.Coffee, navigateToItemEntry)
                }

                item {
                    val totalSiang = uiState.listSiang.sumOf { it.kalori * it.quantity }
                    MealCard("Makan Siang", totalSiang, Icons.Default.Restaurant, navigateToItemEntry)
                }

                item {
                    val totalMalam = uiState.listMalam.sumOf { it.kalori * it.quantity }
                    MealCard("Makan Malam", totalMalam, Icons.Default.NightsStay, navigateToItemEntry)
                }

                item {
                    val totalSnack = uiState.listSnack.sumOf { it.kalori * it.quantity }
                    MealCard("Snack / Lainnya", totalSnack, Icons.Default.Nightlife, navigateToItemEntry)
                }
            }
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BlueGradient)
            }
        }
    }
}

@Composable
fun HeaderSection(
    uiState: com.example.finalproject.viewmodel.HomeUiState,
    viewModel: HomeViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(PrimaryGradient)
            .padding(24.dp)
    ) {
        Column {
            Text(
                text = "Halo, ${uiState.nama}! 👋",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Mari catat makananmu hari ini",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.2f)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { viewModel.changeDate(-1) }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Prev", tint = Color.White)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = viewModel.getDateString(),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
                IconButton(onClick = { viewModel.changeDate(1) }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Kalori Hari Ini", color = Color.White, fontWeight = FontWeight.Medium)
                Text(
                    "${uiState.kaloriTerisi} / ${uiState.targetKalori} kcal",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4. Progress Bar
            val progress = if (uiState.targetKalori > 0) {
                uiState.kaloriTerisi.toFloat() / uiState.targetKalori
            } else 0f

            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Macro Nutrients
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MacroItem(label = "Protein", value = "${uiState.protein}g")
                MacroItem(label = "Karbo", value = "${uiState.karbo}g")
                MacroItem(label = "Lemak", value = "${uiState.lemak}g")
            }
        }
    }
}

// ... MacroItem dan MealCard TETAP SAMA seperti sebelumnya ...
@Composable
fun MacroItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun MealCard(title: String, totalCalories: Int, icon: ImageVector, onAddClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFE0F2F1)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = GreenGradient, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Text("$totalCalories kcal", color = Color.Gray, fontSize = 14.sp)
                }
            }
            IconButton(
                onClick = onAddClick,
                modifier = Modifier.clip(CircleShape).background(Color(0xFFF0F0F0)).size(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = BlueGradient, modifier = Modifier.size(20.dp))
            }
        }
    }
}