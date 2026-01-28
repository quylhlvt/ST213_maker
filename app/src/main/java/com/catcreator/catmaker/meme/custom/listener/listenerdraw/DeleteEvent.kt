package com.catcreator.catmaker.meme.custom.listener.listenerdraw

import android.view.MotionEvent
import com.catcreator.catmaker.meme.custom.DrawView

class DeleteEvent : DrawEvent {
    override fun onActionDown(tattooView: DrawView?, event: MotionEvent?) {}
    override fun onActionMove(tattooView: DrawView?, event: MotionEvent?) {}
    override fun onActionUp(tattooView: DrawView?, event: MotionEvent?) {
        if (!tattooView!!.isLocking()) {
            tattooView.removeDrawCurrent()
        }
    }
}
