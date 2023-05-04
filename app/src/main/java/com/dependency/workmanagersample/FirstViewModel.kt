package com.dependency.workmanagersample

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.dependency.workmanagersample.utils.*
import com.dependency.workmanagersample.worker.AppIconWorker
import com.dependency.workmanagersample.worker.ShowNotificationWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class FirstViewModel @Inject constructor(): ViewModel() {
    lateinit var constraints: Constraints
    lateinit var workManager : WorkManager

    val isOnWork = MutableLiveData(false)
    val workRequestType = MutableLiveData(WorkType.NONE)
    lateinit var showNotificationWorkerInputData : Data
    lateinit var outputWorkInfos: LiveData<List<WorkInfo>>

    private val repeatInterval = MutableLiveData<String?>()
    private val flexInterval = MutableLiveData<String?>()

    internal fun setWorkManager(applicationContext: Context) {
        workManager = WorkManager.getInstance(applicationContext)
        outputWorkInfos = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)
        createInputData(applicationContext)
    }

    fun setWorkType(workType: WorkType) {
        workRequestType.postValue(workType)
    }

    fun startIconWork(){
        workRequestType.value?.let { type ->
            when (type) {
                WorkType.PERIODIC -> {
                    startPeriodicIconWork()
                }
                WorkType.ONETIME -> {
                    startOneTimeIconWork()
                }
                else -> {
                    startOneTimeIconWork()
                }
            }
        }
    }

    private fun startPeriodicIconWork(){
        adjustConstraints()
        val iconChangerWork = PeriodicWorkRequestBuilder<AppIconWorker>(
            repeatInterval = repeatInterval.value?.toLong()?: 15, TimeUnit.MINUTES,
            flexTimeInterval = flexInterval.value?.toLong() ?: 10, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag(TAG_OUTPUT)
            .build()
        workManager.enqueueUniquePeriodicWork(
            ICON_CHANGE_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            iconChangerWork
        )
    }

    private fun startOneTimeIconWork(){
        adjustConstraints()
        val showNotificationWorker = OneTimeWorkRequestBuilder<ShowNotificationWorker>()
            .setInputData(showNotificationWorkerInputData)
        var continuation = workManager.beginUniqueWork(
            ICON_CHANGE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            showNotificationWorker.build()
        )

        val changeIconWorkBuilder = OneTimeWorkRequestBuilder<AppIconWorker>()
            .setConstraints(constraints)
            .addTag(TAG_OUTPUT)
        continuation = continuation.then(changeIconWorkBuilder.build())
        continuation.enqueue()
    }

    private fun adjustConstraints(){
        constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(false)
            .setRequiresDeviceIdle(false)
            .setRequiresStorageNotLow(false)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }

    fun stopIconWork() {
        workManager.cancelUniqueWork(ICON_CHANGE_WORK_NAME)
    }

    internal fun createInputData(applicationContext:Context) {
        val type =
            if (isAliasEnabled(applicationContext, IconType.DEFAULT.cls)) IconType.DEFAULT
            else IconType.ALTERNATE

        val message = if (type.equals(IconType.DEFAULT)) ALTERNATE_NOTIFICATION_TEXT
        else DEFAULT_NOTIFICATION_TEXT
        val builder = Data.Builder()
        message.let {
            builder.putString(KEY_ICON_TEXT, it)
        }
        showNotificationWorkerInputData = builder.build()
    }

    enum class WorkType() {
        ONETIME,
        PERIODIC,
        NONE
    }
}