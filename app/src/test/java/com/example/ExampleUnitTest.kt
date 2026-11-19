package com.example

import com.example.model.ChildRegistrationDraft
import com.example.model.LearningPackage
import com.example.model.TomoyoFormatters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun formatRupiah_and_parseRupiah_roundTrip() {
        assertEquals("Rp 250.000", TomoyoFormatters.formatRupiah(250000.0))
        assertEquals("-", TomoyoFormatters.formatRupiah(null))
        assertEquals(250000.0, TomoyoFormatters.parseRupiahAmount("Rp 250.000,-"))
        assertNull(TomoyoFormatters.parseRupiahAmount(""))
    }

    @Test
    fun getPackagePriceForBranch_usesBranchSpecificRates() {
        val pkg = LearningPackage(
            slug = "paket-1",
            pricePusat = 300000.0,
            priceCabang = 250000.0
        )
        assertEquals(300000.0, TomoyoFormatters.getPackagePriceForBranch(pkg, "indramayu-kota"))
        assertEquals(250000.0, TomoyoFormatters.getPackagePriceForBranch(pkg, "terisi"))
    }

    @Test
    fun validateRegistrationSubmission_rejectsInvalidAndAcceptsValidMultiChild() {
        val invalid = TomoyoFormatters.validateRegistrationSubmission(
            parentName = "A",
            parentPhone = "123",
            children = listOf(ChildRegistrationDraft(childName = "", gradeLevel = ""))
        )
        assertTrue(invalid.isNotEmpty())

        val valid = TomoyoFormatters.validateRegistrationSubmission(
            parentName = "Ibu Siti Aminah",
            parentPhone = "081234567890",
            children = listOf(
                ChildRegistrationDraft(
                    childName = "Budi Santoso",
                    childGender = "Laki-laki",
                    gradeLevel = "Kelas 3 SD",
                    packageId = "paket-2",
                    branchId = "indramayu-kota"
                ),
                ChildRegistrationDraft(
                    childName = "Rani Santoso",
                    childGender = "Perempuan",
                    gradeLevel = "TK B",
                    packageId = "paket-1",
                    branchId = "terisi"
                )
            )
        )
        assertTrue(valid.isEmpty())
    }

    @Test
    fun isPaymentDueNow_handlesConfirmedAndPastDates() {
        assertFalse(TomoyoFormatters.isPaymentDueNow(null, null))
        assertTrue(TomoyoFormatters.isPaymentDueNow("2020-01-01T00:00:00.000Z", null))
        assertFalse(
            TomoyoFormatters.isPaymentDueNow(
                "2020-01-01T00:00:00.000Z",
                "2020-01-02T00:00:00.000Z"
            )
        )
    }
}
