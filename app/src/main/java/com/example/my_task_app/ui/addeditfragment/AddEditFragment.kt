package com.example.my_task_app.ui.addeditfragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.my_task_app.R
import com.example.my_task_app.databinding.FragmentAddEditTaskBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditFragment : Fragment(R.layout.fragment_add_edit_task) {

    private val viewModel: AddEditFragmentViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditTaskBinding.bind(view)
        binding.apply {
            editTask.setText(viewModel.textname)
            cbImportant.isChecked=viewModel.taskImportant
            cbImportant.jumpDrawablesToCurrentState()
            dateCreated.isVisible= viewModel.task!=null
            dateCreated.text= "Created: ${viewModel.task?.createdDateFormat} "

            editTask.addTextChangedListener {
                viewModel.textname=it.toString()
            }
            cbImportant.setOnCheckedChangeListener { _, isChecked ->
                viewModel.taskImportant=isChecked
            }
            fabSaveTask.setOnClickListener {
                viewModel.onSaveClick()
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTaskEvent.collect {event->
                when(event){
                    is AddEditFragmentViewModel.AddEditTaskEvent.showInvalidInputMessage -> {
                        Snackbar.make(requireView(),event.msg,Snackbar.LENGTH_LONG).show()
                    }
                    is AddEditFragmentViewModel.AddEditTaskEvent.navigateBackWithResults ->{
                        binding.editTask.clearFocus()
                        setFragmentResult(
                                "add_edit_request",
                                bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }
}