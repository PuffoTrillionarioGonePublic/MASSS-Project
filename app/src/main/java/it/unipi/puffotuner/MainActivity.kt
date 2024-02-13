package it.unipi.puffotuner


import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.unipi.puffotuner.ui.theme.GreetingCardTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContent {
            GreetingCardTheme {

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val drawerOpen by viewModel.drawerShouldBeOpened
                    .collectAsStateWithLifecycle()

                if (drawerOpen) {
                    LaunchedEffect(Unit) {
                        try {
                            drawerState.open()
                        } finally {
                            viewModel.resetOpenDrawerAction()
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "tunerScreen") {
                        composable("tunerScreen") { TunerScreen(navController) }
                        composable("aboutScreen") { AboutScreen(navController) }
                    }
                }
            }
        }
    }
}


class MainViewModel : ViewModel() {

    private val _drawerShouldBeOpened = MutableStateFlow(false)
    val drawerShouldBeOpened = _drawerShouldBeOpened.asStateFlow()

    fun openDrawer() {
        _drawerShouldBeOpened.value = true
    }

    fun resetOpenDrawerAction() {
        _drawerShouldBeOpened.value = false
    }
}




