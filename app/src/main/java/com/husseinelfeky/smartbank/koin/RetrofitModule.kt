package com.husseinelfeky.smartbank.koin

import com.husseinelfeky.smartbank.model.TransactionStatus
import com.husseinelfeky.smartbank.model.TransactionType
import com.husseinelfeky.smartbank.network.FirebaseApi
import com.husseinelfeky.smartbank.network.FirebaseApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.EnumJsonAdapter
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

val retrofitModule = module {
    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .add(
                TransactionType::class.java,
                EnumJsonAdapter.create(TransactionType::class.java)
                    .withUnknownFallback(TransactionType.WITHDRAWAL)
            )
            .add(
                TransactionStatus::class.java,
                EnumJsonAdapter.create(TransactionStatus::class.java)
                    .withUnknownFallback(TransactionStatus.FAILED)
            )
            .build()
    }
    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }
    single {
        Retrofit.Builder()
            .client(get())
            .baseUrl(FirebaseApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .build()
            .create(FirebaseApiService::class.java)
    }
}
