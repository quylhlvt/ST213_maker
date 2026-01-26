package com.fokalore.ocmaker.create.custom.listener.listenerdraw

import android.view.MotionEvent
import com.fokalore.ocmaker.create.custom.DrawView

class EditEvent : DrawEvent {
    override fun onActionDown(tattooView: DrawView?, event: MotionEvent?) {}
    override fun onActionMove(tattooView: DrawView?, event: MotionEvent?) {}
    override fun onActionUp(tattooView: DrawView?, event: MotionEvent?) {
        if (!tattooView!!.isLocking()) {
            tattooView.editText()
        }
    }
}
