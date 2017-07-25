package com.steeplesoft.sunago;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        setTitle("Login");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        final String url = intent.getStringExtra("url");
        final String queryParam = intent.getStringExtra("queryParam");

        WebView webView = (WebView)findViewById(R.id.webView);
        final WebViewClient client = new LoginWebViewClient(queryParam);
        webView.setWebViewClient(client);
        webView.loadUrl(url);
    }

    private class LoginWebViewClient extends WebViewClient {
        private String queryParam;

        public LoginWebViewClient(String queryParam) {
            this.queryParam = queryParam;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            final Uri uri = Uri.parse(url);
            final String value = uri.getQueryParameter(queryParam);
            if (value != null) {
                Intent resultIntent = new Intent();
                for (String name : uri.getQueryParameterNames()) {
                    resultIntent.putExtra(name, uri.getQueryParameter(name));
                }
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
            super.onPageStarted(view, url, favicon);
        }
    }
}
