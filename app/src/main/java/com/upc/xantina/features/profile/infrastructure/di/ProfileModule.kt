package com.upc.xantina.features.profile.infrastructure.di

import com.upc.xantina.features.profile.domain.repository.ProfileRepository
import com.upc.xantina.features.profile.infrastructure.api.ProfileApiService
import com.upc.xantina.features.profile.infrastructure.repository.ProfileRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Módulo de inyección de dependencias para Profile
 * Configura los providers de Hilt
 */
@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {
    
    /**
     * Provee la instancia del ProfileApiService
     */
    @Provides
    @Singleton
    fun provideProfileApiService(
        retrofit: Retrofit
    ): ProfileApiService {
        return retrofit.create(ProfileApiService::class.java)
    }
    
    /**
     * Provee la implementación del ProfileRepository
     */
    @Provides
    @Singleton
    fun provideProfileRepository(
        apiService: ProfileApiService
    ): ProfileRepository {
        return ProfileRepositoryImpl(apiService)
    }
}
