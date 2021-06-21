package com.example.my_task_app.ui.addeditfragment

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_task_app.data.Task
import com.example.my_task_app.data.TaskDao
import com.example.my_task_app.ui.ADD_TASK_RESULT_OK
import com.example.my_task_app.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditFragmentViewModel @ViewModelInject constructor(
        private val dao : TaskDao,
        @Assisted private val state : SavedStateHandle

): ViewModel() {

    private val addEditTaskEventChannel= Channel<AddEditTaskEvent>()
    val addEditTaskEvent= addEditTaskEventChannel.receiveAsFlow()





    fun onSaveClick() {
        if (textname.isBlank()) {
            showInvalidInputMessage("Name Cannot be Empty")
            return
        }
        if (task != null) {
            val updateTask = task.copy(name = textname, important = taskImportant)
            update(updateTask)
        } else {
            val newTask = Task(name=textname, important = taskImportant)
            createTask(newTask)
        }
    }

    private fun showInvalidInputMessage(s: String) =viewModelScope.launch {
        addEditTaskEventChannel.send(AddEditTaskEvent.showInvalidInputMessage(s))
    }


    val task = state.get<Task>("task")

    var textname = state.get<String>("taskname")?:task?.name?: ""
    set(value) {
        field=value
        state.set("taskname",value)
    }

    var taskImportant = state.get<Boolean>("taskImp")?:task?.important?:false
    set(value) {
        field= value
        state.set("taskImp",value)
    }

    private fun createTask(task: Task) = viewModelScope.launch {
        //by coroutine scope because this is a database operation
        dao.insert(task)
        //navigate back
        addEditTaskEventChannel.send(AddEditTaskEvent.navigateBackWithResults(ADD_TASK_RESULT_OK))
    }

    private fun update(task: Task) = viewModelScope.launch {
        //by coroutine scope because this is a database operation
        dao.update(task)
        //navigate back
        addEditTaskEventChannel.send(AddEditTaskEvent.navigateBackWithResults(EDIT_TASK_RESULT_OK))
    }

    sealed class AddEditTaskEvent{
        data class showInvalidInputMessage(val msg: String):AddEditTaskEvent()
        data class navigateBackWithResults(val result: Int):AddEditTaskEvent()

    }
}