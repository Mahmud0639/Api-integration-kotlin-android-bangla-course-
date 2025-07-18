package com.manuni.hello_world.api_integration.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.manuni.hello_world.R
import com.manuni.hello_world.api_integration.api.ApiEndPoints
import com.manuni.hello_world.api_integration.models.Students
import com.manuni.hello_world.databinding.ThesisCardItemBinding
import com.squareup.picasso.Picasso

class StudentsAdapter(var items: ArrayList<Students>):RecyclerView.Adapter<StudentsAdapter.StudentViewHolder>() {

    private lateinit var onViewOrEditListener: (Students) -> Unit
    private lateinit var onDeleteClickListener: (Students) -> Unit

     fun setOnViewOrEditListener(action: (Students)->Unit){
        onViewOrEditListener = action
    }

    //delete Button
    fun setOnDeleteClickListener(action: (Students)->Unit){
        onDeleteClickListener = action
    }


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ThesisCardItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val item = items[position]
        val binding = holder.binding

        binding.proTitleTxt.text = item.projectTitle
        binding.descriptionTxt.text = item.projectDesc

        Picasso.get().load(ApiEndPoints.BASE_URL+"ourapi" + item.imageUrl).placeholder(R.drawable.avatar).into(binding.studentImg)
    }




    inner class StudentViewHolder(var binding: ThesisCardItemBinding):ViewHolder(binding.root){
        //click listener type works here

        init {
            binding.viewOrEditBtn.setOnClickListener {
                onViewOrEditListener(items[adapterPosition])
            }

            binding.deleteBtn.setOnClickListener {
                onDeleteClickListener(items[adapterPosition])
            }
        }

    }
}