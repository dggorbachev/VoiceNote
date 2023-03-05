package com.dggorbachev.voicenotes.di.modules.activity

import com.dggorbachev.voicenotes.MainActivity
import com.dggorbachev.voicenotes.di.modules.fragments.FragmentsBuilderModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivityBuildersModule {

    @ContributesAndroidInjector(modules = [FragmentsBuilderModule::class])
    fun contributeMainActivity(): MainActivity

}