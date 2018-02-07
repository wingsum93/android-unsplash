/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.unsplash.ui.grid

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.bumptech.glide.Glide
import com.example.android.unsplash.R
import com.example.android.unsplash.data.model.Photo
import com.example.android.unsplash.databinding.PhotoItemBinding
import com.example.android.unsplash.ui.ImageSize

import java.util.ArrayList

class PhotoAdapter(context: Context, private val photos: ArrayList<Photo>) : RecyclerView.Adapter<PhotoViewHolder>() {
    private val requestedPhotoWidth: Int
    private val layoutInflater: LayoutInflater

    init {
        requestedPhotoWidth = context.resources.displayMetrics.widthPixels
        layoutInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(DataBindingUtil.inflate<ViewDataBinding>(layoutInflater,
                R.layout.photo_item, parent, false) as PhotoItemBinding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val binding = holder.binding
        val data = photos[position]
        binding.data = data
        binding.executePendingBindings()
        Glide.with(layoutInflater.context)
                .load(holder.binding.data!!.getPhotoUrl(requestedPhotoWidth))
                .placeholder(R.color.placeholder)
                .override(ImageSize.NORMAL[0], ImageSize.NORMAL[1])
                .into(holder.binding.photo)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    override fun getItemId(position: Int): Long {
        return photos[position].id
    }
}
