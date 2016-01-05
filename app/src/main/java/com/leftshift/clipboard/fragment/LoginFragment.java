package com.leftshift.clipboard.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.Scope;
import com.leftshift.clipboard.BaseApplication;
import com.leftshift.clipboard.R;
import com.leftshift.clipboard.activities.LoginActivity;


public class LoginFragment extends Fragment implements
        View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LoginFragment";

    public static final int REQUEST_CODE_RESOLVE_ERR = 1005;
    public static final int REQUEST_CODE_SIGN_IN = 1006;
    private static final String KEY_SIGNIN_BUTTON_CLICKED = "KEY_SIGNIN_BUTTON_CLICKED";
    private static final String WALLET_SCOPE =
            "https://www.googleapis.com/auth/payments.make_payments";

    private ProgressDialog mProgressDialog;
    private GoogleApiClient mGoogleApiClient;
    private int mLoginAction;

    public static LoginFragment newInstance(int loginAction) {
        LoginFragment fragment = new LoginFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(LoginActivity.EXTRA_ACTION, loginAction);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mLoginAction = args.getInt(LoginActivity.EXTRA_ACTION);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(WALLET_SCOPE))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        SignInButton signInButton = (SignInButton) view.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(this);

        view.findViewById(R.id.button_login_bikestore).setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                logIn();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                onSignInClicked();
                break;
            case R.id.button_login_bikestore:
                Toast.makeText(getActivity(), R.string.login_bikestore_message, Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (mLoginAction == LoginActivity.Action.LOGOUT) {
            logOut();
        } else {
            logIn();
        }
    }



    private void onSignInClicked() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent, 1006);
    }

    private void logIn() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            GoogleSignInAccount gsa = opr.get().getSignInAccount();
            Toast.makeText(getActivity(), getString(R.string.welcome_user,
                    gsa.getDisplayName()), Toast.LENGTH_LONG).show();

            ((BaseApplication) getActivity().getApplication()).login(gsa.getEmail());
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        } else {
            Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_LONG).show();
        }
    }

    private void logOut() {
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);

            ((BaseApplication) getActivity().getApplication()).logout();
            Toast.makeText(getActivity(), getString(R.string.logged_out), Toast.LENGTH_LONG).show();
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        } else {
            mLoginAction = LoginActivity.Action.LOGOUT;
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // nothing specifically required here, onConnected will be called when connection resumes
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
