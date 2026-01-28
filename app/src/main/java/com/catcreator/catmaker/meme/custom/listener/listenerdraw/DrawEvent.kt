package com.catcreator.catmaker.meme.custom.listener.listenerdraw

import android.view.MotionEvent
import com.catcreator.catmaker.meme.custom.DrawView


interface DrawEvent {
    fun onActionDown(tattooView: DrawView?, event: MotionEvent?)
    fun onActionMove(tattooView: DrawView?, event: MotionEvent?)
    fun onActionUp(tattooView: DrawView?, event: MotionEvent?)
}