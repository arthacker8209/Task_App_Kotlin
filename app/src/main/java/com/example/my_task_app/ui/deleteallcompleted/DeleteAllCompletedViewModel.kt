package com.example.my_task_app.ui.deleteallcompleted

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.my_task_app.data.TaskDao
import com.example.my_task_app.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteAllCompletedViewModel @ViewModelInject  constructor(
        private val taskDao: TaskDao,
        @ApplicationScope private val applicationScope: CoroutineScope

) :ViewModel() {

    fun onConfirmClick()= applicationScope.launch {
        taskDao.deleteCompletedTasks()
    }
}