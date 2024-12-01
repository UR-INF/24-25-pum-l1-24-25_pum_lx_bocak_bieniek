package com.focuszone.domain

// monitor application activity if any was limited
object AppMonitor {

    /* Listen any opened app and check if its on the limited list
    * */
    fun monitorOpenedApps(){
        // create app process listener
        // fetch async list from DB
        // compare if app is present in blocked list
        // monitor/block app in loop
    }

    /* Block given app to the end of the day
    * */
    fun blockApp(){
        // get app ID/name/package name/etc
        // show fullscreen message
        // tell system to block app
    }
}