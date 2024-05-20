package es.upm.macroscore.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.upm.macroscore.data.UserRepositoryImpl
import es.upm.macroscore.data.network.MacroScoreApiService
import es.upm.macroscore.domain.UserRepository

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

    @Provides
    fun provideUserRepository(macroScoreApiService: MacroScoreApiService): UserRepository {
        return UserRepositoryImpl(macroScoreApiService)
    }

}