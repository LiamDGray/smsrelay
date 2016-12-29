package com.advarisk.otpsender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (!preferences.getBoolean("is_enabled", false) || bundle == null)
            return;

        final Object[] pdusArray = (Object[]) bundle.get("pdus");
        if (pdusArray == null || pdusArray.length == 0)
            return;

        String senderFilter  = preferences.getString("sender_filter",  "").toLowerCase();
        String contentFilter = preferences.getString("content_filter", "").toLowerCase();
        String url           = preferences.getString("url", "");
        String deviceID      = preferences.getString("device_id", "");
        String deviceKey     = preferences.getString("device_secret_key", "");
        String authHeader    = "";

        if (!deviceID.isEmpty() && !deviceKey.isEmpty())
        {
            byte[] authText = (deviceID + ":" + deviceKey).getBytes();
            authHeader = "Basic "+Base64.encodeToString(authText, Base64.NO_WRAP);
        }

        for (int i=0; i<pdusArray.length; i++) {
            SmsMessage message = SmsMessage.createFromPdu((byte[]) pdusArray[i]);

            String sender  = message.getDisplayOriginatingAddress();
            String content = message.getDisplayMessageBody();

            if (!senderFilter.isEmpty() && sender.toLowerCase().indexOf(senderFilter) == -1)
                continue;

            if (!contentFilter.isEmpty() && content.toLowerCase().indexOf(contentFilter) == -1)
                continue;

            new UploadAsyncTask(context).execute(sender, content, url, authHeader);
        }
    }

    private class UploadAsyncTask extends AsyncTask<String, Void, String> {
        private Context context;

        public UploadAsyncTask(Context context)
        {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            String sender  = params[0];
            String content = params[1];
            String url     = params[2];
            String auth    = params[3];


            DefaultHttpClient client = new DefaultHttpClient();
            HttpPut          request = new HttpPut(url);
            if (!auth.isEmpty())
                request.setHeader("Authorization", auth);

            ArrayList<NameValuePair> data = new ArrayList<NameValuePair>(2);
            data.add(new BasicNameValuePair("sender",  sender));
            data.add(new BasicNameValuePair("content", content));
            try {
                request.setEntity(new UrlEncodedFormEntity(data));
                HttpResponse response = client.execute(request);
                int code = response.getStatusLine().getStatusCode();
                if (code == 200)
                    return "OTP from "+sender+" uploaded!";
                else if (code == 403)
                    return "Incorrect Device ID and/or Device Secret Key configured!";
            } catch (Exception e) {
                return "Exception: "+e.toString();
            }

            return "Unexpected error!";
        }

        @Override
        protected void onPostExecute(String status) {
            Toast.makeText(context, status, Toast.LENGTH_LONG).show();
        }
    }
}
