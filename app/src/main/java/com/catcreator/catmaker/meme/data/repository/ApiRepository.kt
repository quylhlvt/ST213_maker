package com.catcreator.catmaker.meme.data.repository

import android.util.Log
import com.catcreator.catmaker.meme.data.callapi.ApiHelper
import com.catcreator.catmaker.meme.data.model.CharacterResponse
import com.catcreator.catmaker.meme.utils.CONST
import com.catcreator.catmaker.meme.utils.DataHelper
import javax.inject.Inject

class ApiRepository @Inject constructor(private val apiHelper: ApiHelper) {
    suspend fun getFigure(): CharacterResponse? {
        try {
            CONST.BASE_URL = CONST.BASE_URL_1
            return apiHelper.apiMermaid1.getAllData()
//            return null
        } catch (e: Exception) {
            Log.d(DataHelper.TAG, "getFigure: $e")
            try {
                CONST.BASE_URL = CONST.BASE_URL_2
//                return null
                return apiHelper.apiMermaid2.getAllData()
            } catch (e: Exception) {
                Log.d(DataHelper.TAG, "getFigure: $e")
                return null
            }
        }
    }
}