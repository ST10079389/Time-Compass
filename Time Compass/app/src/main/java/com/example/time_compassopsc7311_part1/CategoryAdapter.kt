package com.example.time_compassopsc7311_part1

import Category
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(private val categoryList: List<Category>) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //declare all variables here
        val categoryName: Button = itemView.findViewById(R.id.CategoryBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item_layout, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        //this is where you can set your values
        val category = categoryList[position]
        holder.categoryName.setText(category.categoryName)
        holder.categoryName.setBackgroundColor(colorChange(category.color).toColorInt())
        holder.categoryName.setTextColor(colorChangeText(category.color).toColorInt())
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
    private fun colorChange(x:String): String {
         var colourName = ""
        if (x.equals("Dark Blue")){
            colourName = "#00003f"
        }else if (x.equals("Light Blue")){
            colourName ="#00ADB5"
        }else if (x.equals("Grey")){
            colourName ="#808080"
        }else if (x.equals("Orange")){
            colourName ="#F8B400"
        }else if (x.equals("Purple")){
            colourName ="#7209B7"
        }else if (x.equals("Black")){
            colourName ="#000000"
        }else if (x.equals("White")){
            colourName ="#FFFFFFFF"
        }else if(x.equals("Select Color")){
            colourName ="#D9D9D9"
        }
        return colourName
    }
    private fun colorChangeText(x:String): String {
        var colourName = ""
        if (x.equals("Dark Blue")){
            colourName = "#FFFFFFFF"
        }else if (x.equals("Light Blue")){
            colourName ="#FF000000"
        }else if (x.equals("Grey")){
            colourName ="#FFFFFFFF"
        }else if (x.equals("Orange")){
            colourName ="#FF000000"
        }else if (x.equals("Purple")){
            colourName ="#FFFFFFFF"
        }else if (x.equals("Black")){
            colourName ="#FFFFFFFF"
        }else if (x.equals("White")){
            colourName ="#FF000000"
        }else if(x.equals("Select Color")){
            colourName ="#FF000000"
        }
        return colourName
    }
}