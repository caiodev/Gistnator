package utils.init

import androidx.multidex.MultiDexApplication
import br.com.caiodev.gistnator.BuildConfig
import br.com.caiodev.gistnator.sections.favoriteGists.model.diModules.provideGistDatabaseInstance
import timber.log.Timber

@Suppress("UNUSED")
class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        provideGistDatabaseInstance(this@App)
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}