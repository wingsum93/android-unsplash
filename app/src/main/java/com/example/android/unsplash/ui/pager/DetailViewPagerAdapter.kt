package com.example.android.unsplash.ui.pager

import android.app.Activity
import android.databinding.DataBindingUtil
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bumptech.glide.Glide
import com.example.android.unsplash.R
import com.example.android.unsplash.R.color.placeholder
import com.example.android.unsplash.data.model.Photo
import com.example.android.unsplash.databinding.DetailViewBinding
import com.example.android.unsplash.ui.DetailSharedElementEnterCallback
import com.example.android.unsplash.ui.ImageSize

/**
 * Adapter for paging detail views.
 */

class DetailViewPagerAdapter(private val host: Activity, private val allPhotos: List<Photo>,
                             private val sharedElementCallback: DetailSharedElementEnterCallback) : PagerAdapter() {
    private val layoutInflater: LayoutInflater
    private val photoWidth: Int

    init {
        layoutInflater = LayoutInflater.from(host)
        photoWidth = host.resources.displayMetrics.widthPixels
    }

    override fun getCount(): Int {
        return allPhotos.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = DataBindingUtil.inflate<DetailViewBinding>(layoutInflater, R.layout.detail_view, container, false)
        binding.data = allPhotos[position]
        onViewBound(binding)
        binding.executePendingBindings()
        container.addView(binding.root)
        return binding
    }

    private fun onViewBound(binding: DetailViewBinding) {
        Glide.with(host)
                .load(binding.data?.getPhotoUrl(photoWidth))
                .placeholder(R.color.placeholder)
                .override(ImageSize.NORMAL[0], ImageSize.NORMAL[1])
                .into(binding.photo)
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        if (`object` is DetailViewBinding) {
            sharedElementCallback.setBinding(`object`)
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return `object` is DetailViewBinding && view == `object`.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView((`object` as DetailViewBinding).root)
    }
}
