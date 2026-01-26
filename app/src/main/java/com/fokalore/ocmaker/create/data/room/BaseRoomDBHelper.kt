package com.fokalore.ocmaker.create.data.room

import android.content.Context
import androidx.room.Room
import com.fokalore.ocmaker.create.utils.SingletonHolder


open class BaseRoomDBHelper(context: Context) {
    val db = Room.databaseBuilder(context, AppDB::class.java,"Avatar").build()
    companion object : SingletonHolder<BaseRoomDBHelper, Context>(::BaseRoomDBHelper)
}