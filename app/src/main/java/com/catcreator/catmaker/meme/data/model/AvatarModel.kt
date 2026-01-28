package com.catcreator.catmaker.meme.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize  //Parcelable
@Entity
data class AvatarModel(
    @PrimaryKey()
    var path : String,
    var pathAvatar : String,
    var online : Boolean?=false,
    var arr : String) : Parcelable, Serializable {
}