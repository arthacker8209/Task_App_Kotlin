package com.example.my_task_app.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.my_task_app.R
import com.example.my_task_app.data.SortOrder
import com.example.my_task_app.data.Task
import com.example.my_task_app.databinding.FragmentTasksBinding
import com.example.my_task_app.util.OnQueryTextChanged
import com.example.my_task_app.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class TasksFragment:Fragment(R.layout.fragment_tasks), TaskAdapter.OnItemClickListener {
    private val viewModel:TaskViewModel by viewModels()
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTasksBinding.bind(view)
        val taskAdapter=TaskAdapter(this)

        binding.apply {
            rvTasks.apply {
                adapter=taskAdapter
                layoutManager= LinearLayoutManager(requireContext())
                itemAnimator=DefaultItemAnimator()
                setHasFixedSize(true)
            }
            ItemTouchHelper(object :ItemTouchHelper
            .SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT ){
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                   return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = taskAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTaskSwiped(task)

                }

            }).attachToRecyclerView(rvTasks)

            fabAddTask.setOnClickListener {
                viewModel.addNewTaskClick()
            }
        }
        setFragmentResultListener("add_edit_request"){_,bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.addNewTaskResult(result)
        }

        viewModel.tasks.observe(viewLifecycleOwner){
            taskAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.taskEvent.collect {event ->
                when(event){
                    is TaskViewModel.TaskEvent.ShowUndoDeletedTaskMessage -> {
                        Snackbar.make(requireView(),"Task Deleted", Snackbar.LENGTH_LONG)
                                .setAction("UNDO"){
                                    viewModel.onUndoClicked(event.task)
                                }.show()
                    }
                    is TaskViewModel.TaskEvent.NavigateToAddTaskScreen -> {
                            val action = TasksFragmentDirections.actionTasksFragmentToAddEditFragment3(null, "New Task")
                            findNavController().navigate(action)
                    }
                    is TaskViewModel.TaskEvent.navigateToAddTask -> {
                        val action = TasksFragmentDirections.actionTasksFragmentToAddEditFragment3(event.task,"Edit Task")
                        findNavController().navigate(action)
                    }
                    is TaskViewModel.TaskEvent.ShowTaskSavedConfirmationMessage -> {
                        Snackbar.make(requireView(),event.msg,Snackbar.LENGTH_SHORT).show()
                    }
                     TaskViewModel.TaskEvent.NavigateToDeleteAllCompletedScreen -> {
                        val action = TasksFragmentDirections.actionGlobalDeleteAllCompletedDialogFragment()
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }

        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_menu_items,menu)
        val searchItem= menu.findItem(R.id.action_search)
        searchView= searchItem.actionView as SearchView

        val pendingQuery = viewModel.searchQuery.value
        if (pendingQuery!=null && pendingQuery.isNotEmpty()){
            searchView.setQuery(pendingQuery,false)
        }

       searchView.OnQueryTextChanged {
            viewModel.searchQuery.value= it
       }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

       return when(item.itemId) {
            R.id.action_sort_by_name -> {
               viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
           R.id.action_sort_by_date_created ->{
               viewModel.onSortOrderSelected(SortOrder.BY_DATE)
               true
           }
           R.id.action_hide_completed_tasks ->{
               item.isChecked = !item.isChecked
               viewModel.onHideCompletedSelected( item.isChecked)
               true
           }
           R.id.action_delete_all_completed_tasks ->{
               viewModel.onDeleteAllCompleted()
               true
           }

           else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        searchView.setOnQueryTextListener(null)
    }

    override fun onItemClicked(task: Task) {
        viewModel.onTaskSelected(task)
    }

    override fun onItemChecked(task: Task, isChecked: Boolean) {
        viewModel.onItemCheckedChanges(task,isChecked)
    }
}