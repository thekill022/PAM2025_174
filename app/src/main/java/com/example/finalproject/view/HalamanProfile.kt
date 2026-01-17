package com.example.finalproject.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.finalproject.model.DataBerat
import com.example.finalproject.ui.view.BlueGradient
import com.example.finalproject.ui.view.GreenGradient
import com.example.finalproject.ui.view.PrimaryGradient
import com.example.finalproject.viewmodel.PenyediaViewModel
import com.example.finalproject.viewmodel.ProfileViewModel
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.nativeCanvas
import kotlin.math.roundToInt

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    navigateToHome: () -> Unit = {},
    navigateToMealPlan: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiState
    var selectedItem by remember { mutableIntStateOf(2) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.RestaurantMenu, contentDescription = "Food Log") },
                    label = { Text("Food Log") },
                    selected = selectedItem == 0,
                    onClick = { navigateToHome() },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GreenGradient,
                        indicatorColor = Color.White
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Assignment, contentDescription = "AI Planner") },
                    label = { Text("AI Planner") },
                    selected = selectedItem == 1,
                    onClick = { navigateToMealPlan() },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GreenGradient,
                        indicatorColor = Color.White
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Person, contentDescription = "Profile") },
                    label = { Text("Profile", color = if (selectedItem == 2) GreenGradient else Color.Black) },
                    selected = selectedItem == 2,
                    onClick = { selectedItem = 2 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GreenGradient,
                        indicatorColor = Color.White
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Header Section
                item {
                    ProfileHeader(
                        nama = uiState.user.nama,
                        email = uiState.user.email
                    )
                }

                // BMI Card
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    BMICard(
                        bmi = viewModel.calculateBMI(),
                        category = viewModel.getBMICategory(),
                        berat = uiState.user.berat,
                        tinggi = uiState.user.tinggi_badan
                    )
                }

                // Personal Info
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    PersonalInfoCard(
                        usia = uiState.user.usia,
                        jenisKelamin = uiState.user.jenis_kelamin,
                        aktivitas = uiState.user.aktivitas_harian
                    )
                }

                // Logout Button
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    LogoutButton(onClick = { viewModel.showLogoutDialog() })
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Loading Indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BlueGradient)
                }
            }

            // Logout Dialog
            if (uiState.showLogoutDialog) {
                LogoutDialog(
                    onConfirm = { viewModel.logout(onLogout) },
                    onDismiss = { viewModel.hideLogoutDialog() }
                )
            }
        }
    }
}

@Composable
fun ProfileHeader(nama: String, email: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(PrimaryGradient)
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = nama,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = email,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun BMICard(bmi: Float, category: String, berat: Float, tinggi: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = GreenGradient,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Body Mass Index (BMI)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                BMIInfoItem(
                    label = "BMI",
                    value = if (bmi > 0) String.format("%.1f", bmi) else "-"
                )
                BMIInfoItem(
                    label = "Kategori",
                    value = category
                )
                BMIInfoItem(
                    label = "Berat",
                    value = if (berat > 0) "${berat}kg" else "-"
                )
                BMIInfoItem(
                    label = "Tinggi",
                    value = if (tinggi > 0) "${tinggi}cm" else "-"
                )
            }
        }
    }
}

@Composable
fun BMIInfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = GreenGradient
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun PersonalInfoCard(usia: Int, jenisKelamin: String, aktivitas: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = GreenGradient,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Informasi Personal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            InfoRow(icon = Icons.Default.Cake, label = "Usia", value = "$usia tahun")
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.3f))
            
            InfoRow(icon = Icons.Default.Person, label = "Jenis Kelamin", value = jenisKelamin.capitalize())
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.3f))
            
            InfoRow(icon = Icons.Default.DirectionsRun, label = "Aktivitas", value = aktivitas)
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                label,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Red.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            Icons.Default.Logout,
            contentDescription = null,
            tint = Color.Red
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "Keluar dari Akun",
            color = Color.Red,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun LogoutDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Logout,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                "Konfirmasi Logout",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("Apakah Anda yakin ingin keluar dari akun?")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Text("Ya, Keluar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = Color.Gray)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

// Extension function untuk capitalize
fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
