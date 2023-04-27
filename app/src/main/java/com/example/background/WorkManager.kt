package com.example.background

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

class WorkManager {
    val job : Job = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.Default + job)

    fun doWork(){
        scope.launch { println("hi") }
    }

    fun cancelWork(){
        scope.coroutineContext.cancelChildren()
        job.cancelChildren()
    }
}

fun main(){
    val mWorkManager = WorkManager()
    mWorkManager.doWork()
    mWorkManager.cancelWork()
}