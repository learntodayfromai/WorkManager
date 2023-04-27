/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.example.background

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkInfo
import com.example.background.databinding.ActivityBlurBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.lang.Exception

class BlurActivity : AppCompatActivity() {

    private val viewModel: BlurViewModel by viewModels {
        BlurViewModel.BlurViewModelFactory(
            application
        )
    }
    private lateinit var binding: ActivityBlurBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlurBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goButton.setOnClickListener { viewModel.applyBlur(blurLevel) }

        // Observe work status, added in onCreate()
        viewModel.outputWorkInfos.observe(this, workInfosObserver())

        tryCatchWithLaunch4()
        /*lifecycleScope.launch {
            try{
                coroutineScope {
                    val task = async{

                    }
                    task.await()
                }
            }catch(e:Exception){

            }
        }*/
    }

    fun tryCatchWithLaunch1() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                println("trycatch1")
                throw IllegalStateException("Error thrown in tryCatchWithLaunch")
            } catch (e: Exception) {
                println("!!! Handle Exception $e")
            }
        }
    }

    fun tryCatchWithLaunch2() {
        lifecycleScope.launch(coroutineExceptionHandler+Dispatchers.IO) {
            try {
                println("trycatch1")
                launch {
                    throw IllegalStateException("Error thrown in tryCatchWithLaunch")
                }
            } catch (e: Exception) {
                println("!!! Handle Exception $e")
            }
        }
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable -> println("exception handler $throwable") }

    fun tryCatchWithLaunch3() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                println("trycatch1")
                coroutineScope {
                    launch {
                        throw IllegalStateException("Error thrown in tryCatchWithLaunch")
                    }
                }

            } catch (e: Exception) {
                println("!!! Handle Exception $e")
            }
        }
    }

    fun tryCatchWithLaunch4() {
        lifecycleScope.launch(Dispatchers.IO) {
            val deferredResult = async {
                throw IllegalStateException("Error thrown in tryCatchWithAsyncAwait")
            }

            try {
                deferredResult.await()
            }  catch (e: Exception) {
                println("Handle Exception $e")
            }
        }
    }
    // Define the observer function
    private fun workInfosObserver(): Observer<List<WorkInfo>> {
        return Observer { listOfWorkInfo ->

            // Note that these next few lines grab a single WorkInfo if it exists
            // This code could be in a Transformation in the ViewModel; they are included here
            // so that the entire process of displaying a WorkInfo is in one location.

            // If there are no matching work info, do nothing
            if (listOfWorkInfo.isNullOrEmpty()) {
                return@Observer
            }

            // We only care about the one output status.
            // Every continuation has only one worker tagged TAG_OUTPUT
            val workInfo = listOfWorkInfo[0]

            if (workInfo.state.isFinished) {
                showWorkFinished()
            } else {
                showWorkInProgress()
            }
        }
    }
        /**
     * Shows and hides views for when the Activity is processing an image
     */
    private fun showWorkInProgress() {
        with(binding) {
            progressBar.visibility = View.VISIBLE
            cancelButton.visibility = View.VISIBLE
            goButton.visibility = View.GONE
            seeFileButton.visibility = View.GONE
        }
    }

    /**
     * Shows and hides views for when the Activity is done processing an image
     */
    private fun showWorkFinished() {
        with(binding) {
            progressBar.visibility = View.GONE
            cancelButton.visibility = View.GONE
            goButton.visibility = View.VISIBLE
        }
    }

    private val blurLevel: Int
        get() =
            when (binding.radioBlurGroup.checkedRadioButtonId) {
                R.id.radio_blur_lv_1 -> 1
                R.id.radio_blur_lv_2 -> 2
                R.id.radio_blur_lv_3 -> 3
                else -> 1
            }
}
