package com.dggorbachev.voicenotes.di.modules.fragments

import com.dggorbachev.voicenotes.features.records_screen.ui.view.RecordsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface FragmentsBuilderModule {
    @ContributesAndroidInjector
    fun contributeRecordsFragment(): RecordsFragment
}