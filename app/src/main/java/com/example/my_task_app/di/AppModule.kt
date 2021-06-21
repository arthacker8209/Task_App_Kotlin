package com.example.my_task_app.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.my_task_app.data.TaskDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataBase(app:Application,callback:TaskDataBase.callback)=
            Room.databaseBuilder(app,TaskDataBase::class.java, "Task.db")
            .fallbackToDestructiveMigration()
                    .addCallback(callback)
            .build()

    @Provides
    fun provideTaskDao(db:TaskDataBase)= db.taskDao()

    
    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope()= CoroutineScope(SupervisorJob())

}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope