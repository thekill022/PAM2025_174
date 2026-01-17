package com.example.finalproject.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalproject.model.meals
import com.example.finalproject.model.DataMealPlan
import com.example.finalproject.ui.view.GreenGradient
import com.example.finalproject.viewmodel.MealPlannerViewModel
import com.example.finalproject.viewmodel.MealUiState
import com.example.finalproject.viewmodel.PenyediaViewModel


val BlueOcean = Color(0xFF00C6FF)
val BlueDeep = Color(0xFF0072FF)
val GradientBlue = Brush.horizontalGradient(listOf(BlueOcean, BlueDeep))
val SuccessGreen = Color(0xFF4CAF50)

@Composable
fun MealPlannerScreen(
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MealPlannerViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current


    LaunchedEffect(viewModel.saveMessage) {
        viewModel.saveMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()


            if (message.contains("Berhasil", ignoreCase = true)) {
                navigateToHome()
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.RestaurantMenu, contentDescription = "Food Log") },
                    label = { Text("Food Log") },
                    selected = false,
                    onClick = { navigateToHome() },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenGradient, indicatorColor = Color.White)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Assignment, contentDescription = "AI Planner") },
                    label = { Text("AI Planner", color = GreenGradient) },
                    selected = true,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenGradient, indicatorColor = Color.White)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = false,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenGradient, indicatorColor = Color.White)
                )
            }
        }
    ) { innerPad ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(innerPad)
                .padding(16.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ceritakan preferensi atau kebutuhan diet Anda",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.promptText,
                onValueChange = { viewModel.updatePrompt(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Contoh: Saya ingin diet rendah karbohidrat sekitar 1500 kalori...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlueDeep,
                    unfocusedBorderColor = Color.LightGray,
                )
            )

            Spacer(modifier = Modifier.height(12.dp))


            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionChip("Diet Low Carb") { viewModel.updatePrompt("Buatkan rencana makan Diet Low Carb") }
                SuggestionChip("Vegetarian") { viewModel.updatePrompt("Buatkan rencana makan Vegetarian") }
                SuggestionChip("Bulking") { viewModel.updatePrompt("Buatkan rencana makan Bulking tinggi protein") }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = { viewModel.generatePlan() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(GradientBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Buat Rekomendasi", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = viewModel.uiState) {
                    is MealUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = BlueDeep
                        )
                    }
                    is MealUiState.Error -> {
                        Text(
                            text = state.message,
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is MealUiState.Success -> {

                        MealResultContent(
                            data = state.data,
                            isSaving = viewModel.isSaving,
                            onSaveClick = { viewModel.savePlanToLog() }
                        )
                    }
                    else -> {

                    }
                }
            }
        }
    }
}

@Composable
fun SuggestionChip(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFE8F5E9))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, color = Color(0xFF2E7D32), fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun MealResultContent(
    data: DataMealPlan,
    isSaving: Boolean,
    onSaveClick: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(GradientBlue)
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Kalori Harian", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                            Text(
                                "${data.totalNutrition.kalori} kcal",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(
                            Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }

        item {
            Text("Rencana Menu:", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(vertical = 4.dp))
        }


        items(data.meals) { meal ->
            AiMealCard(meal)
        }
    }
}

@Composable
fun AiMealCard(meal: meals) {
    val (icon, color) = when {
        meal.kategori.contains("Sarapan", true) -> Pair(Icons.Default.WbSunny, Color(0xFFFF9800))
        meal.kategori.contains("Siang", true) -> Pair(Icons.Default.Restaurant, Color(0xFF4CAF50))
        meal.kategori.contains("Malam", true) -> Pair(Icons.Default.NightsStay, Color(0xFF3F51B5))
        else -> Pair(Icons.Default.LocalCafe, Color(0xFF9C27B0))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(meal.kategori, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Text(
                        "${meal.kalori} kcal",
                        color = Color(0xFFFF5722),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(12.dp))


            Text(
                text = meal.nama,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))


            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Porsi: ${meal.quantity}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))


            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MicroBadge("Protein", "${meal.protein}g")
                MicroBadge("Karbo", "${meal.karbo}g")
                MicroBadge("Lemak", "${meal.lemak}g")
            }
        }
    }
}

@Composable
fun MicroBadge(label: String, value: String) {
    Text(
        text = "• $label $value",
        fontSize = 11.sp,
        color = Color.Gray
    )
}