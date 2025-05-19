package test.compose.zingplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import test.compose.zingplayer.ui.home.HomeScreen
import test.compose.zingplayer.ui.search.SearchResultScreen
import test.compose.zingplayer.ui.search.SearchScreen
import test.compose.zingplayer.ui.theme.ZingPlayerTheme

class MainActivity : ComponentActivity() {
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZingPlayerTheme {
                MainScreen()
            }
        }
    }
}

@Composable
private fun MainScreen(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    Surface(modifier = modifier.fillMaxSize()) {
        NavHost(navController, "home") {
            composable("home") {
                HomeScreen(navController)
            }
            composable("search") {
                SearchScreen(navController)
            }
            composable(
                "search/any?query={query}",
                arguments = listOf(navArgument("query") { type = NavType.StringType })
            ) {
                val query = it.arguments?.getString("query") ?: ""
                SearchResultScreen(query, navController)
            }
        }
    }
}