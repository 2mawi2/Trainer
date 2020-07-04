package com.mawistudios.features.workout

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.mawistudios.app.model.Workout
import com.mawistudios.trainer.R
import kotlinx.android.synthetic.main.list_item_workout.view.*
import java.text.SimpleDateFormat

class WorkoutItem(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.list_item_workout, this, true)
    }
}

class WorkoutAdapter :
    ListAdapter<Workout, WorkoutAdapter.WorkoutViewHolder>(DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        getItem(position)?.let { holder.setupView(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val item = WorkoutItem(parent.context)
        val matchParent = ViewGroup.LayoutParams.MATCH_PARENT
        item.layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
        return WorkoutViewHolder(item)
    }

    override fun getItemViewType(position: Int): Int = 0

    class WorkoutViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        fun setupView(workout: Workout) {
            itemView.workout_name.text = workout.name
            itemView.workout_date.text = SimpleDateFormat("dd/MM/yyyy").format(workout.createdDate)
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Workout> =
            object : DiffUtil.ItemCallback<Workout>() {
                override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
                    return oldItem.id == newItem.id
                }

            }
    }
}