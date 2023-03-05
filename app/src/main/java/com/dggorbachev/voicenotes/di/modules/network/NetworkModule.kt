package com.dggorbachev.voicenotes.di.modules.network

import com.dggorbachev.voicenotes.base.HostSelectionInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        interceptor: HttpLoggingInterceptor,
        hostSelectionInterceptor: HostSelectionInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(hostSelectionInterceptor)
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideHostSelectionInterceptor(): HostSelectionInterceptor {
        return HostSelectionInterceptor()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://vk.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}