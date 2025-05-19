package test.compose.zingplayer.api

import test.compose.zingplayer.model.SongSteam

class ZingApi(
    private val httpClient: ZingApiClient,
) {
    suspend fun getChart(): Chart {
        val response = httpClient.get<Chart>("/api/v2/page/get/chart-home")
        return response.data
    }

    suspend fun searchSongs(query: String): SearchResult {
        val response = httpClient.get<SearchResult>("/api/v2/search/multi", mapOf("q" to query))
        return response.data
    }

    suspend fun streamSong(songID: String): SongSteam {
        val response = httpClient.get<SongSteam>("/api/v2/song/get/streaming", mapOf("id" to songID))
        return response.data
    }
}