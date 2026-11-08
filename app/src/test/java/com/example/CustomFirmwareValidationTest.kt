package com.example

import com.example.model.CustomFirmwareValidation
import com.example.model.McuArchitecture
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomFirmwareValidationTest {

    @Test
    fun acceptsSupportedExtensionsCaseInsensitively() {
        listOf("firmware.bin", "FIRMWARE.BIN", "app.uf2", "dfu-package.Zip", "my.fw.v2.bin").forEach {
            assertNull("$it should be accepted", CustomFirmwareValidation.rejectionReason(it, 1024L))
        }
    }

    @Test
    fun rejectsUnsupportedOrMissingExtensions() {
        val hex = CustomFirmwareValidation.rejectionReason("firmware.hex", 1024L)
        assertNotNull(hex)
        assertTrue(hex!!.contains(".hex"))

        val none = CustomFirmwareValidation.rejectionReason("firmware", 1024L)
        assertNotNull(none)
        assertTrue(none!!.contains("no extension"))
    }

    @Test
    fun rejectsEmptyAndOversizedFiles() {
        assertNotNull(CustomFirmwareValidation.rejectionReason("a.bin", 0L))
        assertNotNull(
            CustomFirmwareValidation.rejectionReason("a.bin", CustomFirmwareValidation.MAX_FILE_BYTES + 1)
        )
        assertNull(CustomFirmwareValidation.rejectionReason("a.bin", CustomFirmwareValidation.MAX_FILE_BYTES))
    }

    @Test
    fun unknownSizeIsNotRejected() {
        assertNull(CustomFirmwareValidation.rejectionReason("a.bin", null))
    }

    @Test
    fun warnsOnChipMismatch() {
        assertNotNull(CustomFirmwareValidation.compatibilityWarning("x.uf2", McuArchitecture.ESP32_S3))
        assertNotNull(CustomFirmwareValidation.compatibilityWarning("x.bin", McuArchitecture.NRF52840))
        assertNotNull(CustomFirmwareValidation.compatibilityWarning("x.zip", McuArchitecture.RP2040))
    }

    @Test
    fun noWarningOnTypicalPairs() {
        assertNull(CustomFirmwareValidation.compatibilityWarning("x.bin", McuArchitecture.ESP32_S3))
        assertNull(CustomFirmwareValidation.compatibilityWarning("x.zip", McuArchitecture.ESP32_S3))
        assertNull(CustomFirmwareValidation.compatibilityWarning("x.zip", McuArchitecture.NRF52840))
        assertNull(CustomFirmwareValidation.compatibilityWarning("x.uf2", McuArchitecture.NRF52840))
        assertNull(CustomFirmwareValidation.compatibilityWarning("x.uf2", McuArchitecture.RP2040))
    }

    @Test
    fun extensionParsing() {
        assertEquals("bin", CustomFirmwareValidation.extensionOf("a.b.BIN"))
        assertEquals("", CustomFirmwareValidation.extensionOf("noext"))
    }
}
