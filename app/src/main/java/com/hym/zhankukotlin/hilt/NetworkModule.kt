package com.hym.zhankukotlin.hilt

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.google.gson.GsonBuilder
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.model.ZkTypeAdapterFactory
import com.hym.zhankukotlin.network.*
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import javax.inject.Singleton

/**
 * @author hehua2008
 * @date 2022/8/4
 */
@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    companion object {
        private const val CLIENT_CACHE_DIR_NAME = "retrofit"
    }

    @Singleton
    @Provides
    fun provideOkHttp(app: Application, cookieManager: CookieManager): OkHttpClient {
        val clientCacheDir = File(app.cacheDir, CLIENT_CACHE_DIR_NAME)
        if (!clientCacheDir.isDirectory) {
            clientCacheDir.deleteRecursively()
            clientCacheDir.mkdirs()
        }
        return OkHttpClient.Builder()
            .cookieJar(cookieManager)
            .addInterceptor(HeaderInterceptor())
            .addNetworkInterceptor(LogInterceptor())
            .cache(Cache(clientCacheDir, 100 * 1024 * 1024))
            //.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888)))
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(Constants.API_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().registerTypeAdapterFactory(ZkTypeAdapterFactory).create()
                )
            )
            .callbackExecutor(Dispatchers.IO.asExecutor())
            .build()
    }

    @Singleton
    @Provides
    fun provideNetworkService(retrofit: Retrofit): NetworkService {
        return retrofit.create(NetworkService::class.java)
    }

    @Singleton
    @Provides
    fun transparentDrawable(app: Application): Drawable {
        return ContextCompat.getDrawable(app, R.drawable.transparent)!!
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface Accessor {
        fun okHttpClient(): OkHttpClient

        fun retrofit(): Retrofit

        fun networkService(): NetworkService

        fun transparentDrawable(): Drawable
    }
}