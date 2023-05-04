package com.dependency.workmanagersample.worker

import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.dependency.workmanagersample.utils.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ShowNotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        return try {
            val inputData = inputData.getString(KEY_ICON_TEXT)
            inputData?.let {
                makeStatusNotification(it, applicationContext)
            }
            sleep()

            val output = workDataOf(KEY_SDK_VERSION_TEXT to
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        SDK_VERSION_Q_TEXT
                    } else {
                        SDK_VERSION_UNDER_Q_TEXT
                    })

            Result.success(output)

        } catch (exception: Exception) {
            Result.failure()
        }

    }
}