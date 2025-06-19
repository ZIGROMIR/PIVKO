package com.example.myapplication

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import com.example.myapplication.network.loginAndStoreToken

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Безопасное подключение шрифтов
    val pacificoFont = FontFamily(Font(R.font.pacifico_regular))
    val arimaFont = FontFamily(Font(R.font.arima_regular))

    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Hello :)",
                fontSize = 52.sp,
                fontFamily = pacificoFont,
                lineHeight = 60.sp,
                letterSpacing = 0.01.em,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.25f),
                        offset = Offset(0f, 4f),
                        blurRadius = 4f
                    )
                ),
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp, bottom = 24.dp)
            )

            Text(
                text = "Study",
                fontSize = 36.sp,
                fontFamily = arimaFont,
                style = TextStyle(shadow = Shadow(Color.Black.copy(0.25f), Offset(0f, 4f), 4f)),
                color = Color.Black,
                modifier = Modifier.padding(start = 48.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Do",
                    fontSize = 36.sp,
                    fontFamily = arimaFont,
                    style = TextStyle(shadow = Shadow(Color.Black.copy(0.25f), Offset(0f, 4f), 4f)),
                    color = Color.Black,
                    modifier = Modifier.padding(end = 48.dp)
                )
            }

            Text(
                text = "Plan",
                fontSize = 36.sp,
                fontFamily = arimaFont,
                style = TextStyle(shadow = Shadow(Color.Black.copy(0.25f), Offset(0f, 4f), 4f)),
                color = Color.Black,
                modifier = Modifier.padding(start = 48.dp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(
                value = login,
                onValueChange = { login = it },
                label = { Text("Login") },
                placeholder = { Text("Enter your login") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                placeholder = { Text("••••••••") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Forgot password?",
                    fontSize = 12.sp,
                    color = Color(0xFF6C63FF),
                    modifier = Modifier.clickable { }
                )

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val success = loginAndStoreToken(login, password, context)
                            if (success) {
                                navController.navigate("day") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text("Sign in", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("First time here?", fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("register") },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("Sign up", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("About", fontSize = 12.sp, color = Color.Gray)
                Text("FAQ", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}