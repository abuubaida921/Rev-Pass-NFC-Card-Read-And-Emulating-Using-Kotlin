

package com.example.rev_pass_testing_using_metrodroid.card

import au.id.micolous.metrodroid.card.cepascompat.CEPASCard
import au.id.micolous.metrodroid.card.classic.ClassicCard
import au.id.micolous.metrodroid.card.desfire.DesfireCard
import au.id.micolous.metrodroid.card.felica.FelicaCard
import au.id.micolous.metrodroid.card.iso7816.ISO7816Card
import au.id.micolous.metrodroid.card.nfcv.NFCVCard
import au.id.micolous.metrodroid.card.ultralight.UltralightCard
import au.id.micolous.metrodroid.multi.logAndSwiftWrap
import au.id.micolous.metrodroid.time.TimestampFull
import au.id.micolous.metrodroid.transit.TransitCurrency
import au.id.micolous.metrodroid.transit.TransitData

import au.id.micolous.metrodroid.transit.TransitIdentity
import au.id.micolous.metrodroid.ui.ListItemInterface
import com.example.rev_pass_testing_using_metrodroid.util.ImmutableByteArray
import kotlinx.serialization.*

abstract class CardProtocol {
    /**
     * Is this a partial or incomplete card read?
     * @return true if there is not complete data in this scan.
     */

    abstract val isPartialRead: Boolean
    /**
     * Gets items to display when manufacturing information is requested for the card.
     */
    open val manufacturingInfo: List<ListItemInterface>?
        get() = null
    /**
     * Gets items to display when raw data is requested for the card.
     */
    open val rawData: List<ListItemInterface>?
        get() = null

    @Transient
    lateinit var scannedAt: TimestampFull
        private set
    @Transient
    lateinit var tagId: ImmutableByteArray
        private set

    /**
     * This is where a card is actually parsed into TransitData compatible data.
     * @return
    */
    abstract fun parseTransitData(): TransitData?

    /**
     * This is where the "transit identity" is parsed, that is, a combination of the card type,
     * and the card's serial number (according to the operator).
     * @return
     */
    abstract fun parseTransitIdentity(): TransitIdentity?

    open fun postCreate(card: Card) {
        scannedAt = card.scannedAt
        tagId = card.tagId
    }
}

@Serializable
class Card(
    val tagId: ImmutableByteArray,
    val scannedAt: TimestampFull,
    val label: String? = null,
    val mifareClassic: ClassicCard? = null,
    val mifareDesfire: DesfireCard? = null,
    val mifareUltralight: UltralightCard? = null,
    val cepasCompat: CEPASCard? = null,
    val felica: FelicaCard? = null,
    val iso7816: ISO7816Card? = null,
    val vicinity: NFCVCard? = null
) {
    val allProtocols: List<CardProtocol>
        get() = listOfNotNull(mifareClassic, mifareDesfire, mifareUltralight, cepasCompat,
                felica, iso7816, vicinity)
    val manufacturingInfo: List<ListItemInterface>?
        get() = allProtocols.mapNotNull { it.manufacturingInfo }.flatten().ifEmpty { null }
    val rawData: List<ListItemInterface>?
        get() = allProtocols.mapNotNull { it.rawData }.flatten().ifEmpty { null }
    val isPartialRead: Boolean
        get() = allProtocols.any { it.isPartialRead }
    val cardType: CardType
        get () = when {
            allProtocols.size > 1 -> CardType.MultiProtocol
            mifareClassic != null -> when (mifareClassic.subType) {
                ClassicCard.SubType.CLASSIC -> CardType.MifareClassic
                ClassicCard.SubType.PLUS -> CardType.MifarePlus
            }
            mifareUltralight != null -> CardType.MifareUltralight
            mifareDesfire != null -> CardType.MifareDesfire
            cepasCompat != null -> CardType.CEPAS
            felica != null -> CardType.FeliCa
            iso7816 != null -> CardType.ISO7816
            vicinity != null -> CardType.Vicinity
            else -> CardType.Unknown
        }

    @Throws(Throwable::class)
    fun parseTransitIdentity(): TransitIdentity? = logAndSwiftWrap("Card", "parseTransitIdentity failed") lam@{
        for (protocol in allProtocols) {
            val td = protocol.parseTransitIdentity()
            if (td != null)
                return@lam td
        }
        return@lam null
    }

    @Throws(Throwable::class)
    fun parseTransitData(): TransitData? = logAndSwiftWrap("Card", "parseTransitData failed") lam@{
        for (protocol in allProtocols) {
            val td = protocol.parseTransitData()
            if (td != null)
                return@lam td
        }
        return@lam null
    }

    // Convenience for Swift interop
    @Suppress("unused")
    val safeBalance : TransitCurrency? by lazy {
        try {
            parseTransitData()?.balances?.first()?.balance
        } catch (e: Exception) {
            null
        }
    }

    // Convenience for Swift interop
    @Suppress("unused")
    val safeTransitIdentity: TransitIdentity? by lazy {
        try {
            parseTransitIdentity()
        } catch (e: Exception) {
            null
        }
    }

    init {
        allProtocols.forEach {
            it.postCreate(this)
        }
    }
}
