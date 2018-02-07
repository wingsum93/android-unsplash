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

package com.example.android.unsplash

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.Transition
import android.util.Log
import android.util.Pair
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

import com.example.android.unsplash.data.UnsplashService
import com.example.android.unsplash.data.model.Photo
import com.example.android.unsplash.databinding.PhotoItemBinding
import com.example.android.unsplash.ui.DetailSharedElementEnterCallback
import com.example.android.unsplash.ui.TransitionCallback
import com.example.android.unsplash.ui.grid.GridMarginDecoration
import com.example.android.unsplash.ui.grid.OnItemSelectedListener
import com.example.android.unsplash.ui.grid.PhotoAdapter
import com.example.android.unsplash.ui.grid.PhotoViewHolder

import java.util.ArrayList

import retrofit.Callback
import retrofit.RestAdapter
import retrofit.RetrofitError
import retrofit.client.Response

class MainActivity : Activity() {

    private val sharedExitListener = object : TransitionCallback() {
        override fun onTransitionEnd(transition: Transition) {
            setExitSharedElementCallback(null)
        }
    }

    private var grid: RecyclerView? = null
    private var empty: ProgressBar? = null
    private var relevantPhotos: ArrayList<Photo>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        postponeEnterTransition()
        // Listener to reset shared element exit transition callbacks.
        window.sharedElementExitTransition.addListener(sharedExitListener)

        grid = findViewById(R.id.image_grid) as RecyclerView
        empty = findViewById(android.R.id.empty) as ProgressBar

        setupRecyclerView()

        if (savedInstanceState != null) {
            relevantPhotos = savedInstanceState.getParcelableArrayList(IntentUtil.RELEVANT_PHOTOS)
        }
        displayData()
    }

    private fun displayData() {
        if (relevantPhotos != null) {
            populateGrid()
        } else {
            val unsplashApi = RestAdapter.Builder()
                    .setEndpoint(UnsplashService.ENDPOINT)
                    .build()
                    .create(UnsplashService::class.java)
            unsplashApi.getFeed(object : Callback<List<Photo>> {
                override fun success(photos: List<Photo>, response: Response) {
                    // the first items not interesting to us, get the last <n>
                    relevantPhotos = ArrayList(photos.subList(photos.size - PHOTO_COUNT,
                            photos.size))
                    populateGrid()
                }

                override fun failure(error: RetrofitError) {
                    Log.e(TAG, "Error retrieving Unsplash feed:", error)
                }
            })
        }
    }

    private fun populateGrid() {
        grid!!.adapter = PhotoAdapter(this, relevantPhotos!!)
        grid!!.addOnItemTouchListener(object : OnItemSelectedListener(this@MainActivity) {
            override fun onItemSelected(holder: RecyclerView.ViewHolder?, position: Int) {
                if (holder !is PhotoViewHolder) {
                    return
                }
                val binding = holder.binding
                val intent = getDetailActivityStartIntent(this@MainActivity,
                        relevantPhotos, position, binding)
                val activityOptions = getActivityOptions(binding)

                this@MainActivity.startActivityForResult(intent, IntentUtil.REQUEST_CODE,
                        activityOptions.toBundle())
            }
        })
        empty!!.visibility = View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(IntentUtil.RELEVANT_PHOTOS, relevantPhotos)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        postponeEnterTransition()
        // Start the postponed transition when the recycler view is ready to be drawn.
        grid!!.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                grid!!.viewTreeObserver.removeOnPreDrawListener(this)
                startPostponedEnterTransition()
                return true
            }
        })

        if (data == null) {
            return
        }

        val selectedItem = data.getIntExtra(IntentUtil.SELECTED_ITEM_POSITION, 0)
        grid!!.scrollToPosition(selectedItem)

        val holder = grid!!.findViewHolderForAdapterPosition(selectedItem) as PhotoViewHolder?
        if (holder == null) {
            Log.w(TAG, "onActivityReenter: Holder is null, remapping cancelled.")
            return
        }
        val callback = DetailSharedElementEnterCallback(intent)
        callback.setBinding(holder.binding)
        setExitSharedElementCallback(callback)
    }

    private fun setupRecyclerView() {
        val gridLayoutManager = grid!!.layoutManager as GridLayoutManager
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                /* emulating https://material-design.storage.googleapis.com/publish/material_v_4/material_ext_publish/0B6Okdz75tqQsck9lUkgxNVZza1U/style_imagery_integration_scale1.png */
                when (position % 6) {
                    5 -> return 3
                    3 -> return 2
                    else -> return 1
                }
            }
        }
        grid!!.addItemDecoration(GridMarginDecoration(
                resources.getDimensionPixelSize(R.dimen.grid_item_spacing)))
        grid!!.setHasFixedSize(true)

    }

    private fun getActivityOptions(binding: PhotoItemBinding): ActivityOptions {
        val authorPair = Pair.create<View, String>(binding.author, binding.author.transitionName)
        val photoPair = Pair.create<View, String>(binding.photo, binding.photo.transitionName)
        val decorView = window.decorView
        val statusBackground = decorView.findViewById(android.R.id.statusBarBackground)
        val navBackground = decorView.findViewById(android.R.id.navigationBarBackground)
        val statusPair = Pair.create(statusBackground,
                statusBackground.transitionName)

        val options: ActivityOptions
        if (navBackground == null) {
            options = ActivityOptions.makeSceneTransitionAnimation(this,
                    authorPair, photoPair, statusPair)
        } else {
            val navPair = Pair.create(navBackground, navBackground.transitionName)
            options = ActivityOptions.makeSceneTransitionAnimation(this,
                    authorPair, photoPair, statusPair, navPair)
        }
        return options
    }

    companion object {

        private val PHOTO_COUNT = 12
        private val TAG = "MainActivity"

        private fun getDetailActivityStartIntent(host: Activity, photos: ArrayList<Photo>?,
                                                 position: Int, binding: PhotoItemBinding): Intent {
            val intent = Intent(host, DetailActivity::class.java)
            intent.action = Intent.ACTION_VIEW
            intent.putParcelableArrayListExtra(IntentUtil.PHOTO, photos)
            intent.putExtra(IntentUtil.SELECTED_ITEM_POSITION, position)
            intent.putExtra(IntentUtil.FONT_SIZE, binding.author.textSize)
            intent.putExtra(IntentUtil.PADDING,
                    Rect(binding.author.paddingLeft,
                            binding.author.paddingTop,
                            binding.author.paddingRight,
                            binding.author.paddingBottom))
            intent.putExtra(IntentUtil.TEXT_COLOR, binding.author.currentTextColor)
            return intent
        }
    }
}
