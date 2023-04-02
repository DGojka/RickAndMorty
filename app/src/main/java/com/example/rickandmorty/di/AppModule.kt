package com.example.rickandmorty.di

import android.content.Context
import com.example.rickandmorty.personsfragment.list.helpers.FavouritePersonsDb
import com.example.rickandmorty.personsfragment.list.helpers.FiltersManager
import com.example.rickandmorty.network.ApiService
import com.example.rickandmorty.repository.Repository
import com.example.rickandmorty.repository.RepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideRepository(apiService: ApiService): Repository = RepositoryImpl(apiService)

    @Provides
    @Singleton
    fun provideFiltersManager(@ApplicationContext context: Context): FiltersManager {
        return FiltersManager(context)
    }

    @Provides
    @Singleton
    fun provideFavouritePersonsDb(@ApplicationContext context : Context): FavouritePersonsDb{
        return  FavouritePersonsDb(context)
    }

    companion object {
        private const val BASE_URL = "https://rickandmortyapi.com/api/"
    }
}