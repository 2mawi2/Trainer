package com.mawistudios.trainer

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.filters.LargeTest
import androidx.test.filters.SmallTest
import androidx.test.rule.ActivityTestRule
import com.mawistudios.MainActivity


import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
@SmallTest
class MainActivitySystemTests {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    fun should_load_app_context() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.mawistudios.trainer", appContext.packageName)
    }

    @Test
    fun main_activity_should_update_info_text_when_button_discover_was_clicked() {
        onView(withId(R.id.discoverButton)).perform(click())
        onView(withId(R.id.info)).check(matches(withText("discovery started")))
    }
}
