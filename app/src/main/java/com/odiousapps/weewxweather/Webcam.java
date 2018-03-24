package com.odiousapps.weewxweather;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.util.Timer;
import java.util.TimerTask;

public class Webcam extends Activity implements GestureDetector.OnGestureListener
{
    Common common = null;
    GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webcam);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getActionBar();
        if(actionBar != null)
            actionBar.hide();

        common = new Common(this);

        gestureDetector = new GestureDetector(Webcam.this, Webcam.this);

        reloadImageView();
        Common.LogMessage("set things in motion!");

        new ReloadWebView(300);
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    protected void reloadImageView()
    {
        Common.LogMessage("reload webcam...");
        WebView wv = findViewById(R.id.webcam);
        wv.getSettings().setAppCacheEnabled(false);
        wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        wv.getSettings().setUserAgentString(Common.UA);
        wv.clearCache(true);
        String webcam = common.GetStringPref("WEBCAM_URL", "");

        if (webcam == null || webcam.equals(""))
            webcam = "http://mx.cafesydney.com:8888/mjpg/video.mjpg";

        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "  <head>\n" +
                "    <meta charset='utf-8'>\n" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "\t<img style='margin:0px;padding:0px;border:0px;text-align:center;max-width:100%;width:auto;height:auto;'\n" +
                "\tsrc='" + webcam + "?date=" + Math.round(System.currentTimeMillis() / 1000) + "'>\n" +
                "  </body>\n" +
                "</html>";
        wv.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    }

    protected class ReloadWebView extends TimerTask
    {
        Activity context;
        Timer timer;

        private ReloadWebView(int seconds)
        {
            Common.LogMessage("new Timer == "+seconds);
            timer = new Timer();
            timer.schedule(this,0,seconds * 1000);
        }

        @Override
        public void run()
        {
            if(context == null || context.isFinishing())
            {
                // Activity killed
                this.cancel();
                return;
            }

            context.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    reloadImageView();
                }
            });
        }
    }

    @Override
    public boolean onFling(MotionEvent motionEvent1, MotionEvent motionEvent2, float X, float Y)
    {
        if(motionEvent2.getX() - motionEvent1.getX() > 100)
        {
            Common.LogMessage("Swipe Right");
            finish();
            return true;
        }

        return true;
    }

    @Override
    public void onLongPress(MotionEvent arg0)
    {
        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        if(vibrator != null)
        {
            vibrator.vibrate(100);
            reloadImageView();
        }
    }

    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3)
    {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent arg0) {}

    @Override
    public boolean onSingleTapUp(MotionEvent arg0)
    {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    @Override
    public boolean onDown(MotionEvent arg0)
    {
        return false;
    }
}