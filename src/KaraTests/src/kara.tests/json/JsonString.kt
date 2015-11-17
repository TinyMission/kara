
import kara.jsonString
import org.junit.Test
import kotlin.test.assertEquals

class JsonQuotationTest() {

    @Test fun testSimpleString() {
        val SAMPLE = "Hello World"
        assertEquals(SAMPLE, jsonString(SAMPLE).value)
    }

    @Test fun testUnicodeString() {
        assertEquals("A\\u0080\u00f1\u00fcC\\u0000asd\\u2050", jsonString("A\u0080\u00f1\u00fcC\u0000asd\u2050").value)
    }

    @Test fun testNonPrintableSymbols() {
        assertEquals("\\u0000", jsonString(String(Array<Byte>(1, {0}).toByteArray())).value)
    }

    @Test fun testSpecialSymbols() {
        assertEquals("\\f\\n\\r\\t\\b\\\\\\\"", jsonString("\u000c\n\r\t\b\\\"").value)
    }

    @Test fun ensureNoTabsInJson1() {
        val inputStringWithTab = "��������� ���;?	��� ������ ����"
        val outputJsonWithoutTab = jsonString(inputStringWithTab)
        assertEquals("��������� ���;?\\t��� ������ ����", outputJsonWithoutTab.value)
    }

    @Test fun ensureNoTabsInJson2() {
        val inputStringWithTabs = "Actually these types of questions could be solved using different kinds of pre- and post release researches. We can offer the next options:\n-	Usability testing\n-	A/B testing"
        val outputJsonWithoutTab = jsonString(inputStringWithTabs)
        assertEquals("Actually these types of questions could be solved using different kinds of pre- and post release researches. We can offer the next options:\\n-\\tUsability testing\\n-\\tA/B testing", outputJsonWithoutTab.value)
    }
}