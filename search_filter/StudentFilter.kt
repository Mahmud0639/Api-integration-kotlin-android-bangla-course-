package com.manuni.hello_world.api_integration.search_filter

import android.widget.Filter
import com.manuni.hello_world.api_integration.adapters.StudentsAdapter
import com.manuni.hello_world.api_integration.models.Students

class StudentFilter(
    private val adapter: StudentsAdapter,
    private val originalList: ArrayList<Students>
) : Filter() {

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val result = FilterResults()

        if (!constraint.isNullOrEmpty()) {
            val query = constraint.toString().lowercase()
            val filteredList = ArrayList<Students>()

            for (item in originalList) {
                if (item.name!!.lowercase().contains(query) || item.email!!.lowercase()
                        .contains(query)
                    || item.phone!!.lowercase()
                        .contains(query) || item.departments!!.deptName!!.lowercase()
                        .contains(query)
                    || item.projectTitle!!.lowercase().contains(query)
                    || item.projectDesc!!.lowercase().contains(query)
                ) {
                     filteredList.add(item)
                }
            }

            result.count = filteredList.size
            result.values = filteredList
        }else{
            result.count = originalList.size
            result.values = originalList
        }

        return result

    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        if (results != null){
            adapter.items = results.values as ArrayList<Students>
            adapter.notifyDataSetChanged()
        }

    }
}