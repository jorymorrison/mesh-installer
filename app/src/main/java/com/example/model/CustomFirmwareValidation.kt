package com.example.model

import java.util.Locale

/**
 * Pure checks for user-supplied ("custom") firmware files, kept free of Android types so they
 * can be unit tested on the JVM.
 */
object CustomFirmwareValidation {

    /** Extensions the flasher knows how to handle. */
    val SUPPORTED_EXTENSIONS = setOf("bin", "uf2", "zip")

    /** Largest file we will read into memory (the biggest supported board has 16 MB of flash). */
    const val MAX_FILE_BYTES: Long = 16L * 1024 * 1024

    fun extensionOf(fileName: String): String =
        fileName.substringAfterLast('.', "").lowercase(Locale.US)

    /**
     * Returns an error message if the file must be rejected before it is read, or null if it is
     * acceptable. [sizeBytes] may be null when the content provider does not report a size.
     */
    fun rejectionReason(fileName: String, sizeBytes: Long?): String? {
        val ext = extensionOf(fileName)
        if (ext !in SUPPORTED_EXTENSIONS) {
            val shown = if (ext.isEmpty()) "a file with no extension" else "a .$ext file"
            return "Can't use $shown. Pick a .bin, .uf2 or .zip firmware file."
        }
        if (sizeBytes != null && sizeBytes == 0L) {
            return "$fileName is empty."
        }
        if (sizeBytes != null && sizeBytes > MAX_FILE_BYTES) {
            return "$fileName is ${sizeBytes / (1024 * 1024)} MB, larger than any supported board's flash (16 MB)."
        }
        return null
    }

    /**
     * Returns a warning if the file type doesn't usually match the board's chip. These are warnings,
     * not errors: unusual boards and hand-built images exist, so the user can still flash.
     */
    fun compatibilityWarning(fileName: String, mcu: McuArchitecture): String? {
        val ext = extensionOf(fileName)
        return when (mcu) {
            McuArchitecture.NRF52840 -> if (ext == "bin") {
                "nRF52 boards normally take a DFU .zip or a .uf2, not a raw .bin."
            } else null
            McuArchitecture.RP2040 -> if (ext != "uf2") {
                "RP2040 boards normally take a .uf2 file."
            } else null
            else -> if (ext == "uf2") {
                "${mcu.shortName} boards normally take a .bin (or a release .zip), not a .uf2."
            } else null
        }
    }
}
