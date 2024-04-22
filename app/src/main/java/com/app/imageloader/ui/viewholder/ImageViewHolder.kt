package com.app.imageloader.ui.viewholder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.imageloader.R
import com.app.imageloader.data.dataclasses.ContentResponse
import com.app.imageloader.databinding.ItemImageBinding
import com.app.imageloader.imageloading.ImageLoader
import com.app.imageloader.utils.UrlBuilder

class ImageViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    private val binding = ItemImageBinding.bind(itemView)

    @SuppressLint("SetTextI18n")
    fun bind(
        holder: RecyclerView.ViewHolder,
        position: Int,
        contentResponse: ContentResponse,
        imageLoader: ImageLoader
    ) {
        binding.run {
            contentResponse.thumbnail?.let {
                imageLoader.loadImage(
                    UrlBuilder.prepareImageUrl(it.domain, it.basePath, "0", it.key),
                    R.drawable.placeholder,
                    R.drawable.error_placeholder,
                    ivImage
                )
            }
        }
    }

    fun cancelLoading(imageLoader: ImageLoader, currentList: MutableList<ContentResponse>) {
        imageLoader.cancelLoading(binding.ivImage)
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): View {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image, parent, false)
            return view
        }
    }
}