package app.services.scene.factory.entities

import app.components.system.configuration.Configuration
import app.components.system.connection.Connection
import app.components.system.model.SnapshotInfo
import core.entities.Environment
import kotlin.reflect.KClass

class EnvironmentImpl : EntityImpl(), Environment {
    private val configuration: Configuration = super.setComponent(Configuration::class)
    private val connection: Connection = super.setComponent(Connection::class)
    private val snapshotInfo: SnapshotInfo = super.setComponent(SnapshotInfo::class)

    @Suppress("UNCHECKED_CAST")
    override fun <C : Any> getComponent(type: KClass<C>): C? {
        return when (type) {
            SnapshotInfo::class -> snapshotInfo as C
            Configuration::class -> configuration as C
            Connection::class -> connection as C
            else -> super.getComponent(type)
        }
    }
}