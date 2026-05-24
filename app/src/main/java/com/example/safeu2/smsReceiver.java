package com.example.safeu2;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.safeu2.api.ApiClient;
import com.example.safeu2.api.ApiService;
import com.example.safeu2.api.PredictionRequest;
import com.example.safeu2.api.PredictionResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class smsReceiver extends BroadcastReceiver {
    private static final String TAG = "smsReceiver";
    public static final String pdu_type = "pdus";
    private static final String CHANNEL_ID = "PhishingAlertChannel";

    public smsReceiver() {
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        SmsMessage[] msgs;
        String format = bundle.getString("format");
        Object[] pdus = (Object[]) bundle.get(pdu_type);

        if (pdus != null) {
            boolean isVersionM = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
            msgs = new SmsMessage[pdus.length];

            for (int i = 0; i < msgs.length; i++) {
                if (isVersionM) {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                } else {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }

                String sender = msgs[i].getOriginatingAddress();
                String messageBody = msgs[i].getMessageBody();

                Log.d(TAG, "SMS Received from: " + sender);

                String extractedUrl = extractUrl(messageBody);
                if (extractedUrl != null) {
                    Log.d(TAG, "Extracted URL: " + extractedUrl);
                    verifyUrlWithBackend(context, sender, extractedUrl);
                }
            }
        }
    }

    private String extractUrl(String messageWithUrl) {
        if (messageWithUrl == null) return null;
        // Robust regex for URLs
        Pattern pattern = Pattern.compile("(?i)\\b(?:https?://|www\\.)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
        Matcher matcher = pattern.matcher(messageWithUrl);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private void verifyUrlWithBackend(final Context context, final String sender, final String url) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        PredictionRequest request = new PredictionRequest(url);

        apiService.predictUrl(request).enqueue(new Callback<PredictionResponse>() {
            @Override
            public void onResponse(Call<PredictionResponse> call, Response<PredictionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PredictionResponse result = response.body();
                    String output = result.getOutput();
                    double confidence = result.getConfidence() * 100;

                    if ("bad".equalsIgnoreCase(output) || "0".equals(output)) {
                        showPhishingNotification(context, sender, url, confidence);
                    }
                } else {
                    Log.e(TAG, "Backend error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PredictionResponse> call, Throwable t) {
                Log.e(TAG, "Failed to verify URL: " + t.getMessage());
            }
        });
    }

    private void showPhishingNotification(Context context, String sender, String url, double confidence) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Phishing Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Alerts for malicious links in SMS");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        String contentText = String.format("Malicious link detected from %s. Confidence: %.1f%%. Do not click!", sender, confidence);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("⚠️ Phishing Attempt Detected")
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Suspicious link: " + url + "\n\n" + contentText))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.RED)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
