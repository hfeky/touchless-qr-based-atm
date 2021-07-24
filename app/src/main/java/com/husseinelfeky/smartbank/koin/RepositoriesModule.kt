package com.husseinelfeky.smartbank.koin

import com.husseinelfeky.smartbank.repository.AtmRepository
import com.husseinelfeky.smartbank.repository.AuthRepository
import com.husseinelfeky.smartbank.repository.TransactionRepository
import com.husseinelfeky.smartbank.repository.UserRepository
import org.koin.dsl.module

val repositoriesModule = module {
    factory {
        AuthRepository()
    }
    factory {
        UserRepository()
    }
    factory {
        AtmRepository()
    }
    factory {
        TransactionRepository(
            firebaseApiService = get()
        )
    }
}
