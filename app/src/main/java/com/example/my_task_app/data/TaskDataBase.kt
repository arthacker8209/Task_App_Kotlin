package com.example.my_task_app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.my_task_app.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDataBase : RoomDatabase() {

        abstract fun taskDao():TaskDao

        class callback @Inject constructor(
                private val dataBase: Provider<TaskDataBase>,
               @ApplicationScope private val applicationScope:CoroutineScope

        ) : RoomDatabase.Callback(){
                override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                       val dao= dataBase.get().taskDao()
                        applicationScope.launch {
                        }
                }
        }





}