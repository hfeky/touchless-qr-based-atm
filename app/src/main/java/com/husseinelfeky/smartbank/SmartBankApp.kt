package com.husseinelfeky.smartbank

import android.app.Application
import com.husseinelfeky.smartbank.koin.repositoriesModule
import com.husseinelfeky.smartbank.koin.retrofitModule
import com.husseinelfeky.smartbank.koin.viewModelsModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

@ExperimentalCoroutinesApi
@FlowPreview
class SmartBankApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@SmartBankApp)
            logger(AndroidLogger())
            modules(
                retrofitModule,
                repositoriesModule,
                viewModelsModule
            )
        }
    }
}
