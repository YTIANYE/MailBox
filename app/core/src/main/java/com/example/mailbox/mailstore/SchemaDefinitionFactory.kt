package com.example.mailbox.mailstore

import com.example.mailbox.mailstore.LockableDatabase.SchemaDefinition

interface SchemaDefinitionFactory {
    val databaseVersion: Int

    fun createSchemaDefinition(migrationsHelper: MigrationsHelper): SchemaDefinition
}
