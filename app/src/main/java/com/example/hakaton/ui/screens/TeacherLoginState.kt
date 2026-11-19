import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hakaton.data.ApiClient
import com.example.hakaton.data.DATE_ONLY_FORMAT
import com.example.hakaton.data.GroupResponse
import com.example.hakaton.ui.theme.firstFont
import com.example.hakaton.ui.theme.secondFont
import kotlinx.coroutines.launch

sealed interface TeacherState {
    object Idle : TeacherState
    object Loading : TeacherState
    data class Success(val groups: List<GroupResponse>) : TeacherState
    data class Error(val message: String) : TeacherState
}

class TeacherLoginViewModel : ViewModel() {
    var phone by mutableStateOf("")
    var state by mutableStateOf<TeacherState>(TeacherState.Idle)
        private set

    fun login() {
        val p = phone.trim()
        if (p.isBlank() || state is TeacherState.Loading) return
        state = TeacherState.Loading
        viewModelScope.launch {
            state = try {
                val r = ApiClient.api.getGroups(p)
                val body = r.body()
                when {
                    !r.isSuccessful -> TeacherState.Error("Ошибка: ${r.code()}")
                    body.isNullOrEmpty() -> TeacherState.Error("Группы с таким номером не найдены")
                    else -> TeacherState.Success(body)
                }
            } catch (e: Exception) {
                TeacherState.Error("Нет связи с сервером")
            }
        }
    }
}

private val FieldBlue = Color(0xFFC9EBF9)
private val TextGray = Color(0xFF4E4E4E)
private val ErrorRed = Color(0xFFC0485A)

@Composable
fun TeacherLoginScreen(
    onBack: () -> Unit,
    onGroup: (Long) -> Unit,
    vm: TeacherLoginViewModel = viewModel()
) {
    BackHandler { onBack() }
    val state = vm.state

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .padding(horizontal = 24.dp)
    ) {
        Box(Modifier.fillMaxWidth().padding(top = 8.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад", tint = Color.Black)
            }
        }
        Spacer(Modifier.height(32.dp))

        Text("Войти как учитель", fontSize = 22.sp, color = Color.Black, fontFamily = firstFont)
        Spacer(Modifier.height(16.dp))
        Text("Номер телефона", fontSize = 13.sp, color = Color.Black, fontFamily = firstFont)
        Spacer(Modifier.height(4.dp))
        TextField(
            value = vm.phone,
            onValueChange = { vm.phone = it },
            placeholder = { Text("Введите номер", color = TextGray, fontSize = 14.sp, fontFamily = secondFont) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = FieldBlue,
                unfocusedContainerColor = FieldBlue,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = TextGray,
                unfocusedTextColor = TextGray,
                cursorColor = TextGray
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { vm.login() })
        )
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { vm.login() },
            enabled = state !is TeacherState.Loading,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3AA0E0),
                disabledContainerColor = Color(0xFF3AA0E0)
            )
        ) {
            if (state is TeacherState.Loading) {
                CircularProgressIndicator(Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("Войти", color = Color.White, fontSize = 16.sp, fontFamily = firstFont)
            }
        }
        Spacer(Modifier.height(24.dp))

        when (state) {is TeacherState.Error -> Text(state.message, color = ErrorRed, fontSize = 16.sp, fontFamily = firstFont)
            is TeacherState.Success -> {
                Text("Ваши группы", fontSize = 18.sp, color = Color.Black, fontFamily = firstFont)
                Spacer(Modifier.height(8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.groups, key = { it.id }) { g ->
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(FieldBlue)
                                .clickable { onGroup(g.id) }
                                .padding(12.dp)
                        ) {
                            Text(g.schoolName, fontSize = 18.sp, color = Color.Black, fontFamily = firstFont)
                            Text(
                                g.visitDate.format(DATE_ONLY_FORMAT),
                                fontSize = 13.sp, color = Color.Black, fontFamily = secondFont
                            )
                        }
                    }
                }
            }
            else -> {}
        }
    }
}