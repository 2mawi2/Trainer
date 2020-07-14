package com.mawistudios.features.interval.detail

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mawistudios.app.model.Interval
import com.mawistudios.features.workout.detail.IntervalItem
import kotlinx.android.synthetic.main.list_item_interval.view.*
import java.text.SimpleDateFormat

class IntervalAdapter(
    private val onClickRemove: (Interval) -> Unit,
    private val onClickModify: (Interval) -> Unit
) : ListAdapter<Interval, IntervalAdapter.IntervalViewHolder>(DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: IntervalViewHolder, position: Int) {
        getItem(position)?.let { holder.setupView(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntervalViewHolder {
        val item = IntervalItem(parent.context)
        return IntervalViewHolder(item, onClickRemove, onClickModify)
    }

    override fun getItemViewType(position: Int): Int = 0

    class IntervalViewHolder(
        intervalItem: IntervalItem,
        private val onClickRemove: (Interval) -> Unit,
        private val onClickModify: (Interval) -> Unit
    ) : RecyclerView.ViewHolder(intervalItem) {
        fun setupView(interval: Interval) {
            itemView.interval_duration.text = interval.duration().toString()
            itemView.remove_btn.setOnClickListener { onClickRemove(interval) }
            itemView.modify_btn.setOnClickListener { onClickModify(interval) }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Interval> =
            object : DiffUtil.ItemCallback<Interval>() {
                override fun areItemsTheSame(oldItem: Interval, newItem: Interval): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: Interval, newItem: Interval): Boolean {
                    return oldItem == newItem
                }

            }
    }
}