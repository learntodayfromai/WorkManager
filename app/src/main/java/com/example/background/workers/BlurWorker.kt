package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.R
import java.lang.Exception
private const val TAG = "BlurWorker"
class BlurWorker(ctx: Context, params: WorkerParameters) : Worker(ctx,params){
    override fun doWork(): Result {
        val appContext = applicationContext
        val resourceUri = inputData.getString(KEY_IMAGE_URI)
        makeStatusNotification("Blurring image", appContext)

        sleep()

        return try{
            if (TextUtils.isEmpty(resourceUri))
            {
                throw IllegalArgumentException("invalid argument")
            }

            val resolver = appContext.contentResolver

            val picture = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(resourceUri)))

            val blurred = blurBitmap(picture,appContext)
            val uri=writeBitmapToFile(appContext,blurred)
            makeStatusNotification(uri.toString(),appContext)
            val outputData = workDataOf(KEY_IMAGE_URI to uri.toString())

            Result.success(outputData)
        }catch (e:Exception){
            Log.e(TAG,"error applying blur")
            Result.failure()
        }

    }
}
