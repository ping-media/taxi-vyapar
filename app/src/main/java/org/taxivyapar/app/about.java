package org.taxivyapar.app;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class about extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        webView = findViewById(R.id.webview);

        webView.getSettings().setJavaScriptEnabled(true);

        String htmlData = "<html><body><h1>Privacy Policy</h1><p>This is a paragraph.</p></body></html>";
        webView.loadData(profileContainer.privacy, "text/html", "UTF-8");


    }
}