package com.example.moneymanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AchievementAdapter(private val achievements: List<Achievement>) :
    RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    class AchievementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.ivAchievementIcon)
        val title: TextView = itemView.findViewById(R.id.tvAchievementTitle)
        val description: TextView = itemView.findViewById(R.id.tvAchievementDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.achievement_item, parent, false)
        return AchievementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = achievements[position]

        holder.title.text = achievement.title
        holder.description.text = achievement.description

        if (achievement.isUnlocked) {
             // locked icon
        }
    }

    override fun getItemCount(): Int = achievements.size
}
