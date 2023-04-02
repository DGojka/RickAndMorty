package com.example.rickandmorty.di

import android.content.Context
import com.example.rickandmorty.characterfragment.list.helpers.FiltersManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideFiltersManager(@ApplicationContext context: Context): FiltersManager {
        return FiltersManager(context)
    }
}