package com.joseph.coolme.model

interface DownloadObservable {
    fun setDownloadObserver(obj: Any)
    fun notifyDownloadObserver()
}