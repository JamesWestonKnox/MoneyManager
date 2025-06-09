import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanager.Goal
import com.example.moneymanager.R

class GoalHomeAdapter(private val goalList: List<Goal>) :
    RecyclerView.Adapter<GoalHomeAdapter.GoalViewHolder>() {

    class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvGoalName: TextView = itemView.findViewById(R.id.tvGoalName)
        val pbGoalProgress: ProgressBar = itemView.findViewById(R.id.pbGoalProgress)
        val tvGoalPercentage: TextView = itemView.findViewById(R.id.tvGoalPercentage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.goal_item_home, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goalList[position]
        holder.tvGoalName.text = goal.goalName

        val progress = if (goal.amount != 0.0) {
            ((goal.totalAmount / goal.amount) * 100).toInt()
        } else 0
        holder.pbGoalProgress.progress = progress
        holder.tvGoalPercentage.text = "Goal Progress: $progress%"
    }

    override fun getItemCount() = goalList.size
}
