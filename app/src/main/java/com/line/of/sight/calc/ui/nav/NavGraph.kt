package com.line.of.sight.calc.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.line.of.sight.calc.ui.screens.CalculatorScreen
import com.line.of.sight.calc.ui.screens.OnboardingScreen
import com.line.of.sight.calc.ui.screens.SplashScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onDone = {
                navController.navigate("onboarding") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("onboarding") {
            OnboardingScreen(onFinish = {
                navController.navigate("calculator") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }
        composable("calculator") {
            CalculatorScreen()
        }
    }
}
