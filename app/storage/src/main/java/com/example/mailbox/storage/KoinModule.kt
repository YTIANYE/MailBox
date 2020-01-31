package com.example.mailbox.storage

import com.example.mailbox.mailstore.SchemaDefinitionFactory
import org.koin.dsl.module

val storageModule = module {
    single<SchemaDefinitionFactory> { K9SchemaDefinitionFactory() }
}
