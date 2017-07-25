package com.steeplesoft.sunago.instagram;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.steeplesoft.sunago.R;
import com.steeplesoft.sunago.SunagoUtil;
import com.steeplesoft.sunago.WebLoginActivity;

import org.jinstagram.auth.model.Token;

public class InstagramPreferencesFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_instagram_preferences, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI();
    }

    private void updateUI() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Button button = (Button) getView().findViewById(R.id.connectButton);
                if (!InstagramClient.instance().isAuthenticated()) {
                    button.setText(getString(R.string.login));
                    button.setOnClickListener(new LoginClickListener());
                } else {
                    button.setText(getString(R.string.logout));
                    button.setOnClickListener(new LogoutClickListener(button));
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String code = data.getStringExtra("code");
                new Thread(new InstagramLoginRunnable(code)).start();
            }
        }
    }

    private class LoginClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getContext(), WebLoginActivity.class);
            intent.putExtra("url", InstagramClient.instance().getAuthorizationUrl());
            intent.putExtra("queryParam", "code");
            startActivityForResult(intent, 1);
        }
    }

    private class InstagramLoginRunnable implements Runnable {
        private final String code;

        public InstagramLoginRunnable(String code) {
            this.code = code;
        }

        @Override
        public void run() {
            final InstagramClient instance = InstagramClient.instance();
            Token accessToken = instance.verifyCodeAndGetAccessToken(code);
            instance.authenticateUser(accessToken.getToken(),accessToken.getSecret());

            SharedPreferences.Editor preferences = SunagoUtil.getPreferences().edit();
            preferences.putString(getString(R.string.instagram_token), accessToken.getToken());
            preferences.putString(getString(R.string.instagram_token_secret), accessToken.getSecret());
            preferences.putBoolean(getString(R.string.instagram_authd), true);
            preferences.commit();
            updateUI();
        }
    }

    private class LogoutClickListener implements View.OnClickListener {
        private final Button button;

        public LogoutClickListener(Button button) {
            this.button = button;
        }

        @Override
        public void onClick(View view) {
            final SharedPreferences.Editor editor = SunagoUtil.getPreferences().edit();
            editor.remove(getString(R.string.instagram_token));
            editor.remove(getString(R.string.instagram_token_secret));
            editor.putBoolean(getString(R.string.instagram_authd), false);
            editor.commit();
            button.setText(getString(R.string.login));
            button.setOnClickListener(new LoginClickListener());
        }
    }
}
