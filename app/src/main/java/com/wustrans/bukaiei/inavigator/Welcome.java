package com.wustrans.bukaiei.inavigator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by bukaiei on 2017/11/05.
 */

public class Welcome extends Activity implements Runnable {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        new Thread(this).start();
    }

    public void run() {
        try {

            Thread.sleep(1000);

            startActivity(new Intent(Welcome.this, MainActivity.class));

            finish();
        } catch (InterruptedException e) {

        }
    }
}
