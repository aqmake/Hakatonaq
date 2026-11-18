package com.example.hakaton.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

fun generateQr(text: String, size: Int = 512): Bitmap {
    val matrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
    val black = 0xFF000000.toInt()
    val white = 0xFFFFFFFF.toInt()
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap.setPixel(x, y, if (matrix[x, y]) black else white)
        }
    }
    return bitmap
}

@Composable
fun QrCode(text: String, modifier: Modifier = Modifier) {
    val bitmap = remember(text) { generateQr(text) }
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "QR-код",
        modifier = modifier
    )
}