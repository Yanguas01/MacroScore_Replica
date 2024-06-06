package es.upm.macroscore.core.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.upm.macroscore.data.local.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    private const val DATABASE_NAME = "macro_score_database"

    @Singleton
    @Provides
    fun provideRoom(context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideUserDAO(db: AppDatabase) = db.userDAO()

    @Singleton
    @Provides
    fun provideMealDAO(db: AppDatabase) = db.mealDAO()

    @Singleton
    @Provides
    fun provideFoodDAO(db: AppDatabase) = db.foodDAO()

    @Singleton
    @Provides
    fun provideUserFoodDAO(db: AppDatabase) = db.userFoodDAO()
}