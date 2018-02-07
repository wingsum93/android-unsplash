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

import android.content.Intent

/**
 * Holding intent extra names and utility methods for intent handling.
 */
object IntentUtil {
    const val FONT_SIZE = "fontSize"
    const val PADDING = "padding"
    const val PHOTO = "photo"
    const val TEXT_COLOR = "color"
    const val RELEVANT_PHOTOS = "relevant"
    const val SELECTED_ITEM_POSITION = "selected"
    const val REQUEST_CODE = R.id.requestCode

    /**
     * Checks if all extras are present in an intent.
     *
     * @param intent The intent to check.
     * @param extras The extras to check for.
     * @return `true` if all extras are present, else `false`.
     */
    fun hasAll(intent: Intent, vararg extras: String): Boolean {
        for (extra in extras) {
            if (!intent.hasExtra(extra)) {
                return false
            }
        }
        return true
    }

    /**
     * Checks if any extra is present in an intent.
     *
     * @param intent The intent to check.
     * @param extras The extras to check for.
     * @return `true` if any checked extra is present, else `false`.
     */
    fun hasAny(intent: Intent, vararg extras: String): Boolean {
        for (extra in extras) {
            if (intent.hasExtra(extra)) {
                return true
            }
        }
        return false
    }
}
