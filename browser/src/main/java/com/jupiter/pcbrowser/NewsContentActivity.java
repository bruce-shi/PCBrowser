package com.jupiter.pcbrowser;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ShareActionProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.EventListener;


public class NewsContentActivity extends Activity {


    private WebView webView;
    private int newsId;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news_content);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        Intent intent = getIntent();
        String title = intent.getStringExtra(PCConstant.NEWS_TITLE);
        String content = intent.getStringExtra(PCConstant.NEWS_CONTENT);
        String time = intent.getStringExtra(PCConstant.NEWS_TIME);
        String url = intent.getStringExtra(PCConstant.NEWS_URL);
        getActionBar().setTitle(title);


        AssetManager am = getResources().getAssets();

        String finalHtml="";
        try {
            InputStream inputStream = am.open("news_template.html");
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            finalHtml = new String(b).replace("$title$",title).
                    replace("$content$", content).
                    replace("$time$", time).replaceAll("$URL$", url);

            inputStream.close();
        }catch (IOException ex){
            ex.printStackTrace();
        }
        webView = (WebView)findViewById(R.id.webView);
        webView.loadDataWithBaseURL(null, finalHtml, "text/html", "utf-8", null);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //finish();
                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news_content, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();

        // Return true to display menu

        return true;
    }
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

}
