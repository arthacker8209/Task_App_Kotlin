package com.example.my_task_app.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.my_task_app.data.PreferenceManager
import com.example.my_task_app.data.SortOrder
import com.example.my_task_app.data.Task
import com.example.my_task_app.data.TaskDao
import com.example.my_task_app.ui.ADD_TASK_RESULT_OK
import com.example.my_task_app.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TaskViewModel @ViewModelInject constructor(
        private val taskDao: TaskDao,
        private val preferenceManager: PreferenceManager,
       @Assisted private val state: SavedStateHandle

):ViewModel() {
    val searchQuery = state.getLiveData("searchQuery","")
    val preferenceFlow = preferenceManager.preferencesFlow

    private val taskEventChannel = Channel<TaskEvent>()
    val taskEvent = taskEventChannel.receiveAsFlow()
    private val taskflow = combine(
            searchQuery.asFlow(),
    preferenceFlow){query , preferenceFlow ->
        Pair(query, preferenceFlow)

    }.flatMapLatest {(query , preferenceFlow) ->
        taskDao.getTasks(query, preferenceFlow.sortOrder, preferenceFlow.hideCompleted)
    }

    val tasks= taskflow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferenceManager.updateOrder(sortOrder)
    }
    fun onHideCompletedSelected(isHidden:Boolean) = viewModelScope.launch {
        preferenceManager.updateHideCompleted(isHidden)
    }

    fun onTaskSelected(task: Task) = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.navigateToAddTask(task))
    }

    fun onItemCheckedChanges(task: Task, checked: Boolean)=viewModelScope.launch {
        taskDao.update(task.copy(completed = checked))
    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        taskEventChannel.send(TaskEvent.ShowUndoDeletedTaskMessage(task))
    }

    fun onUndoClicked(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun addNewTaskClick() = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.NavigateToAddTaskScreen)
    }

    fun addNewTaskResult(result: Int) {
        when(result){
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task Added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task Edited")
        }
    }

    private fun showTaskSavedConfirmationMessage(s: String) = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.ShowTaskSavedConfirmationMessage(s))
    }

    fun onDeleteAllCompleted() = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.NavigateToDeleteAllCompletedScreen)
    }



    sealed class TaskEvent {
        object NavigateToAddTaskScreen: TaskEvent()
        data class navigateToAddTask(val task:Task):TaskEvent()
        data class ShowUndoDeletedTaskMessage(val task: Task):TaskEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String):TaskEvent()
        object NavigateToDeleteAllCompletedScreen:TaskEvent()
    }

}


