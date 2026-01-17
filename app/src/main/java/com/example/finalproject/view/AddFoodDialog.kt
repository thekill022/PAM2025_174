package com.example.finalproject.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.ui.view.BlueGradient
import com.example.finalproject.ui.view.GreenGradient

@Composable
fun AddFoodDialog(
    category: String,
    foodInputText: String,
    isLoading: Boolean,
    calorieData: com.example.finalproject.model.FoodCalorieData?,
    message: String?,
    onFoodInputChange: (String) -> Unit,
    onGetCalories: () -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Tambah Makanan - $category",
                fontWeight = FontWeight.Bold,
                color = GreenGradient
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Input field
                OutlinedTextField(
                    value = foodInputText,
                    onValueChange = onFoodInputChange,
                    label = { Text("Nama Makanan") },
                    placeholder = { Text("Contoh: 100 gram dada ayam") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = false,
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenGradient,
                        focusedLabelColor = GreenGradient
                    )
                )

                // Button untuk get calories
                if (calorieData == null) {
                    Button(
                        onClick = onGetCalories,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading && foodInputText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BlueGradient
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Menganalisis...")
                        } else {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cari Info Kalori")
                        }
                    }
                }

                // Display calorie data
                if (calorieData != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF0F9FF)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Informasi Nutrisi",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = BlueGradient
                            )

                            calorieData.foodItems.forEach { item ->
                                Divider(color = Color.LightGray.copy(alpha = 0.5f))
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        item.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        "Porsi: ${item.quantity}",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Kalori:", fontSize = 11.sp, color = Color.Gray)
                                        Text("${item.calories} kcal", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("P: ${item.protein.toInt()}g", fontSize = 10.sp)
                                        Text("K: ${item.carbohydrates.toInt()}g", fontSize = 10.sp)
                                        Text("L: ${item.fat.toInt()}g", fontSize = 10.sp)
                                        Text("S: ${item.fiber.toInt()}g", fontSize = 10.sp)
                                    }
                                }
                            }

                            Divider(color = BlueGradient.copy(alpha = 0.3f), thickness = 2.dp)
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Total Kalori:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    "${calorieData.total.calories} kcal",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = GreenGradient
                                )
                            }
                        }
                    }
                }

                // Message
                if (message != null) {
                    Text(
                        text = message,
                        fontSize = 12.sp,
                        color = if (message.contains("berhasil", ignoreCase = true)) 
                            GreenGradient else Color.Red,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            if (calorieData != null) {
                Button(
                    onClick = onSave,
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenGradient
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Simpan")
                }
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
