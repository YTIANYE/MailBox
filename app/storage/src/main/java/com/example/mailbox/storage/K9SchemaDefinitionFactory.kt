package com.example.mailbox.storage

import com.example.mailbox.mailstore.LockableDatabase
import com.example.mailbox.mailstore.MigrationsHelper
import com.example.mailbox.mailstore.SchemaDefinitionFactory

class K9SchemaDefinitionFactory : SchemaDefinitionFactory {
    override val databaseVersion = StoreSchemaDefinition.DB_VERSION

    override fun createSchemaDefinition(migrationsHelper: MigrationsHelper): LockableDatabase.SchemaDefinition {
        return StoreSchemaDefinition(migrationsHelper)
    }
}
