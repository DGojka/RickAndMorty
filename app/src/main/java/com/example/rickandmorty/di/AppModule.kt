package com.example.rickandmorty.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@InstallIn(SingletonComponent::class)
@Module(includes = [NetworkModule::class, DataModule::class])
class AppModule {
}