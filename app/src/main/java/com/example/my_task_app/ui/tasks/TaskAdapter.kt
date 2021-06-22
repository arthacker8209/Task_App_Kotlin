package com.example.my_task_app.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.my_task_app.data.Task
import com.example.my_task_app.databinding.ItemTaskBinding

class TaskAdapter(val listener: OnItemClickListener) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
      val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
       val currentItem = getItem(position)
        holder.bind(currentItem)
    }





   inner class TaskViewHolder(private val binding: ItemTaskBinding):RecyclerView.ViewHolder(binding.root){

      init {
          binding.apply {
              root.setOnClickListener {
                  val position = adapterPosition
                  if(position!= RecyclerView.NO_POSITION){
                      val task = getItem(position)
                      listener.onItemClicked(task)
                  }
              }
              checkboxTaskStatus.setOnClickListener {
                  val position = adapterPosition
                  if(position!= RecyclerView.NO_POSITION){
                      val task = getItem(position)
                      listener.onItemChecked(task, checkboxTaskStatus.isChecked)
                  }
              }

          }
      }


        fun bind(task:Task){
            binding.apply {
                checkboxTaskStatus.isChecked= task.completed
                tvTask.text= task.name
                tvTask.paint.isStrikeThruText=task.completed
                priority.isVisible=task.important
            }
        }
    }


    interface OnItemClickListener {
        fun onItemClicked(task: Task)
        fun onItemChecked(task: Task, isChecked:Boolean)
    }


class DiffCallBack: DiffUtil.ItemCallback<Task>(){
    override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id== newItem.id

    override fun areContentsTheSame(oldItem: Task, newItem: Task)= oldItem== newItem

}
}