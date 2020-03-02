package utils.init

import android.app.Application
import br.com.caiodev.gistnator.BuildConfig
import br.com.caiodev.gistnator.sections.gistObtainment.model.koin.gistObtainmentViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

@Suppress("UNUSED")
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(gistObtainmentViewModel)
        }

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}