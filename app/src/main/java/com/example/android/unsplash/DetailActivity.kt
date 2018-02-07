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
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.transition.Fade
import android.transition.Slide
import android.transition.TransitionSet
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toolbar

import com.example.android.unsplash.data.model.Photo
import com.example.android.unsplash.ui.DetailSharedElementEnterCallback
import com.example.android.unsplash.ui.pager.DetailViewPagerAdapter

import java.util.ArrayList

class DetailActivity : Activity() {
    private var viewPager: ViewPager? = null
    private var initialItem: Int = 0
    private val navigationOnClickListener = View.OnClickListener { finishAfterTransition() }
    private var sharedElementCallback: DetailSharedElementEnterCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_detail)

        postponeEnterTransition()

        val transitions = TransitionSet()
        val slide = Slide(Gravity.BOTTOM)
        slide.interpolator = AnimationUtils.loadInterpolator(this,
                android.R.interpolator.linear_out_slow_in)
        slide.duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        transitions.addTransition(slide)
        transitions.addTransition(Fade())
        window.enterTransition = transitions

        val intent = intent
        sharedElementCallback = DetailSharedElementEnterCallback(intent)
        setEnterSharedElementCallback(sharedElementCallback)
        initialItem = intent.getIntExtra(IntentUtil.SELECTED_ITEM_POSITION, 0)
        setUpViewPager(intent.getParcelableArrayListExtra(IntentUtil.PHOTO))

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.setNavigationOnClickListener(navigationOnClickListener)

        super.onCreate(savedInstanceState)
    }

    private fun setUpViewPager(photos: ArrayList<Photo>) {
        viewPager = findViewById(R.id.pager) as ViewPager
        viewPager!!.adapter = DetailViewPagerAdapter(this, photos, sharedElementCallback!!)
        viewPager!!.currentItem = initialItem

        viewPager!!.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int,
                                        oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                if (viewPager!!.childCount > 0) {
                    viewPager!!.removeOnLayoutChangeListener(this)
                    startPostponedEnterTransition()
                }
            }
        })

        viewPager!!.pageMargin = resources.getDimensionPixelSize(R.dimen.padding_mini)
        viewPager!!.setPageMarginDrawable(R.drawable.page_margin)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_INITIAL_ITEM, initialItem)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        initialItem = savedInstanceState.getInt(STATE_INITIAL_ITEM, 0)
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onBackPressed() {
        setActivityResult()
        super.onBackPressed()
    }

    override fun finishAfterTransition() {
        setActivityResult()
        super.finishAfterTransition()
    }

    private fun setActivityResult() {
        if (initialItem == viewPager!!.currentItem) {
            setResult(Activity.RESULT_OK)
            return
        }
        val intent = Intent()
        intent.putExtra(IntentUtil.SELECTED_ITEM_POSITION, viewPager!!.currentItem)
        setResult(Activity.RESULT_OK, intent)
    }

    companion object {

        private val STATE_INITIAL_ITEM = "initial"
    }

}
