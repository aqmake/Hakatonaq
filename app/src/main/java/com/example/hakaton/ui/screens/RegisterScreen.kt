package com.example.hakaton.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hakaton.ui.nav.NavState
import com.example.hakaton.ui.theme.firstFont
import com.example.hakaton.ui.theme.secondFont
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.draw.blur
import androidx.compose.ui.tooling.preview.Preview
import com.example.hakaton.data.DATE_ONLY_FORMAT
import com.example.hakaton.data.DATE_TIME_FORMAT
import com.example.hakaton.data.Excursion
import java.time.LocalTime
import java.time.Instant
import java.time.ZoneOffset


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RegisterScreen(
    excursion: Excursion,
    vm: RegisterViewModel = viewModel(),
    onNextScreen: (NavState) -> Unit,
    onGroupCreated: (Long) -> Unit,
    onBack: () -> Unit
) {
    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color(0xFFC9EBF9),
        unfocusedContainerColor = Color(0xFFC9EBF9),
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedTextColor = Color(0xFF4E4E4E),
        unfocusedTextColor = Color(0xFF4E4E4E),
        errorTextColor = Color(0xFF4E4E4E),
        cursorColor = Color(0xFF4E4E4E)
    )
    val fieldShape = RoundedCornerShape(12.dp)
    val fieldModifier = Modifier
        .fillMaxWidth()
        .heightIn(min = 56.dp)

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var pickedDate by remember { mutableStateOf<LocalDate?>(null) }

    Box(Modifier.fillMaxSize()) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(8.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад", tint = Color.Black)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Добро\nпожаловать!",
                color = Color(0xff000000),
                fontSize = 36.sp,
                lineHeight = 42.sp,
                fontFamily = firstFont
            )
            Spacer(Modifier.height(20.dp))
            Text(
                "Зарегистрируйте группу",
                color = Color(0xff000000),
                fontSize = 20.sp,
                fontFamily = firstFont
            )
            Spacer(Modifier.height(13.dp))

            TextField(
                value = vm.school,
                onValueChange = { vm.school = it },
                placeholder = {
                    Text(
                        "введите название школы",
                        color = Color(0xff4E4E4E),
                        fontSize = 14.sp,
                        fontFamily = secondFont
                    )
                },
                modifier = fieldModifier,
                shape = fieldShape,
                colors = fieldColors,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            Spacer(Modifier.height(13.dp))
            TextField(
                value = excursion.startsAt.format(DATE_ONLY_FORMAT),
                onValueChange = {},
                readOnly = true,
                label = { Text(excursion.title, fontFamily = secondFont, fontSize = 12.sp) },
                modifier = fieldModifier,
                shape = fieldShape,
                colors = fieldColors,
                singleLine = true
            )
            Spacer(Modifier.height(13.dp))
            TextField(
                value = vm.phone,
                onValueChange = { vm.phone = it },
                placeholder = {
                    Text(
                        "введите номер телефона",
                        color = Color(0xff4E4E4E),
                        fontSize = 14.sp,
                        fontFamily = secondFont
                    )
                },
                modifier = fieldModifier,
                shape = fieldShape,
                colors = fieldColors,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(Modifier.height(17.dp))

            Button(
                onClick = { vm.send(excursion) { id -> onGroupCreated(id) } },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(50.dp),
                contentPadding = PaddingValues(horizontal = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF159BD2))
            ) {
                Text(
                    "Добавить группу",
                    color = Color(0xffFFFFFF),
                    fontSize = 20.sp,
                    maxLines = 1,
                    fontFamily = firstFont
                )
            }

            if (vm.status.isNotEmpty()) {
                Text(
                    vm.status,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp)
                )
            }


            Spacer(Modifier.height(78.dp))
        }
    }
}



