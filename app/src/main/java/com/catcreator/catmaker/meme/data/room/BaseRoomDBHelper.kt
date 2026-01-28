package com.catcreator.catmaker.meme.data.room

import android.content.Context
import androidx.room.Room
import com.catcreator.catmaker.meme.utils.SingletonHolder


open class BaseRoomDBHelper(context: Context) {
    val db = Room.databaseBuilder(context, AppDB::class.java,"Avatar").build()
    companion object : SingletonHolder<BaseRoomDBHelper, Context>(::BaseRoomDBHelper)
}