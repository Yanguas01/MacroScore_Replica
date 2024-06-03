package es.upm.macroscore.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.upm.macroscore.data.repository.UserRepositoryImpl
import es.upm.macroscore.data.network.MacroScoreApiService
import es.upm.macroscore.data.repository.MealRepositoryImpl
import es.upm.macroscore.data.storage.TokenManager
import es.upm.macroscore.domain.MealRepository
import es.upm.macroscore.domain.UserRepository
import es.upm.macroscore.domain.mapper.LogInRequestMapper
import es.upm.macroscore.domain.mapper.SignUpRequestMapper

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

    @Provides
    fun provideUserRepository(
        macroScoreApiService: MacroScoreApiService,
        signUpRequestMapper: SignUpRequestMapper,
        logInRequestMapper: LogInRequestMapper,
        tokenManager: TokenManager
    ): UserRepository {
        return UserRepositoryImpl(macroScoreApiService, signUpRequestMapper, logInRequestMapper, tokenManager)
    }

    @Provides
    fun provideMealRepository(
        macroScoreApiService: MacroScoreApiService
    ): MealRepository {
        return MealRepositoryImpl(macroScoreApiService)
    }
}