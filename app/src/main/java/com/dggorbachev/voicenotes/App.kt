package com.dggorbachev.voicenotes

import com.dggorbachev.voicenotes.di.AppComponent
import com.dggorbachev.voicenotes.di.DaggerAppComponent.factory
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class App : DaggerApplication() {

    val component by lazy {
        factory()
            .create(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return initializeDaggerComponent()
    }

    private fun initializeDaggerComponent(): AppComponent {
        component.inject(this)

        return component
    }
}