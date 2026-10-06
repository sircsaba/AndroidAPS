package app.aaps.core.utils.fabric

import java.util.UUID

/**
 * OAPS 2026-10-06: Firebase Installations removed.
 * A random ID is generated locally each time the app starts; it is never sent anywhere.
 */
object InstanceId {

    var instanceId: String = UUID.randomUUID().toString()
}
