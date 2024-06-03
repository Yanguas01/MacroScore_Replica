package es.upm.macroscore.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.upm.macroscore.data.implementation.UserRepositoryImpl
import es.upm.macroscore.data.network.MacroScoreApiService
import es.upm.macroscore.data.implementation.MealRepositoryImpl
import es.upm.macroscore.data.storage.TokenManager
import es.upm.macroscore.domain.repositories.MealRepository
import es.upm.macroscore.domain.repositories.UserRepository

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

    @Provides
    fun provideUserRepository(
        macroScoreApiService: MacroScoreApiService,
        tokenManager: TokenManager
    ): UserRepository {
        return UserRepositoryImpl(macroScoreApiService, tokenManager)
    }

    @Provides
    fun provideMealRepository(
        macroScoreApiService: MacroScoreApiService
    ): MealRepository {
        return MealRepositoryImpl(macroScoreApiService)
    }
}