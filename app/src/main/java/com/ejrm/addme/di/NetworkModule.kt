package com.ejrm.addme.di

import com.ejrm.addme.data.network.ContactApiClient
import com.ejrm.addme.data.network.HeaderInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("http://tuapkmovil.125mb.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(getClient())
        .build()


    private fun getClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.HEADERS
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(HeaderInterceptor())
            .build()
    }


    @Singleton
    @Provides
    fun provideContactApiClient(retrofit: Retrofit): ContactApiClient =
        retrofit.create(ContactApiClient::class.java)

}