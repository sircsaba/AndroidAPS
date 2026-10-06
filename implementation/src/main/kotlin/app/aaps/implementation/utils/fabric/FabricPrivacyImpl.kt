package app.aaps.implementation.utils.fabric

import android.content.SharedPreferences
import android.os.Bundle
import app.aaps.core.interfaces.logging.AAPSLogger
import app.aaps.core.interfaces.logging.LTag
import app.aaps.core.interfaces.rx.weardata.EventData
import app.aaps.core.interfaces.utils.fabric.FabricPrivacy
import dagger.Reusable
import javax.inject.Inject

/**
 * OAPS 2026-10-06: Firebase (Analytics and Crashlytics) is removed.
 * Nothing is sent anywhere; events and errors are only written to the local AAPS log.
 */
@Reusable
class FabricPrivacyImpl @Inject constructor(
    private val aapsLogger: AAPSLogger,
    @Suppress("unused") private val sharedPreferences: SharedPreferences
) : FabricPrivacy {

    override fun setUserProperty(key: String, value: String) {
        aapsLogger.debug(LTag.CORE, "User property (local only): $key=$value")
    }

    override fun logCustom(name: String, event: Bundle) {
        aapsLogger.debug(LTag.CORE, "Event (local only): $name $event")
    }

    override fun logCustom(event: String) {
        aapsLogger.debug(LTag.CORE, "Event (local only): $event")
    }

    override fun logMessage(message: String) {
        aapsLogger.info(LTag.CORE, "Log message (local only): $message")
    }

    override fun logException(throwable: Throwable) {
        aapsLogger.error("Exception (local only): ", throwable)
    }

    override fun fabricEnabled(): Boolean = false

    override fun logWearException(wearException: EventData.WearException) {
        aapsLogger.error(LTag.WEAR, "Wear exception (local only) from ${wearException.manufacturer} ${wearException.model}")
    }
}
