package com.example.codepathand101project5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView


class ApodAdapter(private var apodList: MutableList<ApodData>, private val onClick: (ApodData) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }

    private var isLoadingAdded = false

    // Update ViewHolder classes
    class ApodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.itemTitle)
        val dateTextView: TextView = itemView.findViewById(R.id.itemDate)
        val imageView: CircleImageView = itemView.findViewById(R.id.itemImage)
    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_apod, parent, false)
            ApodViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    // Update onBindViewHolder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            val apod = apodList[position]
            val itemHolder = holder as ApodViewHolder

            itemHolder.titleTextView.text = apod.title
            itemHolder.dateTextView.text = apod.date

            Glide.with(itemHolder.itemView.context)
                .load(apod.imageUrl)
                .placeholder(R.drawable.ic_launcher)
                .error(R.drawable.ic_launcher)
                .into(itemHolder.imageView)

            itemHolder.itemView.setOnClickListener { onClick(apod) }
        }
    }

    // Update getItemViewType and getItemCount
    override fun getItemViewType(position: Int): Int {
        return if (isLoadingAdded && position == itemCount - 1) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int = apodList.size + if (isLoadingAdded) 1 else 0

    // Helper methods
    fun addLoadingFooter() {
        isLoadingAdded = true
        notifyItemInserted(itemCount - 1)
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        notifyItemRemoved(itemCount)
    }

    fun addAll(newItems: List<ApodData>) {
        val startPos = itemCount
        apodList.addAll(newItems)
        notifyItemRangeInserted(startPos, newItems.size)
    }
}