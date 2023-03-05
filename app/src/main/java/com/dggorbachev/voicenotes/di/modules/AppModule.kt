package com.dggorbachev.voicenotes.di.modules

import com.dggorbachev.voicenotes.di.modules.activity.ActivityBuildersModule
import com.dggorbachev.voicenotes.di.modules.data.DataModule
import com.dggorbachev.voicenotes.di.modules.network.NetworkModule
import dagger.Module

@Module(
    includes = [
        ActivityBuildersModule::class,
        DataModule::class,
        NetworkModule::class
    ]
)
class AppModule