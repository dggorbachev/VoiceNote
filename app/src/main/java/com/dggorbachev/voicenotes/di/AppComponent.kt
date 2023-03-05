package com.dggorbachev.voicenotes.di

import android.app.Application
import com.dggorbachev.voicenotes.App
import com.dggorbachev.voicenotes.di.modules.AppModule
import com.dggorbachev.voicenotes.features.save_screen.ui.SaveNoteDialog
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import javax.inject.Singleton

@Component(modules = [AndroidInjectionModule::class, AppModule::class])
@Singleton
interface AppComponent : AndroidInjector<DaggerApplication> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Application): AppComponent
    }

    fun app(): Application
    fun inject(app: App)
    fun inject(saveNoteDialog: SaveNoteDialog)
}