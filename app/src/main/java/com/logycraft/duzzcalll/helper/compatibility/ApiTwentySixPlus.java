package com.logycraft.duzzcalll.helper.compatibility;

import static android.media.CamcorderProfile.get;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.duzzcall.duzzcall.R;
import com.logycraft.duzzcalll.Util.Preference;
import com.logycraft.duzzcalll.fragment.HistoryFragment;
import com.logycraft.duzzcalll.helper.notifications.Notifiable;
import com.logycraft.duzzcalll.helper.notifications.NotifiableMessage;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ApiTwentySixPlus {
    ApiTwentySixPlus() {
    }

    public static String getDeviceName(Context context) {
        BluetoothAdapter adapter;
        String name = Settings.Global.getString(context.getContentResolver(), "device_name");
        if (name == null && (adapter = BluetoothAdapter.getDefaultAdapter()) != null) {
//            name = adapter.getName();
        }
        if (name == null) {
            name = Settings.Secure.getString(context.getContentResolver(), "bluetooth_name");
        }
        if (name == null) {
            return Build.MANUFACTURER + " " + Build.MODEL;
        }
        return name;
    }

    public static Notification createRepliedNotification(Context context, String reply) {
        return new Notification.Builder(context, context.getString(R.string.notification_channel_id)).setSmallIcon(R.drawable.app_logo).setContentText(context.getString(R.string.notification_replied_label).replace("%s", reply)).build();
    }


    public static void createServiceChannel(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String id = context.getString(R.string.notification_service_channel_id);
        CharSequence name = context.getString(R.string.content_title_notification_service);
        String description = context.getString(R.string.content_title_notification_service);
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(description);
        channel.enableVibration(false);
        channel.enableLights(false);
        channel.setShowBadge(false);
        notificationManager.createNotificationChannel(channel);
    }

    public static void createMessageChannel(Context context) {
        @SuppressLint("WrongConstant") NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        String id = context.getString(R.string.notification_channel_id);
        String name = context.getString(R.string.content_title_notification);
        String description = context.getString(R.string.content_title_notification);
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(description);
        channel.setLightColor(context.getColor(R.color.notification_led_color));
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setShowBadge(true);
        notificationManager.createNotificationChannel(channel);
    }

    public static Notification createMessageNotification(Context context, Notifiable notif, Bitmap contactIcon, PendingIntent intent) {
        Notification.MessagingStyle style = new Notification.MessagingStyle(notif.getMyself());
        for (NotifiableMessage message : notif.getMessages()) {
            Notification.MessagingStyle.Message msg = new Notification.MessagingStyle.Message(message.getMessage(), message.getTime(), message.getSender());
            if (message.getFilePath() != null) {
                msg.setData(message.getFileMime(), message.getFilePath());
            }
            style.addMessage(msg);
        }
        if (notif.isGroup()) {
            style.setConversationTitle(notif.getGroupTitle());
        }
        return new Notification.Builder(context, context.getString(R.string.notification_channel_id)).setSmallIcon(R.drawable.app_logo).setAutoCancel(true).setContentIntent(intent).setDefaults(7).setLargeIcon(contactIcon).setCategory(NotificationCompat.CATEGORY_MESSAGE).setGroup(Compatibility.CHAT_NOTIFICATIONS_GROUP).setVisibility(Notification.VISIBILITY_PRIVATE).setPriority(Notification.PRIORITY_HIGH).setNumber(notif.getMessages().size()).setWhen(System.currentTimeMillis()).setShowWhen(true).setColor(context.getColor(R.color.notification_led_color)).setStyle(style).addAction(Compatibility.getReplyMessageAction(context, notif)).addAction(Compatibility.getMarkMessageAsReadAction(context, notif)).build();
    }

    public static Notification createInCallNotification(Context context, int callId, String msg, int iconID, String contactIcon, String contactName, PendingIntent intent) {

        return new NotificationCompat.Builder(context, context.getString(R.string.notification_service_channel_id)).setContentTitle(contactName).setContentText(msg)
                .setSmallIcon(iconID).setAutoCancel(false).setContentIntent(intent).setLargeIcon(StringToBitMap(contactIcon)).setCategory(NotificationCompat.CATEGORY_CALL)
                .setVisibility(Notification.VISIBILITY_PUBLIC).setPriority(Notification.PRIORITY_LOW).setWhen(System.currentTimeMillis()).setShowWhen(true).setOngoing(true).setColor(context.getColor(R.color.notification_led_color)).addAction(Compatibility.getCallDeclineAction(context, callId)).build();
    }

    public static Notification createIncomingCallNotification(Context context, int callId, String contactIcon, String contactName, String sipUri, PendingIntent intent) {
        RemoteViews notificationLayoutHeadsUp = new RemoteViews(context.getPackageName(), (int) R.layout.call_incoming_notification_heads_up);
        notificationLayoutHeadsUp.setTextViewText(R.id.caller, contactName);
        notificationLayoutHeadsUp.setTextViewText(R.id.sip_uri, sipUri);
        notificationLayoutHeadsUp.setTextViewText(R.id.incoming_call_info, context.getString(R.string.incall_notif_incoming));
        String[] split = contactName.toString().split(" ");
        String firstLetter = "";
        String second = "";
        String firstword = "D";
        String secondword = "C";
        Log.e("iconnrrrorr", "" + contactIcon);

        if (split != null && split.length >= 2) {
            android.util.Log.e("nameeeeeeeet", "namee");
            if (split[0] != null && !split[0].isEmpty()) {
                firstword = split[0];
                firstLetter = String.valueOf(firstword.charAt(0));
            }

            if (split[1] != null && !split[1].isEmpty()) {
                secondword = split[1];
                second = String.valueOf(secondword.charAt(0));
            }
        } else {
            android.util.Log.e("nameeeeeeeet", "errorr");
            firstword = split[0];
            firstLetter = String.valueOf(firstword.charAt(0));
            second = "C";
        }

        String textss = "" + firstLetter + second;
        int color = Color.parseColor("#2F80ED");

        if (contactIcon == null || contactIcon.equalsIgnoreCase("") || contactIcon.isEmpty()) {
            Bitmap bitmap = textToBitmap(textss, color);
            Bitmap bitmap2 = getRoundedCornerBitmap2(bitmap, 500);
            if (bitmap2 != null) {

                notificationLayoutHeadsUp.setImageViewBitmap(R.id.caller_picture, bitmap2);
            }
        } else {
            notificationLayoutHeadsUp.setImageViewBitmap(R.id.caller_picture, StringToBitMap(contactIcon));
        }


//        Intent answerIntent = new Intent(context, CallActionService.class);
//        answerIntent.setAction(CallActionService.ACTION_ANSWER);
//        answerIntent.putExtra("call_id", callId);
//        PendingIntent answerPendingIntent = PendingIntent.getService(context, 0, answerIntent, 0);
//
//        Intent hangupIntent = new Intent(context, CallActionService.class);
//        hangupIntent.setAction(CallActionService.ACTION_HANGUP);
//        hangupIntent.putExtra("call_id", callId);
//        PendingIntent hangupPendingIntent = PendingIntent.getService(context, 0, hangupIntent, 0);
//
//        notificationLayoutHeadsUp.setOnClickPendingIntent(R.id.image_view_accept, answerPendingIntent);
//        notificationLayoutHeadsUp.setOnClickPendingIntent(R.id.image_view_end, hangupPendingIntent);
        return new NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id)).setStyle(new NotificationCompat.DecoratedCustomViewStyle()).setSmallIcon(R.drawable.app_logo).setContentTitle(contactName).setContentText(context.getString(R.string.incall_notif_incoming)).setContentIntent(intent).setCategory(NotificationCompat.CATEGORY_CALL).setVisibility(Notification.VISIBILITY_PUBLIC).setPriority(Notification.PRIORITY_HIGH)
                .setWhen(System.currentTimeMillis()).setAutoCancel(false).setShowWhen(true).setOngoing(true)
                .setColor(context.getColor(R.color.notification_led_color)).setFullScreenIntent(intent, true)
                .addAction(Compatibility.getCallDeclineAction(context, callId)).addAction(Compatibility.getCallAnswerAction(context, callId))
                .setCustomHeadsUpContentView(notificationLayoutHeadsUp).setPriority(NotificationCompat.PRIORITY_HIGH).build();
    }

    public static Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static Bitmap getRoundedCornerBitmap2(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    private static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float radius) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final RectF rectF = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE); // Set the background color of the rounded corner
        canvas.drawRoundRect(rectF, radius, radius, paint);

        paint.setXfermode(null);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return output;
    }

    public static Bitmap textToBitmap(String text, int backgroundColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(600f);
        paint.setColor(Color.WHITE); // Text color
        paint.setTextAlign(Paint.Align.LEFT);

        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);

        int width = textBounds.width() + 600; // Add some padding
        int height = textBounds.height() + 600;

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);

        // Draw background color
        Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
        canvas.drawRect(0, 0, width, height, backgroundPaint);

        // Calculate the position to center the text
        float x = (width - textBounds.width()) / 2f - textBounds.left;
        float y = (height - textBounds.height()) / 2f - textBounds.top;

        // Draw text on the canvas
        canvas.drawText(text, x, y, paint);

        return image;
    }

    public static Notification createNotification(Context context, String title, String message, int icon, int level, Bitmap largeIcon, PendingIntent intent, int priority, boolean ongoing) {
        if (largeIcon != null) {
            return new Notification.Builder(context, context.getString(R.string.notification_service_channel_id)).setContentTitle(title).setContentText(message).setSmallIcon(icon, level).setLargeIcon(largeIcon).setContentIntent(intent).setCategory(NotificationCompat.CATEGORY_SERVICE).setVisibility(Notification.VISIBILITY_SECRET).setPriority(priority).setWhen(System.currentTimeMillis()).setShowWhen(true).setOngoing(ongoing).setColor(context.getColor(R.color.notification_led_color)).build();
        }
        return new Notification.Builder(context, context.getString(R.string.notification_service_channel_id)).setContentTitle(title).setContentText(message).setSmallIcon(icon, level).setContentIntent(intent).setCategory(NotificationCompat.CATEGORY_SERVICE).setVisibility(Notification.VISIBILITY_SECRET).setPriority(priority).setWhen(System.currentTimeMillis()).setShowWhen(true).setColor(context.getColor(R.color.notification_led_color)).build();
    }

    public static Notification createMissedCallNotification(Context context, String title, String text, PendingIntent intent, int count) {

        return new Notification.Builder(context, context.getString(R.string.notification_channel_id)).setContentTitle(title).setContentText(text).setSmallIcon(R.drawable.call_status_missed).setAutoCancel(true).setContentIntent(intent).setDefaults(3).setCategory(NotificationCompat.CATEGORY_EVENT).setVisibility(Notification.VISIBILITY_PRIVATE).setPriority(Notification.PRIORITY_HIGH).setWhen(System.currentTimeMillis()).setShowWhen(true).setNumber(count).setColor(context.getColor(R.color.notification_led_color)).build();
    }

    public static Notification createSimpleNotification(Context context, String title, String text, PendingIntent intent) {
        return new Notification.Builder(context, context.getString(R.string.notification_channel_id)).setContentTitle(title).setContentText(text).setSmallIcon(R.drawable.app_logo).setAutoCancel(true).setContentIntent(intent).setDefaults(3).setCategory(NotificationCompat.CATEGORY_MESSAGE).setVisibility(Notification.VISIBILITY_PRIVATE).setPriority(Notification.PRIORITY_HIGH).setWhen(System.currentTimeMillis()).setShowWhen(true).setColorized(true).setColor(context.getColor(R.color.notification_led_color)).build();
    }

    public static void startService(Context context, Intent intent) {
        context.startForegroundService(intent);
    }

    public static void setFragmentTransactionReorderingAllowed(FragmentTransaction transaction, boolean allowed) {
        transaction.setReorderingAllowed(allowed);
    }

    @SuppressLint("NewApi")
    public static void enterPipMode(Activity activity) {
        boolean supportsPip = activity.getPackageManager().hasSystemFeature("android.software.picture_in_picture");
//        Log.i("[Call] Is picture in picture supported: " + supportsPip);
        if (supportsPip) {
            activity.enterPictureInPictureMode();
        }
    }

    public static void vibrate(Vibrator vibrator) {
        long[] timings = {0, 1000, 1000};
        int[] amplitudes = {0, -1, 0};
        VibrationEffect effect = VibrationEffect.createWaveform(timings, amplitudes, 1);
        AudioAttributes audioAttrs = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build();
        vibrator.vibrate(effect, audioAttrs);
    }
}
