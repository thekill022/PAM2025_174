package com.example.finalproject.view

import com.example.finalproject.viewmodel.PenyediaViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalproject.viewmodel.RegisterViewModel
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateUp: () -> Unit,
    viewModel: RegisterViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiState
    val scrollState = rememberScrollState()

    val aktivitasOptions = listOf(
        "Sedentary (Rebahan/Duduk Kerja)",
        "Ringan (Olahraga 1-3 hari/minggu)",
        "Sedang (Olahraga 3-5 hari/minggu)",
        "Berat (Olahraga 6-7 hari/minggu)",
        "Ekstrem (Fisik berat/Atlet)"
    )
    var expanded by remember { mutableStateOf(false) }


    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateUp()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryGradient),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Buat Akun Baru",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Mulai perjalanan sehatmu bersama TrackMe",
                    fontSize = 12.sp,
                    color = TextGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                CustomOutlinedTextField(
                    label = "Nama Lengkap",
                    value = uiState.nama,
                    onValueChange = { viewModel.updateUiState(nama = it) },
                    placeholder = "Nama kamu",
                    icon = Icons.Default.Person,
                    keyboardType = KeyboardType.Text
                )

                CustomOutlinedTextField(
                    label = "Email",
                    value = uiState.email,
                    onValueChange = { viewModel.updateUiState(email = it) },
                    placeholder = "email@contoh.com",
                    icon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )

                var passwordVisible by remember { mutableStateOf(false) }
                CustomOutlinedTextField(
                    label = "Password",
                    value = uiState.password,
                    onValueChange = { viewModel.updateUiState(password = it) },
                    placeholder = "Buat password",
                    icon = Icons.Default.Lock,
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onVisibilityChange = { passwordVisible = !passwordVisible }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CustomOutlinedTextField(
                            label = "Usia (Thn)",
                            value = uiState.usia,
                            onValueChange = { viewModel.updateUiState(usia = it) },
                            placeholder = "20",
                            icon = Icons.Default.DateRange,
                            keyboardType = KeyboardType.Number
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        CustomOutlinedTextField(
                            label = "Berat (Kg)",
                            value = uiState.beratBadan,
                            onValueChange = { viewModel.updateUiState(berat = it) },
                            placeholder = "60",
                            icon = Icons.Default.ShoppingCart,
                            keyboardType = KeyboardType.Number
                        )
                    }
                }

                CustomOutlinedTextField(
                    label = "Tinggi Badan (cm)",
                    value = uiState.tinggiBadan,
                    onValueChange = { viewModel.updateUiState(tinggi = it) },
                    placeholder = "170",
                    icon = Icons.Default.ArrowUpward,
                    keyboardType = KeyboardType.Number
                )


                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Aktivitas Harian",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = uiState.aktivitasHarian,
                            onValueChange = {},
                            readOnly = true, // Supaya user tidak bisa ketik manual
                            placeholder = { Text("Pilih Aktivitas", fontSize = 12.sp, color = Color.LightGray) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(), // Wajib: menandakan ini jangkar menu
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.DirectionsRun, contentDescription = null, tint = Color.Gray)
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedBorderColor = BlueGradient,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            aktivitasOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(text = option, fontSize = 14.sp) },
                                    onClick = {
                                        viewModel.updateUiState(aktivitas = option)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Jenis Kelamin",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { viewModel.updateUiState(gender = "Laki-laki") }
                                .padding(end = 16.dp)
                        ) {
                            RadioButton(
                                selected = uiState.jenis_kelamin == "Laki-laki",
                                onClick = { viewModel.updateUiState(gender = "Laki-laki") },
                                colors = RadioButtonDefaults.colors(selectedColor = BlueGradient)
                            )
                            Text(text = "Laki-laki", fontSize = 14.sp)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { viewModel.updateUiState(gender = "Perempuan") }
                        ) {
                            RadioButton(
                                selected = uiState.jenis_kelamin == "Perempuan",
                                onClick = { viewModel.updateUiState(gender = "Perempuan") },
                                colors = RadioButtonDefaults.colors(selectedColor = BlueGradient)
                            )
                            Text(text = "Perempuan", fontSize = 14.sp)
                        }
                    }
                }

                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.register() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    enabled = !uiState.isLoading
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ButtonGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(text = "Daftar Akun", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Row(
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Sudah punya akun? ", fontSize = 12.sp, color = TextGray)
                    Text(
                        text = "Masuk",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlueGradient,
                        modifier = Modifier.clickable { onNavigateUp() }
                    )
                }
            }
        }
    }
}