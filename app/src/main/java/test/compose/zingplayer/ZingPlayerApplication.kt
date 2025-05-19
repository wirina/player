package test.compose.zingplayer

import android.app.Application
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import test.compose.zingplayer.api.ZingApi
import test.compose.zingplayer.api.ZingApiClient
import test.compose.zingplayer.player.PlayerManager
import test.compose.zingplayer.ui.home.HomeScreenViewModel
import test.compose.zingplayer.ui.player.PlayerViewModel
import test.compose.zingplayer.ui.search.SearchViewModel
import test.compose.zingplayer.util.MoshiFactory
import java.net.CookieManager
import java.net.CookiePolicy

class ZingPlayerApplication: Application() {

    private val modules = module {
        single {
            val cookieManager = CookieManager()
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
            OkHttpClient.Builder().cookieJar(JavaNetCookieJar(cookieManager)).build()
        }
        single { MoshiFactory.factory() }
        single { ZingApiClient(moshi = get(), httpClient = get(), isDebug = true) }
        single { ZingApi(get()) }
        single { SnackbarHostState() }
        single { PlayerManager(androidContext()) }
        single(named("appScope")) {
            val eh = CoroutineExceptionHandler { _, _ ->

            }
            CoroutineScope(Dispatchers.Default + SupervisorJob() + eh)
        }
        viewModel { HomeScreenViewModel() }
        viewModel { PlayerViewModel() }
        viewModel { SearchViewModel() }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ZingPlayerApplication)
            androidLogger(level = Level.INFO)
            modules(modules)
        }
    }

}