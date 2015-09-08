
import kara.jsonString
import org.junit.Test
import kotlin.test.assertEquals

class JsonValidatorTest() {

    @Test fun ensureNoTabsInJson1() {
        val inputStringWithTab = "емнадцати лет;?	акт органа опек"
        val outputJsonWithoutTab = jsonString(inputStringWithTab)
        assertEquals("емнадцати лет;?    акт органа опек", outputJsonWithoutTab.value)
    }

    @Test fun ensureNoTabsInJson2() {
        val inputStringWithTabs = "Actually these types of questions could be solved using different kinds of pre- and post release researches. We can offer the next options:\n-	Usability testing\n-	A/B testing"
        val outputJsonWithoutTab = jsonString(inputStringWithTabs)
        assertEquals("Actually these types of questions could be solved using different kinds of pre- and post release researches. We can offer the next options:\\n-    Usability testing\\n-    A/B testing", outputJsonWithoutTab.value)
    }
}