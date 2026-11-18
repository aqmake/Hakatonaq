package com.example.hakaton.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hakaton.ui.theme.firstFont
import com.example.hakaton.ui.theme.secondFont

@Composable
fun RegisterViaCode(
    vm: CodeViewModel = viewModel(),
    onParticipant: () -> Unit,
    onAdmin: () -> Unit,
    onBack: () -> Unit
) {

    BackHandler { onBack() }

    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color(0xFFC9EBF9),
        unfocusedContainerColor = Color(0xFFC9EBF9),
        errorContainerColor = Color(0xFFC9EBF9),
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        focusedTextColor = Color(0xFF4E4E4E),
        unfocusedTextColor = Color(0xFF4E4E4E),
        errorTextColor = Color(0xFF4E4E4E),
        cursorColor = Color(0xFF4E4E4E)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Назад",
                tint = Color.Black
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Введи код",
                color = Color(0xff000000),
                fontSize = 42.sp,
                fontFamily = firstFont
            )
            Spacer(Modifier.height(80.dp))

            Text(
                "Введите уникальный код",
                color = Color(0xff000000),
                fontSize = 13.sp,
                fontFamily = firstFont
            )
            Spacer(Modifier.height(6.dp))

            TextField(
                value = vm.code,
                onValueChange = { vm.code = it },
                placeholder = {
                    Text(
                        "например, KV-2020-0017",
                        color = Color(0xff4E4E4E),
                        fontSize = 14.sp,
                        fontFamily = secondFont
                    )
                },
                isError = vm.error.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            if (vm.error.isNotEmpty()) {
                Text(
                    vm.error,
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { vm.check(onParticipant, onAdmin) },
                enabled = !vm.loading,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(50.dp),
                contentPadding = PaddingValues(horizontal = 48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F96E0))
            ) {
                if (vm.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Войти",
                        color = Color.White,
                        fontSize = 22.sp,
                        maxLines = 1,
                        fontFamily = firstFont
                    )
                }
            }
        }
    }
}