package com.catcreator.catmaker.meme.data.callapi

import com.catcreator.catmaker.meme.data.model.CharacterResponse
import retrofit2.http.GET

interface ApiMermaid {
    @GET("api/ST213_CatMeme")
    suspend fun getAllData(): CharacterResponse
}