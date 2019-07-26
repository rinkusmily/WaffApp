package shrinkcom.waff.com.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;

import shrinkcom.waff.com.R;

public class WebviewActivity extends AppCompatActivity {
    private Context mContext;
    private Toolbar toolbar;

    Intent intent ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        mContext = this;

        intent = getIntent();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("mToolbar"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        WebView mywebview = (WebView) findViewById(R.id.webView);

        if (intent != null)
        {
            mywebview.loadUrl(intent.getStringExtra("link"));

        }
        else
        {
            mywebview.loadUrl("https://www.google.com/");

        }

    }
}
