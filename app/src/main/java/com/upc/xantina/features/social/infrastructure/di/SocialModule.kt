package com.upc.xantina.features.social.infrastructure.di

import com.upc.xantina.features.social.domain.repository.SocialRepository
import com.upc.xantina.features.social.infrastructure.api.SocialApiService
import com.upc.xantina.features.social.infrastructure.repository.SocialRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SocialModule {
    
    @Provides
    @Singleton
    fun provideSocialApiService(
        retrofit: Retrofit
    ): SocialApiService {
        return retrofit.create(SocialApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideSocialRepository(
        apiService: SocialApiService
    ): SocialRepository {
        return SocialRepositoryImpl(apiService)
    }
}

