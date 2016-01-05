package com.leftshift.clipboard.activities;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.leftshift.clipboard.R;
import com.leftshift.clipboard.fragment.LoginFragment;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_ACTION = "EXTRA_ACTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        int loginAction = getIntent().getIntExtra(EXTRA_ACTION, Action.LOGIN);
        Fragment fragment = getResultTargetFragment();
        if (fragment == null) {
            fragment = LoginFragment.newInstance(loginAction);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.login_fragment, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }


    public Fragment getResultTargetFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.login_fragment);
    }

    public static class Action {
        public static final int LOGIN = 2000;
        public static final int LOGOUT = 2001;
        private Action() {}
    }
}
