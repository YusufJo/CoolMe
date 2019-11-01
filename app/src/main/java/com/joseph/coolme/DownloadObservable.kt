package com.joseph.coolme

interface DownloadObservable {
    fun setDownloadObserver(obj: Any)
    fun notifyDownloadObserver()
}