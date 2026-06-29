package com.v2ray.ang.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.v2ray.ang.R

class SettingsAdapter(
    private val data: List<SettingsNewActivity.SettingItem>,
    private val onClick: (SettingsNewActivity.SettingItem) -> Unit
) : RecyclerView.Adapter<SettingsAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val ivIcon: ImageView = v.findViewById(R.id.ivIcon)
        val tvTitle: TextView = v.findViewById(R.id.tvTitle)
        val ivArrow: ImageView = v.findViewById(R.id.ivArrow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_setting, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = data[position]
        holder.ivIcon.setImageResource(item.icon)
        holder.tvTitle.text = item.title + (item.subtitle?.let { "  $it" } ?: "")
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = data.size
}
