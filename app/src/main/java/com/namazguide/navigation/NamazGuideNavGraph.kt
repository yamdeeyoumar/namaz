package com.namazguide.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.namazguide.ui.components.ErrorState
import com.namazguide.ui.components.LoadingState
import com.namazguide.ui.screens.CompletionScreen
import com.namazguide.ui.screens.LandingScreen
import com.namazguide.ui.screens.PrayerGuidanceScreen
import com.namazguide.ui.screens.SetupScreen
import com.namazguide.ui.state.PrayerUiState
import com.namazguide.ui.viewmodel.PrayerViewModel

@Composable
fun NamazGuideNavGraph(viewModel: PrayerViewModel = viewModel()) {
    val navController = rememberNavController()
    val state by viewModel.uiState.collectAsState()

    NavHost(navController = navController, startDestination = "landing") {
        composable("landing") {
            LandingScreen(onStart = { navController.navigate("setup") })
        }
        composable("setup") {
            SetupScreen(onBegin = {
                viewModel.startPrayer(it)
                navController.navigate("guidance")
            })
        }
        composable("guidance") {
            when (val ui = state) {
                is PrayerUiState.Error -> ErrorState(ui.message)
                PrayerUiState.Idle -> LoadingState("Waiting for configuration")
                PrayerUiState.Loading -> LoadingState()
                is PrayerUiState.Success -> PrayerGuidanceScreen(
                    state = ui,
                    onNext = { viewModel.nextStep() },
                    onToggleTransliteration = { viewModel.toggleTransliteration() },
                    onToggleAutoAdvance = { viewModel.toggleAutoAdvance() },
                    onSpeedChange = { viewModel.setPlaybackSpeed(it) },
                    onComplete = { navController.navigate("complete") }
                )
            }
        }
        composable("complete") {
            val success = state as? PrayerUiState.Success
            CompletionScreen(
                summary = success?.let {
                    "Estimated ${it.plan.estimatedTotalSeconds}s vs target ${it.plan.targetTotalSeconds}s"
                } ?: "Prayer session finished.",
                onReturn = {
                    viewModel.reset()
                    navController.navigate("landing") {
                        popUpTo("landing") { inclusive = true }
                    }
                }
            )
        }
    }
}
