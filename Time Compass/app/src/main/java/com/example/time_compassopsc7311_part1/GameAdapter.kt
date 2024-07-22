package com.example.time_compassopsc7311_part1

import Points
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GameAdapter(private val pointsList: List<Points>) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Declare all variables here
        val userPosition: TextView = itemView.findViewById(R.id.displayPosition)
        val userName: TextView = itemView.findViewById(R.id.displayUser)
        val userPoints: TextView = itemView.findViewById(R.id.displayPoints)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        // Bind the data to the views
        val point = pointsList[position]
        (position + 1).toString().also { holder.userPosition.text = it }
        holder.userName.text = point.userName
        """${point.userPoints} pts""".also { holder.userPoints.text = it }
    }

    override fun getItemCount(): Int {
        return pointsList.size
    }
}