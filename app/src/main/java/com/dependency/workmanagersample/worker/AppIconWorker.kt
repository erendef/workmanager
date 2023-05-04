package com.dependency.workmanagersample.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dependency.workmanagersample.utils.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AppIconWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
    ): Worker(appContext, workerParams) {

    override fun doWork(): Result {
        return try {
            val inputMessage = inputData.getString(KEY_SDK_VERSION_TEXT)
            inputMessage?.let {
                makeStatusNotification(it, applicationContext)
            }
            val type = if (isAliasEnabled(applicationContext, IconType.DEFAULT.cls)) IconType.ALTERNATE
                        else IconType.DEFAULT
            changeAppIcon(type,applicationContext)
            sleep()
            makeStatusNotification(SUCCESS_TEXT, applicationContext)

            Result.success()

        } catch (e: Throwable) {
            e.printStackTrace()
            Result.failure()
        }
    }
}