package com.securemypasswords.secureMyPasswords.setupActivityTest;


import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.securemypasswords.secureMyPasswords.R;
import com.securemypasswords.secureMyPasswords.SetupActivity;
import com.securemypasswords.secureMyPasswords.Util;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SetupWithRightPasswordAndVerifyPasswordFileCreationTest {

    @Rule
    public ActivityTestRule<SetupActivity> mActivityTestRule = new ActivityTestRule<>(SetupActivity.class);

    File file;

    @Before
    public void setUp(){
        Util.removePasswordFile();
    }

    @Test
    public void setupWithRightPasswordTest() {
        Context context = InstrumentationRegistry.getTargetContext();
        file = new File(context.getFilesDir(), "securedMyPassword.smp");

        ViewInteraction passwordView = onView(withId(R.id.et_setup_password));
        passwordView.perform(scrollTo(), replaceText("password1"), closeSoftKeyboard());

        ViewInteraction confirmPasswordView = onView(withId(R.id.et_setup_confirmPassword));
        confirmPasswordView.perform(scrollTo(), replaceText("password1"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(withId(R.id.bt_setup_confirm));
        appCompatButton.perform(scrollTo(), click());

        assertThat(file.exists(), is(true));
    }

    @After
    public void clearDataAfterTest(){
        Util.removePasswordFile();
    }
}
