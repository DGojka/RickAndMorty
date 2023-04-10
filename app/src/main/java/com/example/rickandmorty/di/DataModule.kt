package com.example.rickandmorty.di

import android.content.Context
import com.example.rickandmorty.network.ApiService
import com.example.rickandmorty.personsfragment.helpers.FavouritePersonsDb
import com.example.rickandmorty.personsfragment.list.helpers.FiltersManager
import com.example.rickandmorty.personsfragment.list.helpers.listfilter.PersonsFilter
import com.example.rickandmorty.repository.Repository
import com.example.rickandmorty.repository.RepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
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
    fun provideFavouritePersonsDb(@ApplicationContext context: Context): FavouritePersonsDb {
        return FavouritePersonsDb(context)
    }

    @Provides
    @Singleton
    fun providePersonsFilter(
        filtersManager: FiltersManager,
        favouritePersonsDb: FavouritePersonsDb
    ): PersonsFilter {
        return PersonsFilter(mutableListOf(), filtersManager, favouritePersonsDb)
    }
}