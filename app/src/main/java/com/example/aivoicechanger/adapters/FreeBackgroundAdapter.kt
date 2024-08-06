package com.example.aivoicechanger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aivoicechanger.R
import com.example.aivoicechanger.interfaces.OnVoiceCallback
import com.example.aivoicechanger.models.BackgroundModel


class FreeBackgroundAdapter(
    private var list: ArrayList<BackgroundModel>,
    val onClick: OnVoiceCallback
) : RecyclerView.Adapter<FreeBackgroundAdapter.PdfListHolder>() {


    class PdfListHolder(holderItem: View) : RecyclerView.ViewHolder(holderItem) {
        val title: TextView? = holderItem.findViewById(R.id.title)
        val iconImg: ImageView? = holderItem.findViewById(R.id.iconImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfListHolder {
        var newLayout: View? = null

        newLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.free_voice_item, parent, false)


        return PdfListHolder(newLayout!!)

    }

    override fun onBindViewHolder(holder: PdfListHolder, position: Int) {
        holder.iconImg!!.setImageResource(list[position].img)
        holder.title!!.text = list[position].title

        if (list[position].select) {
            holder.iconImg.setBackgroundResource(R.drawable.stroke_circle_item)
        } else {
            holder.iconImg.setBackgroundResource(R.drawable.simple_circle_item)
        }
        holder.itemView.setOnClickListener {
            onClick.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}