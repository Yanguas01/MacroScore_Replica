package es.upm.macroscore.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.upm.macroscore.data.implementation.FoodRepositoryImpl
import es.upm.macroscore.data.implementation.UserRepositoryImpl
import es.upm.macroscore.data.network.MacroScoreApiService
import es.upm.macroscore.data.implementation.MealRepositoryImpl
import es.upm.macroscore.data.local.dao.MealDAO
import es.upm.macroscore.data.local.dao.UserDAO
import es.upm.macroscore.data.storage.TokenManager
import es.upm.macroscore.data.storage.UserManager
import es.upm.macroscore.domain.repositories.FoodRepository
import es.upm.macroscore.domain.repositories.MealRepository
import es.upm.macroscore.domain.repositories.UserRepository

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

    @Provides
    fun provideUserRepository(
        macroScoreApiService: MacroScoreApiService,
        userDAO: UserDAO,
        mealDAO: MealDAO,
        tokenManager: TokenManager,
        userManager: UserManager
    ): UserRepository {
        return UserRepositoryImpl(macroScoreApiService, userDAO, mealDAO, tokenManager, userManager)
    }

    @Provides
    fun provideMealRepository(
        macroScoreApiService: MacroScoreApiService
    ): MealRepository {
        return MealRepositoryImpl(macroScoreApiService)
    }

    @Provides
    fun provideFoodRepositoy(
        macroScoreApiService: MacroScoreApiService
    ): FoodRepository {
        return FoodRepositoryImpl(macroScoreApiService)
    }
}