package com.securemypasswords.secureMyPasswords.secure;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.securemypasswords.secureMyPasswords.R;

public class SetupPassword {

    public static TextWatcher getPasswordTextWatcher(final Context context, final EditText mPasswordView, final TextView passwordStrengthIndicator, final EditText mConfirmPasswordView) {
        return new TextWatcher() {
            boolean _ignore = false; // indicates if the change was made by the TextWatcher itself.

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (_ignore)
                    return;

                _ignore = true; // prevent infinite loop

                // Reset errors.
                mPasswordView.setError(null);
                mConfirmPasswordView.setError(null);

                String passwordText = s.toString();

                if (s.length() < 6 || getRating(passwordText) == 0) {
                    passwordStrengthIndicator.setText(R.string.tooSmall_password);
                    passwordStrengthIndicator.setTextColor(context.getResources().getColor(R.color.EASY));
                    if(Build.VERSION.SDK_INT >= 21) {
                        changeBackGroundTint(mPasswordView, context.getResources().getColor(R.color.EASY));
                    }
                } else if (getRating(passwordText) == 1) {
                    passwordStrengthIndicator.setText(R.string.medium_password);
                    passwordStrengthIndicator.setTextColor(context.getResources().getColor(R.color.EASY));
                    if(Build.VERSION.SDK_INT >= 21) {
                        changeBackGroundTint(mPasswordView, context.getResources().getColor(R.color.EASY));
                    }
                } else if (getRating(passwordText) == 2) {
                    passwordStrengthIndicator.setText(R.string.medium_password);
                    passwordStrengthIndicator.setTextColor(context.getResources().getColor(R.color.MEDIUM));
                    if(Build.VERSION.SDK_INT >= 21) {
                        changeBackGroundTint(mPasswordView, context.getResources().getColor(R.color.MEDIUM));
                    }
                } else if (getRating(passwordText) == 3) {
                    passwordStrengthIndicator.setText(R.string.strong_password);
                    passwordStrengthIndicator.setTextColor(context.getResources().getColor(R.color.STRONG));
                    if(Build.VERSION.SDK_INT >= 21) {
                        changeBackGroundTint(mPasswordView, context.getResources().getColor(R.color.STRONG));
                    }
                } else {
                    passwordStrengthIndicator.setText(R.string.strongest_password);
                    passwordStrengthIndicator.setTextColor(context.getResources().getColor(R.color.STRONGEST));
                    if(Build.VERSION.SDK_INT >= 21) {
                        changeBackGroundTint(mPasswordView, context.getResources().getColor(R.color.STRONGEST));
                    }
                }
                _ignore = false; // release, so the TextWatcher start to listen again.
            }
        };
    }

    public static boolean validThePassword(final Context context, EditText mPasswordView, EditText mConfirmPasswordView) {
        final String password = mPasswordView.getText().toString();
        final String confirmPassword = mConfirmPasswordView.getText().toString();
        boolean cancel = false;
        if(password.length() < 6 || SetupPassword.getRating(password)<1){
            mPasswordView.setError(context.getString(R.string.tooSmall_password_error));
            mPasswordView.requestFocus();
            cancel = true;
        }else if(!password.equals(confirmPassword)){
            mPasswordView.setError(context.getString(R.string.password_not_match_error));
            mConfirmPasswordView.setError(context.getString(R.string.password_not_match_error));
            mConfirmPasswordView.requestFocus();
            cancel = true;
        }
        return !cancel;
        /*return */
    }


    @TargetApi(21)
    private static void changeBackGroundTint(EditText password, int Color){
        password.setBackgroundTintList(ColorStateList.valueOf(Color));
    }

    private static float getRating(String password) throws IllegalArgumentException {
        if (password == null) {throw new IllegalArgumentException();}
        int passwordStrength = 0;
        if (password.length() > 5) {passwordStrength++;} // minimal pw length of 6
        if (!password.toLowerCase().equals(password)) {passwordStrength++;} // lower and upper case
        if (password.length() > 8) {passwordStrength++;} // good pw length of 9+
        int numDigits= getNumberDigits(password);
        if (numDigits > 0 && numDigits != password.length()) {passwordStrength++;} // contains digits and non-digits
        return passwordStrength;
    }

    private static int getNumberDigits(String inString){
        if (isEmpty(inString)) {
            return 0;
        }
        int numDigits= 0;
        int length= inString.length();
        for (int i = 0; i < length; i++) {
            if (Character.isDigit(inString.charAt(i))) {
                numDigits++;
            }
        }
        return numDigits;
    }

    private static boolean isEmpty(String inString) {
        return inString == null || inString.length() == 0;
    }

}
