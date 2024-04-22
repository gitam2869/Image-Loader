package com.app.imageloader.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.imageloader.data.dataclasses.ContentResponse
import com.app.imageloader.imageloading.ImageLoader
import com.app.imageloader.ui.viewholder.ImageViewHolder

class ImageAdapter(private val imageLoader: ImageLoader) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = "CashValueAdapter"

    private val differCallback = object : DiffUtil.ItemCallback<ContentResponse>() {
        override fun areItemsTheSame(
            oldItem: ContentResponse,
            newItem: ContentResponse
        ): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(
            oldItem: ContentResponse,
            newItem: ContentResponse
        ): Boolean {
            return oldItem === newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImageViewHolder(
            ImageViewHolder.createViewHolder(parent)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val imageViewHolder = holder as ImageViewHolder
        imageViewHolder.bind(
            holder,
            position,
            differ.currentList[position],
            imageLoader
        )
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        val imageViewHolder = holder as ImageViewHolder
        imageViewHolder.cancelLoading(imageLoader, differ.currentList)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<ContentResponse>) {
        differ.submitList(list)
    }

    fun getList(): List<ContentResponse> = differ.currentList
}