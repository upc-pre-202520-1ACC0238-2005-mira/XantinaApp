package com.upc.xantina.features.extraccion.infrastructure.di

import com.upc.xantina.features.extraccion.domain.repository.ExtraccionRepository
import com.upc.xantina.features.extraccion.domain.usecase.GetExtraccionesRecientesUseCase
import com.upc.xantina.features.extraccion.domain.usecase.GetMetodosExtraccionUseCase
import com.upc.xantina.features.extraccion.domain.usecase.GetBolsasCafeUseCase
import com.upc.xantina.features.extraccion.domain.usecase.ConsumirBolsaCafeUseCase
import com.upc.xantina.features.extraccion.domain.usecase.CreateBolsaCafeUseCase
import com.upc.xantina.features.extraccion.domain.usecase.GuardarExtraccionUseCase
import com.upc.xantina.features.extraccion.infrastructure.repository.ExtraccionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ExtraccionBindingsModule {

    @Binds
    @Singleton
    abstract fun bindExtraccionRepository(
        impl: ExtraccionRepositoryImpl
    ): ExtraccionRepository
}

@Module
@InstallIn(SingletonComponent::class)
object ExtraccionUseCaseModule {

    @Provides
    fun provideGetMetodosExtraccionUseCase(
        repository: ExtraccionRepository
    ): GetMetodosExtraccionUseCase = GetMetodosExtraccionUseCase(repository)

    @Provides
    fun provideGetExtraccionesRecientesUseCase(
        repository: ExtraccionRepository
    ): GetExtraccionesRecientesUseCase = GetExtraccionesRecientesUseCase(repository)

    @Provides
    fun provideGuardarExtraccionUseCase(
        repository: ExtraccionRepository
    ): GuardarExtraccionUseCase = GuardarExtraccionUseCase(repository)

    @Provides
    fun provideGetBolsasCafeUseCase(
        repository: ExtraccionRepository
    ): GetBolsasCafeUseCase = GetBolsasCafeUseCase(repository)

    @Provides
    fun provideConsumirBolsaCafeUseCase(
        repository: ExtraccionRepository
    ): ConsumirBolsaCafeUseCase = ConsumirBolsaCafeUseCase(repository)

    @Provides
    fun provideCreateBolsaCafeUseCase(
        repository: ExtraccionRepository
    ): CreateBolsaCafeUseCase = CreateBolsaCafeUseCase(repository)
}

