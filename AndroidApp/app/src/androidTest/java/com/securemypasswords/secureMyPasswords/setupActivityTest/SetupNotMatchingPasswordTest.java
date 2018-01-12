package com.securemypasswords.secureMyPasswords.setupActivityTest;


import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.securemypasswords.secureMyPasswords.R;
import com.securemypasswords.secureMyPasswords.SetupActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SetupNotMatchingPasswordTest {

    @Rule
    public ActivityTestRule<SetupActivity> mActivityTestRule = new ActivityTestRule<>(SetupActivity.class);

    @Test
    public void setupNotMatchingPasswordTest() {
        Context context = InstrumentationRegistry.getTargetContext();
        ViewInteraction passwordView = onView(ViewMatchers.withId(R.id.et_setup_password));
        passwordView.perform(scrollTo(), replaceText("password1"), closeSoftKeyboard());

        ViewInteraction confirmPasswordView = onView(withId(R.id.et_setup_confirmPassword));
        confirmPasswordView.perform(scrollTo(), replaceText("password2"), closeSoftKeyboard());

        ViewInteraction confirmButton = onView(withId(R.id.bt_setup_confirm));
        confirmButton.perform(scrollTo(), click());

        passwordView.check(matches(hasErrorText(context.getString(R.string.password_not_match_error))));
        confirmPasswordView.check(matches(hasErrorText(context.getString(R.string.password_not_match_error))));
    }
}
