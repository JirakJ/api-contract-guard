package com.kei.plugin.validation

import com.kei.plugin.settings.KeiSettings
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ContractRulesTest {

    @Test
    fun `axios method detection`() {
        assertEquals("GET", ContractRules.axiosMethod("axios.get"))
        assertEquals("POST", ContractRules.axiosMethod("this.axios.post"))
        assertEquals("DELETE", ContractRules.axiosMethod("axios.delete"))
        assertNull(ContractRules.axiosMethod("axios.getUri"))
        assertNull(ContractRules.axiosMethod("axios"))
        assertNull(ContractRules.axiosMethod("http.get"))
        assertNull(ContractRules.axiosMethod("myaxios.get"))
    }

    @Test
    fun `only POST PUT PATCH take a body`() {
        assertTrue(ContractRules.methodTakesBody("post"))
        assertTrue(ContractRules.methodTakesBody("PUT"))
        assertTrue(ContractRules.methodTakesBody("PATCH"))
        assertFalse(ContractRules.methodTakesBody("GET"))
        assertFalse(ContractRules.methodTakesBody("DELETE"))
    }

    @Test
    fun `type compatibility`() {
        assertTrue(ContractRules.isTypeCompatible("string", "string"))
        assertTrue(ContractRules.isTypeCompatible("integer", "number"))
        assertFalse(ContractRules.isTypeCompatible("number", "integer"))
        assertFalse(ContractRules.isTypeCompatible("string", "integer"))
        assertTrue(ContractRules.isTypeCompatible("null", "string"))
        assertTrue(ContractRules.isTypeCompatible("boolean", null))
    }

    @Test
    fun `literal type inference`() {
        assertEquals("string", ContractRules.literalType("'x'", quoted = true))
        assertEquals("integer", ContractRules.literalType("42", quoted = false))
        assertEquals("integer", ContractRules.literalType("-7", quoted = false))
        assertEquals("number", ContractRules.literalType("4.5", quoted = false))
        assertEquals("boolean", ContractRules.literalType("true", quoted = false))
        assertEquals("null", ContractRules.literalType("null", quoted = false))
        assertNull(ContractRules.literalType("undefined", quoted = false))
    }

    @Test
    fun `default values per type`() {
        assertEquals("''", ContractRules.defaultValue("string"))
        assertEquals("0", ContractRules.defaultValue("INTEGER"))
        assertEquals("0", ContractRules.defaultValue("number"))
        assertEquals("false", ContractRules.defaultValue("boolean"))
        assertEquals("[]", ContractRules.defaultValue("array"))
        assertEquals("{}", ContractRules.defaultValue("object"))
        assertEquals("null", ContractRules.defaultValue(null))
    }

    @Test
    fun `property keys are quoted when not identifiers`() {
        assertEquals("email", ContractRules.propertyKey("email"))
        assertEquals("\$ref", ContractRules.propertyKey("\$ref"))
        assertEquals("'first-name'", ContractRules.propertyKey("first-name"))
        assertEquals("'it\\'s'", ContractRules.propertyKey("it's"))
    }

    @Test
    fun `appendProperty keeps the object literal valid`() {
        assertEquals("{ email: '' }", ContractRules.appendProperty("{}", "email", "''"))
        assertEquals("{ email: '' }", ContractRules.appendProperty("{   }", "email", "''"))
        assertEquals("{ name: 'J', email: '' }", ContractRules.appendProperty("{ name: 'J' }", "email", "''"))
        assertEquals("{\n  name: 'J', age: 0 }", ContractRules.appendProperty("{\n  name: 'J',\n}", "age", "0"))
        assertEquals("{ a: { b: 1 }, 'x-y': null }", ContractRules.appendProperty("{ a: { b: 1 } }", "x-y", "null"))
    }

    @Test
    fun `appendProperty rejects non-object text`() {
        assertThrows<IllegalArgumentException> { ContractRules.appendProperty("[1, 2]", "a", "0") }
    }
}

class KeiSettingsTest {

    @Test
    fun `defaults enable everything`() {
        val state = KeiSettings().state
        assertTrue(state.enableValidation)
        assertTrue(state.warnOnMissingContract)
        assertTrue(state.autoRefreshContracts)
    }

    @Test
    fun `loadState replaces state`() {
        val settings = KeiSettings()
        settings.loadState(KeiSettings.State(enableValidation = false, warnOnMissingContract = false, autoRefreshContracts = false))
        assertEquals(KeiSettings.State(false, false, false), settings.state)
    }
}
