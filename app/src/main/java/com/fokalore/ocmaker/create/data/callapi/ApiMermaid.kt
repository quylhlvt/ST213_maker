package com.fokalore.ocmaker.create.data.callapi

import com.fokalore.ocmaker.create.data.model.CharacterResponse
import retrofit2.http.GET

interface ApiMermaid {
    @GET("api/ST194_FolkloreMaker")
    suspend fun getAllData(): CharacterResponse
}