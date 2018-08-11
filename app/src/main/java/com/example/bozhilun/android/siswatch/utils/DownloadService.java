package com.example.bozhilun.android.siswatch.utils;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.util.ToastUtil;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadService extends IntentService {

    public static final int UPDATE_PROGRESS = 8344;

    public DownloadService() {
        super("DownloadService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String urlToDownload = intent.getStringExtra("url");
        Log.e("DownService","---urlTol="+urlToDownload);
        if (urlToDownload != null) {
            // Log.e(TAG, "---------urlToDownload"+urlToDownload);
            String fileDestination = intent.getStringExtra("dest");
            ResultReceiver receiver =  intent
                    .getParcelableExtra("receiver");
            try {
                URL url = new URL(urlToDownload);
                URLConnection connection = url.openConnection();
                connection.connect();
                // this will be useful so that you can show a typical 0-100%
                // progress bar
                int fileLength = connection.getContentLength();
                // download the file
                InputStream input = new BufferedInputStream(
                        connection.getInputStream());
                OutputStream output = new FileOutputStream(fileDestination);
                byte data[] = new byte[100];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    Bundle resultData = new Bundle();
                    resultData.putInt("progress", (int) (total * 100 / fileLength));
                    receiver.send(UPDATE_PROGRESS, resultData);
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bundle resultData = new Bundle();
            resultData.putInt("progress", 100);
            receiver.send(UPDATE_PROGRESS, resultData);
        } else {
            ToastUtil.showToast(MyApp.getContext(),"下载地址有误!");
        }

    }

}
