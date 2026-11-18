package com.example.hakaton.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hakaton.ui.theme.firstFont

@Composable
fun RoleButton(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(80.dp).clip(CircleShape).background(Color(0xFFC9EBF9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, Modifier.size(44.dp), tint = Color.Black)
        }
        Spacer(Modifier.height(8.dp))
        Text(text, color = Color.Black, fontSize = 12.sp, lineHeight = 16.sp,
            textAlign = TextAlign.Center, fontFamily = firstFont)
    }
}