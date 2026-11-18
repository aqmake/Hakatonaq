package com.example.hakaton.ui.nav

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.hakaton.data.Excursion
import com.example.hakaton.ui.screens.ExcursionsScreen
import com.example.hakaton.ui.screens.LoadingScreen
import com.example.hakaton.ui.screens.RegUch
import com.example.hakaton.ui.screens.RegisterScreen
import com.example.hakaton.ui.screens.RegisterViaCode
import com.example.hakaton.ui.screens.ScAdmin
import com.example.hakaton.ui.screens.ScParticipant
import com.example.hakaton.ui.screens.StudentsScreen

@Composable
fun Naigation() {
    var navState by remember { mutableStateOf(NavState.Zas) }
    var selectedExcursion by remember { mutableStateOf<Excursion?>(null) }
    var groupId by remember { mutableStateOf(0L) }
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
            ScParticipant()
        }

        NavState.Admin -> {
            ScAdmin(

            )
        }

        NavState.Excursions -> {
            ExcursionsScreen(
                onSelect = { selectedExcursion = it; navState = NavState.Log },
                onBack = { navState = NavState.Zas },
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
                onBack = { navState = NavState.Log }
            )
        }
    }
}