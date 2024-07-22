package com.example.time_compassopsc7311_part1

import Task
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class FilterCategoriesAdapter(
    private var totalHoursByCategory: List<Double>,
    private val taskList: List<Task>,
    private val context: Context
) : RecyclerView.Adapter<FilterCategoriesAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val category: TextView = itemView.findViewById(R.id.displayCategory)
        val totalHours: TextView = itemView.findViewById(R.id.totalHours)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item_layout2, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val totalHours = totalHoursByCategory[position]
        holder.totalHours.text = String.format("%.2f hours", totalHours)
        holder.category.text = "Category $position"
    }

    override fun getItemCount(): Int {
        return totalHoursByCategory.size
    }

    fun updateCategoriesTasks(filteredTasks: List<Double>) {
        totalHoursByCategory = filteredTasks
        notifyDataSetChanged()
    }

    private fun getDateInMillis(dateString: String): Long {
        val pattern = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return try {
            val date = sdf.parse(dateString)
            date?.time ?: 0
        } catch (e: ParseException) {
            e.printStackTrace()
            0
        }
    }
}