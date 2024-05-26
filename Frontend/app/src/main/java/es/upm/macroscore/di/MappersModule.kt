package es.upm.macroscore.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.upm.macroscore.data.mapper.SignUpRequestMapperImpl
import es.upm.macroscore.domain.mapper.SignUpRequestMapper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MappersModule {

    @Provides
    @Singleton
    fun providesSignUpRequestMapper(): SignUpRequestMapper {
        return SignUpRequestMapperImpl()
    }
}