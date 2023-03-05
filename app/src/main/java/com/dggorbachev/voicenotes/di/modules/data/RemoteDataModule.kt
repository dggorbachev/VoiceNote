package com.dggorbachev.voicenotes.di.modules.data

import com.dggorbachev.voicenotes.data.remote.DocsApi
import com.dggorbachev.voicenotes.data.remote.DocsRemoteRepo
import com.dggorbachev.voicenotes.data.remote.DocsRemoteRepoImpl
import com.dggorbachev.voicenotes.data.remote.DocsRemoteSource
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
object RemoteDataModule {

    @Provides
    @Singleton
    fun provideDocsApi(retrofit: Retrofit): DocsApi =
        retrofit.create(DocsApi::class.java)

    @Provides
    @Singleton
    fun provideDocsRemoteSource(
        docsApi: DocsApi,
    ): DocsRemoteSource =
        DocsRemoteSource(
            docsApi = docsApi,
        )

    @Provides
    @Singleton
    fun provideDocsRemoteRepo(
        source: DocsRemoteSource,
    ): DocsRemoteRepo =
        DocsRemoteRepoImpl(source)
}