package com.example.hakaton.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hakaton.ui.theme.firstFont
import com.example.hakaton.ui.theme.secondFont
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

private val FieldBlue = Color(0xFFC9EBF9)
private val TextGray = Color(0xFF4E4E4E)
private val ErrorRed = Color(0xFFC0485A)
private val WelcomeDark = Color(0xFF263154)

@Composable
fun ScannerScreen(
    onBack: () -> Unit,
    vm: ScannerViewModel = viewModel()
) {
    val state = vm.state

    // На экранах результата "назад" возвращает к сканеру, на самом сканере уходит на админку
    BackHandler { if (state is ScanState.Idle) onBack() else vm.reset() }

    when (state) {
        is ScanState.Success -> ResultScreen(onBack = vm::reset) {
            Text(
                text = "Добро пожаловать, ${state.name}!", fontSize = 40.sp,
                lineHeight = 48.sp,
                color = WelcomeDark,
                fontFamily = firstFont
            )
        }

        is ScanState.Error -> ResultScreen(onBack = vm::reset) {
            Text(
                text = "Ошибка:",
                fontSize = 40.sp,
                color = ErrorRed,
                fontFamily = firstFont
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = state.message,
                fontSize = 30.sp,
                lineHeight = 36.sp,
                color = ErrorRed,
                fontFamily = firstFont
            )
        }

        else -> ScanForm(vm = vm, onBack = onBack)
    }
}

// ---------- Экран результата (приветствие или ошибка) ----------

@Composable
private fun ResultScreen(onBack: () -> Unit, content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        BackArrow(onBack)
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

// ---------- Экран сканера ----------

@Composable
private fun ScanForm(vm: ScannerViewModel, onBack: () -> Unit) {
    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { hasPermission = it }

    LaunchedEffect(Unit) {
        if (!hasPermission) launcher.launch(Manifest.permission.CAMERA)
    }

    val isLoading = vm.state is ScanState.Loading

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        BackArrow(onBack)
        Spacer(Modifier.height(32.dp))

        // Рамка с камерой
        Box(
            Modifier
                .align(Alignment.CenterHorizontally)
                .size(280.dp),
            contentAlignment = Alignment.Center
        ) {
            if (hasPermission) {
                CameraPreview(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    onCode = vm::submit
                )
            } else {
                Text(
                    "Нет доступа к камере",
                    color = TextGray,
                    fontSize = 14.sp,
                    fontFamily = secondFont,
                    textAlign = TextAlign.Center
                )
            }
            ViewfinderCorners(Modifier.fillMaxSize())
        }

        Spacer(Modifier.height(32.dp))

        // Ручной ввод
        Text(
            "Ввести вручную",
            fontSize = 13.sp,
            color = Color.Black,
            fontFamily = firstFont
        )
        Spacer(Modifier.height(4.dp))
        TextField(
            value = vm.manualCode,
            onValueChange = { vm.manualCode = it },
            placeholder = {
                Text(
                    "Вводите уникальный код",
                    color = TextGray,
                    fontSize = 14.sp,
                    fontFamily = secondFont
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = FieldBlue,
                unfocusedContainerColor = FieldBlue, focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = TextGray,
                unfocusedTextColor = TextGray,
                cursorColor = TextGray
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { vm.submit(vm.manualCode) })
        )

        Spacer(Modifier.height(24.dp))

        // Кнопка отправки
        Button(
            onClick = { vm.submit(vm.manualCode) },
            enabled = !isLoading,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(72.dp),
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FieldBlue,
                disabledContainerColor = FieldBlue
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    Modifier.size(28.dp),
                    color = TextGray,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Проверить",
                    tint = TextGray
                )
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

// ---------- Стрелка назад (как на других экранах) ----------

@Composable
private fun BackArrow(onBack: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Назад",
                tint = Color.Black
            )
        }
    }
}

// ---------- Уголки рамки сканера ----------

@Composable
private fun ViewfinderCorners(modifier: Modifier) {
    Canvas(modifier) {
        val len = 56.dp.toPx()
        val w = 4.dp.toPx()
        val c = Color.Black
        val width = size.width
        val height = size.height

        fun line(x1: Float, y1: Float, x2: Float, y2: Float) =
            drawLine(c, Offset(x1, y1), Offset(x2, y2), strokeWidth = w)

        // левый верхний
        line(0f, 0f, len, 0f); line(0f, 0f, 0f, len)
        // правый верхний
        line(width, 0f, width - len, 0f); line(width, 0f, width, len)
        // левый нижний
        line(0f, height, len, height); line(0f, height, 0f, height - len)
        // правый нижний
        line(width, height, width - len, height); line(width, height, width, height - len)
    }
}

// ---------- Камера + распознавание QR ----------

@Composable
private fun CameraPreview(modifier: Modifier, onCode: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = context as ComponentActivity
    val providerHolder = remember { mutableListOf<ProcessCameraProvider>() }

    DisposableEffect(Unit) {
        onDispose { providerHolder.forEach { it.unbindAll() } }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val providerFuture = ProcessCameraProvider.getInstance(ctx)
            providerFuture.addListener({
                val provider = providerFuture.get()
                providerHolder.add(provider)

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(ContextCompat.getMainExecutor(ctx), QrAnalyzer(onCode))
                    }

                provider.unbindAll()
                provider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis
                )
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        }
    )
}

private class QrAnalyzer(private val onCode: (String) -> Unit) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    )

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val media = imageProxy.image
        if (media == null) {
            imageProxy.close()
            return
        }
        val image = InputImage.fromMediaImage(media, imageProxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener { codes ->
                codes.firstNotNullOfOrNull { it.rawValue }?.let(onCode)
            }
            .addOnCompleteListener { imageProxy.close() }
    }
}