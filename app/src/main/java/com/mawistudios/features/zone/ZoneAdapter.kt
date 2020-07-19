package com.mawistudios.features.zone

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mawistudios.app.model.Zone
import com.mawistudios.trainer.R
import kotlinx.android.synthetic.main.list_item_zone.view.*


class ZoneItem(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.list_item_zone, this, true)
    }
}

class ZoneAdapter(
    private val onClickRemove: (Zone) -> Unit,
    private val onClickModify: (Zone) -> Unit
) : ListAdapter<Zone, ZoneAdapter.ZoneViewHolder>(DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: ZoneViewHolder, position: Int) {
        getItem(position)?.let { holder.setupView(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZoneViewHolder {
        val item = ZoneItem(parent.context)
        return ZoneViewHolder(item, onClickRemove, onClickModify)
    }

    override fun getItemViewType(position: Int): Int = 0

    class ZoneViewHolder(
        zoneItem: ZoneItem,
        private val onClickRemove: (Zone) -> Unit,
        private val onClickModify: (Zone) -> Unit
    ) : RecyclerView.ViewHolder(zoneItem) {
        fun setupView(zone: Zone) {
            itemView.zone_name.text = zone.name
            itemView.remove_btn.setOnClickListener { this.onClickRemove(zone) }
            itemView.modify_btn.setOnClickListener { this.onClickModify(zone) }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Zone> =
            object : DiffUtil.ItemCallback<Zone>() {
                override fun areItemsTheSame(oldItem: Zone, newItem: Zone): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Zone, newItem: Zone): Boolean {
                    return oldItem.id == newItem.id
                }

            }
    }
}