package test.compose.zingplayer.ui.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import test.compose.zingplayer.ui.view.SimpleSearchBar

@Composable
fun SearchScreen(
    navController: NavController,
) {
    val textFieldState = rememberTextFieldState()
    Box(modifier = Modifier.fillMaxSize()) {
        SimpleSearchBar(
            textFieldState,
            onSearch = {
                navController.popBackStack()
                navController.navigate("search/any?query=$it")
            },
            searchResults = emptyList()
        )
    }
}