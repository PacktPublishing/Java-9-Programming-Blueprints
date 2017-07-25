package com.steeplesoft.sunago.twitter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.steeplesoft.sunago.MainActivity;
import com.steeplesoft.sunago.R;
import com.steeplesoft.sunago.Sunago;
import com.steeplesoft.sunago.SunagoUtil;
import com.steeplesoft.sunago.WebLoginActivity;

import java.util.List;

import twitter4j.TwitterException;
import twitter4j.UserList;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterPreferencesFragment extends Fragment {
    private static final int LOGIN_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_twitter_preferences, container, false);
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
                final View prefsLayout = getView().findViewById(R.id.twitterPrefsLayout);
                if (!SunagoUtil.getPreferences().getBoolean(getString(R.string.twitter_authd), false)) {
                    prefsLayout.setVisibility(View.GONE);
                    button.setText(getString(R.string.login));
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new TwitterAuthenticateTask().execute();
                        }
                    });
                } else {
                    button.setText(getString(R.string.logout));
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TwitterClient.instance().logout();
                            final SharedPreferences.Editor editor = SunagoUtil.getPreferences().edit();
                            editor.remove(getString(R.string.twitter_oauth_token));
                            editor.remove(getString(R.string.twitter_oauth_secret));
                            editor.putBoolean(getString(R.string.twitter_authd), false);
                            editor.commit();
                            updateUI();
                        }
                    });

                    prefsLayout.setVisibility(View.VISIBLE);
                    populateUserList();
                }
            }
        });
    }

    private void populateUserList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ListView lv = (ListView) getView().findViewById(R.id.userListsListView);
                final CheckBox cb = (CheckBox) getView().findViewById(R.id.showHomeTimeline);
                final List<UserList> lists = TwitterClient.instance().getLists();

                cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences.Editor editor = SunagoUtil.getPreferences().edit();
                        editor.putBoolean(getString(R.string.twitter_show_home_timeline), cb.isChecked());
                        editor.commit();
                    }
                });
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cb.setChecked(SunagoUtil.getPreferences().getBoolean(getString(R.string.twitter_show_home_timeline), false));
                        lv.setAdapter(new UserListAdapter(Sunago.getAppContext(), R.layout.user_list_info, lists));
                    }
                });

            }
        }).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                new TwitterLoginAsyncTask().execute(data.getStringExtra("oauth_verifier"));
            }
        }
    }

    private class TwitterAuthenticateTask extends AsyncTask<String, String, RequestToken> {
        @Override
        protected void onPostExecute(RequestToken requestToken) {
            super.onPostExecute(requestToken);

            Intent intent = new Intent(getContext(), WebLoginActivity.class);
            intent.putExtra("url", requestToken.getAuthenticationURL());
            intent.putExtra("queryParam", "oauth_verifier");
            startActivityForResult(intent, LOGIN_REQUEST);
        }

        @Override
        protected RequestToken doInBackground(String... strings) {
            try {
                return TwitterClient.instance().getRequestToken();
            } catch (TwitterException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class TwitterLoginAsyncTask extends AsyncTask<String, String, AccessToken> {
        @Override
        protected AccessToken doInBackground(String... codes) {
            AccessToken accessToken = null;
            if (codes != null && codes.length > 0) {
                String code = codes[0];
                TwitterClient twitterClient = TwitterClient.instance();
                try {
                    accessToken = twitterClient.getAcccessToken(twitterClient.getRequestToken(), code);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                twitterClient.authenticateUser(accessToken.getToken(), accessToken.getTokenSecret());
            }

            return accessToken;
        }

        @Override
        protected void onPostExecute(AccessToken accessToken) {
            if (accessToken != null) {
                SharedPreferences.Editor preferences = SunagoUtil.getPreferences().edit();
                preferences.putString(getString(R.string.twitter_oauth_token), accessToken.getToken());
                preferences.putString(getString(R.string.twitter_oauth_secret), accessToken.getTokenSecret());
                preferences.putBoolean(getString(R.string.twitter_authd), true);
                preferences.commit();
                updateUI();
            }
        }
    }
}
