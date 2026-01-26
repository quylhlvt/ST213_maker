package com.fokalore.ocmaker.create.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fokalore.ocmaker.create.data.model.AvatarModel
import javax.inject.Singleton

@Singleton
@Database(entities = [AvatarModel::class], version = 1, exportSchema = false)
abstract class AppDB: RoomDatabase() {
    abstract fun dbDao(): Dao
}