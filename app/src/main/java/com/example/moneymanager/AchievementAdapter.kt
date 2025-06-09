package com.example.moneymanager


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView




class AchievementAdapter(
    private var achievements: List<Achievement>
) : RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    class AchievementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.tvAchievementTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.tvAchievementDescription)
        val iconImageView: ImageView = itemView.findViewById(R.id.ivAchievementIcon)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.achievement_item, parent, false)
        return AchievementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = achievements[position]

        holder.titleTextView.text = achievement.title
        holder.descriptionTextView.text = achievement.description

        val alpha = if (achievement.isUnlocked) 1.0f else 0.5f
        holder.itemView.alpha = alpha

        if (achievement.isUnlocked) {
            holder.titleTextView.setTextColor(holder.itemView.context.getColor(android.R.color.white))
        } else {
            holder.titleTextView.setTextColor(holder.itemView.context.getColor(android.R.color.darker_gray))
        }

        // Set achievement icons based on achievement.id
        val iconResId = when (achievement.id) {
            "firststep" -> R.drawable.silver_medal          // First Goal Created
            "goalsetter" -> R.drawable.savings_goal         // Achieve Savings Goal
            "sharpshooter" -> R.drawable.target_goal        // Hit Monthly Budget Goal
            else -> R.drawable.silver_medal
        }

        holder.iconImageView.setImageResource(iconResId)
    }

    override fun getItemCount(): Int = achievements.size

    fun updateAchievements(newAchievements: List<Achievement>) {
        achievements = newAchievements
        notifyDataSetChanged()
    }

}

// ============================== End of file ==============================

