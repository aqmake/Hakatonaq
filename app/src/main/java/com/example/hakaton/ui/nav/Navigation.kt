package com.example.hakaton.ui.nav

import TeacherLoginScreen
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.hakaton.data.Excursion
import com.example.hakaton.data.StaffCode
import com.example.hakaton.ui.screens.ExcursionsScreen
import com.example.hakaton.ui.screens.LoadingScreen
import com.example.hakaton.ui.screens.ManageExcursionsScreen
import com.example.hakaton.ui.screens.RegUch
import com.example.hakaton.ui.screens.RegisterScreen
import com.example.hakaton.ui.screens.RegisterViaCode
import com.example.hakaton.ui.screens.ScAdmin
import com.example.hakaton.ui.screens.ScParticipant
import com.example.hakaton.ui.screens.ScannerScreen
import com.example.hakaton.ui.screens.StudentsScreen
import com.example.hakaton.ui.screens.SummaryScreen

@Composable
fun Naigation() {
    var fromTeacher by remember { mutableStateOf(false) }
    var navState by remember { mutableStateOf(NavState.Zas) }
    var selectedExcursion by remember { mutableStateOf<Excursion?>(null) }
    var groupId by remember { mutableStateOf(0L)
    }
    when (navState) {

        NavState.Zas -> {
            LoadingScreen(
                onNextScreen = {
                    navState = it
                }
            )
        }

        NavState.RegShkolota -> {
            RegUch()
        }


        NavState.Participant -> {
            ScParticipant(
                onBack = { navState = NavState.RegViaCode }
            )
        }

        NavState.Admin -> {
            ScAdmin(
                onBack = { navState = NavState.Excursions },
                onScan = { navState = NavState.Scanner },
                onSummary = { navState = NavState.Summary },
                onCreateExcursion = { navState = NavState.ManageExcursions }
            )
        }

        NavState.ManageExcursions -> {
            ManageExcursionsScreen(onBack = { navState = NavState.Admin })
        }

        NavState.Excursions -> {
            ExcursionsScreen(
                onSelect = { selectedExcursion = it; navState = NavState.Log },
                onTeach = { navState = NavState.TeacherLogin },
                onCode = { navState = NavState.RegViaCode }
            )
        }

        NavState.Log -> {
            val ex = selectedExcursion
            if (ex != null) {
                RegisterScreen(
                    excursion = ex,
                    onNextScreen = { navState = it },
                    onGroupCreated = { id ->
                        groupId = id
                        navState = NavState.Students
                    },
                    onBack = { navState = NavState.Excursions }
                )
            } else navState = NavState.Excursions
        }

        NavState.RegViaCode -> {
            RegisterViaCode(
                onParticipant = { navState = NavState.Participant },
                onAdmin = { navState = NavState.Admin },
                onBack = { navState = NavState.Excursions }
            )
        }

        NavState.Students -> {
            StudentsScreen(
                groupId = groupId,
                onBack = { navState = if (fromTeacher) NavState.TeacherLogin else NavState.Log }
            )
        }
        NavState.Scanner -> {
            ScannerScreen(onBack = { navState = NavState.Admin })
        }
        NavState.Summary -> {
            SummaryScreen(onBack = { navState = NavState.Admin })
        }
        NavState.TeacherLogin -> {
            TeacherLoginScreen(
                onBack = { navState = NavState.Excursions },
                onGroup = { id ->
                    groupId = id
                    fromTeacher = true
                    navState = NavState.Students
                }
            )
        }
    }
}