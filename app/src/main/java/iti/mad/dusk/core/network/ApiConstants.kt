package iti.mad.dusk.core.network

object ApiConstants {

    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    object Timeout {
        const val CONNECT = 30L
        const val READ = 30L
        const val WRITE = 30L
    }

    object Endpoints {
        const val CURRENT_WEATHER = "weather"
        const val FORECAST = "forecast"
    }

    object Params {
        const val LAT = "lat"
        const val LON = "lon"
        const val API_KEY = "appid"
        const val LANG = "lang"
    }
}