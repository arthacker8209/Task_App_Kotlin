package com.example.my_task_app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {


    fun getTasks(query: String, sortOrder: SortOrder, hideCompleted: Boolean):Flow<List<Task>> =
            when(sortOrder){
                SortOrder.BY_NAME -> getAllTasksBYNAME(query, hideCompleted)
                SortOrder.BY_DATE-> getAllTasksBYDATE(query, hideCompleted)
            }

    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed=0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC , name")
    fun getAllTasksBYNAME(searchQuery: String, hideCompleted:Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed=0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC , created ")
    fun getAllTasksBYDATE(searchQuery: String, hideCompleted:Boolean): Flow<List<Task>>

    @Query("DELETE FROM task_table WHERE completed =1")
    suspend fun deleteCompletedTasks()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task:Task)

    @Update
    suspend fun update(task:Task)

    @Delete
    suspend fun delete(task:Task)

}