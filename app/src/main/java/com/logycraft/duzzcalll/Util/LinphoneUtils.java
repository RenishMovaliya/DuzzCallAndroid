package com.logycraft.duzzcalll.Util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
//
//import com.duzzelcall.linphone.core.Address;
//import com.duzzelcall.linphone.core.Call;
//import com.duzzelcall.linphone.core.CallLog;
//import com.duzzelcall.linphone.core.ChatMessage;
//import com.duzzelcall.linphone.core.Content;
//import com.duzzelcall.linphone.core.Core;
//import com.duzzelcall.linphone.core.EventLog;
//import com.duzzelcall.linphone.core.Factory;
//import com.duzzelcall.linphone.core.LogCollectionState;
//import com.duzzelcall.linphone.core.ProxyConfig;
//import com.duzzelcall.linphone.core.tools.Log;
//import com.duzzelcall.managinig.LinphoneContext;
//import com.duzzelcall.managinig.LinphoneManager;
//import com.duzzelcall.managinig.R;
//import com.duzzelcall.managinig.settings.LinphonePreferences;

import com.duzzcall.duzzcall.R;
import com.logycraft.duzzcalll.LinphoneContext;
import com.logycraft.duzzcalll.LinphoneManager;
import com.logycraft.duzzcalll.LinphonePreferences;

import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.ChatMessage;
import org.linphone.core.Content;
import org.linphone.core.Core;
import org.linphone.core.EventLog;
import org.linphone.core.Factory;
import org.linphone.core.LogCollectionState;
import org.linphone.core.ProxyConfig;
import org.linphone.core.tools.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

/* loaded from: classes2.dex */
public final class LinphoneUtils {
    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    private LinphoneUtils() {
    }

    public static void configureLoggingService(boolean isDebugEnabled, String appName) {
        if (!LinphonePreferences.instance().useJavaLogger()) {
            Factory.instance().enableLogCollection(LogCollectionState.Enabled);
            Factory.instance().setDebugMode(isDebugEnabled, appName);
            return;
        }
        Factory.instance().setDebugMode(isDebugEnabled, appName);
        Factory.instance().enableLogCollection(LogCollectionState.EnabledWithoutPreviousLogHandler);
        if (isDebugEnabled) {
            if (LinphoneContext.isReady()) {
                Factory.instance().getLoggingService().addListener(LinphoneContext.instance().getJavaLoggingService());
            }
        } else if (LinphoneContext.isReady()) {
            Factory.instance().getLoggingService().removeListener(LinphoneContext.instance().getJavaLoggingService());
        }
    }

    public static void dispatchOnUIThread(Runnable r) {
        sHandler.post(r);
    }

    public static void dispatchOnUIThreadAfter(Runnable r, long after) {
        sHandler.postDelayed(r, after);
    }

    public static void removeFromUIThreadDispatcher(Runnable r) {
        sHandler.removeCallbacks(r);
    }

    private static boolean isSipAddress(String numberOrAddress) {
        Factory.instance().createAddress(numberOrAddress);
        return true;
    }

    public static boolean isNumberAddress(String numberOrAddress) {
        ProxyConfig proxy = LinphoneManager.getCore().createProxyConfig();
        return proxy.normalizePhoneNumber(numberOrAddress) != null;
    }

    public static boolean isStrictSipAddress(String numberOrAddress) {
        return isSipAddress(numberOrAddress) && numberOrAddress.startsWith("sip:");
    }

    public static String getDisplayableAddress(Address addr) {
        return "sip:" + addr.getUsername() + "@" + addr.getDomain();
    }

    public static String getAddressDisplayName(String uri) {
        Address lAddress = Factory.instance().createAddress(uri);
        return getAddressDisplayName(lAddress);
    }

    public static String getAddressDisplayName(Address address) {
        if (address == null) {
            return null;
        }
        String displayName = address.getDisplayName();
        if (displayName == null || displayName.isEmpty()) {
            displayName = address.getUsername();
        }
        if (displayName == null || displayName.isEmpty()) {
            return address.asStringUriOnly();
        }
        return displayName;
    }

    public static String timestampToHumanDate(Context context, long timestamp, int format) {
        return timestampToHumanDate(context, timestamp, context.getString(format));
    }

    public static String timestampToHumanDate(Context context, long timestamp, String format) {
        SimpleDateFormat dateFormat;
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(1000 * timestamp);
            if (isToday(cal)) {
                dateFormat = new SimpleDateFormat(context.getResources().getString(R.string.today_date_format), Locale.getDefault());
            } else {
                dateFormat = new SimpleDateFormat(format, Locale.getDefault());
            }
            return dateFormat.format(cal.getTime());
        } catch (NumberFormatException e) {
            return String.valueOf(timestamp);
        }
    }

    private static boolean isToday(Calendar cal) {
        return isSameDay(cal, Calendar.getInstance());
    }

    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1 != null && cal2 != null && cal1.get(0) == cal2.get(0) && cal1.get(1) == cal2.get(1) && cal1.get(6) == cal2.get(6);
    }

    private static boolean isCallRunning(Call call) {
        if (call == null) {
            return false;
        }
        Call.State state = call.getState();
        return state == Call.State.Connected || state == Call.State.Updating || state == Call.State.UpdatedByRemote || state == Call.State.StreamsRunning || state == Call.State.Resuming;
    }

    public static boolean isCallEstablished(Call call) {
        if (call == null) {
            return false;
        }
        Call.State state = call.getState();
        return isCallRunning(call) || state == Call.State.Paused || state == Call.State.PausedByRemote || state == Call.State.Pausing;
    }

    public static boolean isHighBandwidthConnection(Context context) {
        @SuppressLint("WrongConstant") ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected() && isConnectionFast(info.getType(), info.getSubtype());
    }

    private static boolean isConnectionFast(int type, int subType) {
        if (type != 0 || (subType != 1 && subType != 2 && subType != 11)) {
            return true;
        }
        return false;
    }

    public static void reloadVideoDevices() {
        Core core = LinphoneManager.getCore();
        if (core == null) {
            return;
        }
        Log.i("[Utils] Reloading camera devices");
        core.reloadVideoDevices();
        LinphoneManager.getInstance().resetCameraFromPreferences();
    }

    public static String getDisplayableUsernameFromAddress(String sipAddress) {
        String username = sipAddress;
        Core core = LinphoneManager.getCore();
        if (core == null) {
            return username;
        }
        if (username.startsWith("sip:")) {
            username = username.substring(4);
        }
        if (username.contains("@")) {
            String[] split = username.split("@");
            if (split.length > 1) {
                String domain = split[1];
                ProxyConfig lpc = core.getDefaultProxyConfig();
                if (lpc != null) {
                    if (domain.equals(lpc.getDomain())) {
                        return split[0];
                    }
                } else if (domain.equals(LinphoneContext.instance().getApplicationContext().getString(R.string.default_domain))) {
                    return split[0];
                }
            }
            return split[0];
        }
        return username;
    }

    public static String getFullAddressFromUsername(String username) {
        String sipAddress = username;
        Core core = LinphoneManager.getCore();
        if (core == null || username == null) {
            return sipAddress;
        }
        if (!sipAddress.startsWith("sip:")) {
            sipAddress = "sip:" + sipAddress;
        }
        if (!sipAddress.contains("@")) {
            ProxyConfig lpc = core.getDefaultProxyConfig();
            if (lpc != null) {
                return sipAddress + "@" + lpc.getDomain();
            }
            return sipAddress + "@" + LinphoneContext.instance().getApplicationContext().getString(R.string.default_domain);
        }
        return sipAddress;
    }

    public static void displayErrorAlert(String msg, Context ctxt) {
        if (ctxt != null && msg != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctxt);
            builder.setMessage(msg).setCancelable(false).setNeutralButton("OK", (DialogInterface.OnClickListener) null).show();
        }
    }

    public static Spanned getTextWithHttpLinks(String text) {
        int indexFinHttp;
        int indexFinHttp2;
        if (text == null) {
            return null;
        }
        if (text.contains("<")) {
            text = text.replace("<", "&lt;");
        }
        if (text.contains(">")) {
            text = text.replace(">", "&gt;");
        }
        if (text.contains("\n")) {
            text = text.replace("\n", "<br>");
        }
        if (text.contains("http://")) {
            int indexHttp = text.indexOf("http://");
            if (text.indexOf(" ", indexHttp) == -1) {
                indexFinHttp2 = text.length();
            } else {
                indexFinHttp2 = text.indexOf(" ", indexHttp);
            }
            String link = text.substring(indexHttp, indexFinHttp2);
            String linkWithoutScheme = link.replace("http://", "");
            String quote = Pattern.quote(link);
            text = text.replaceFirst(quote, "<a href=\"" + link + "\">" + linkWithoutScheme + "</a>");
        }
        if (text.contains("https://")) {
            int indexHttp2 = text.indexOf("https://");
            if (text.indexOf(" ", indexHttp2) == -1) {
                indexFinHttp = text.length();
            } else {
                indexFinHttp = text.indexOf(" ", indexHttp2);
            }
            String link2 = text.substring(indexHttp2, indexFinHttp);
            String linkWithoutScheme2 = link2.replace("https://", "");
            String quote2 = Pattern.quote(link2);
            text = text.replaceFirst(quote2, "<a href=\"" + link2 + "\">" + linkWithoutScheme2 + "</a>");
        }
        return Html.fromHtml(text);
    }

    public static void showTrustDeniedDialog(Context context) {
//        final Dialog dialog = new Dialog(context);
//        dialog.requestWindowFeature(1);
//        Drawable d = new ColorDrawable(ContextCompat.getColor(context, R.color.dark_grey_color));
//        d.setAlpha(200);
//        dialog.setContentView(R.layout.dialog);
//        dialog.getWindow().setLayout(-1, -1);
//        dialog.getWindow().setBackgroundDrawable(d);
//        TextView title = (TextView) dialog.findViewById(R.id.dialog_title);
//        title.setVisibility(View.GONE);
//        TextView message = (TextView) dialog.findViewById(R.id.dialog_message);
//        message.setVisibility(View.VISIBLE);
//        message.setText(context.getString(R.string.trust_denied));
//        ImageView icon = (ImageView) dialog.findViewById(R.id.dialog_icon);
//        icon.setVisibility(View.VISIBLE);
//        icon.setImageResource(R.drawable.security_alert_indicator);
//        Button delete = (Button) dialog.findViewById(R.id.dialog_delete_button);
//        delete.setVisibility(View.GONE);
//        Button cancel = (Button) dialog.findViewById(R.id.dialog_cancel_button);
//        cancel.setVisibility(View.VISIBLE);
//        Button call = (Button) dialog.findViewById(R.id.dialog_ok_button);
//        call.setVisibility(View.VISIBLE);
//        call.setText(R.string.call);
//        cancel.setOnClickListener(new View.OnClickListener() { // from class: com.duzzelcall.managinig.utils.LinphoneUtils.1
//            @Override // android.view.View.OnClickListener
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        call.setOnClickListener(new View.OnClickListener() { // from class: com.duzzelcall.managinig.utils.LinphoneUtils.2
//            @Override // android.view.View.OnClickListener
//            public void onClick(View view) {
//                Address addressToCall;
//                CallLog[] logs = LinphoneManager.getCore().getCallLogs();
//                CallLog lastLog = logs[0];
//                if (lastLog.getDir() == Call.Dir.Incoming) {
//                    addressToCall = lastLog.getFromAddress();
//                } else {
//                    addressToCall = lastLog.getToAddress();
//                }
//                LinphoneManager.getCallManager().newOutgoingCall(addressToCall.asString(), null);
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
    }

    public static Dialog getDialog(Context context, String text) {
        Dialog dialog = new Dialog(context);
//        dialog.requestWindowFeature(1);
//        Drawable d = new ColorDrawable(ContextCompat.getColor(context, R.color.dark_grey_color));
//        d.setAlpha(200);
//        dialog.setContentView(R.layout.dialog);
//        dialog.getWindow().setLayout(-1, -1);
//        dialog.getWindow().setBackgroundDrawable(d);
//        TextView customText = (TextView) dialog.findViewById(R.id.dialog_message);
//        customText.setText(text);
        return dialog;
    }

    public static void deleteFileContentIfExists(EventLog eventLog) {
        ChatMessage message;
        Content[] contents;
        if (eventLog.getType() == EventLog.Type.ConferenceChatMessage && (message = eventLog.getChatMessage()) != null) {
            for (Content content : message.getContents()) {
                if (content.isFile() && content.getFilePath() != null) {
                    Log.w("[Linphone Utils] Chat message is being deleted, file ", content.getFilePath(), " will also be deleted");
//                    FileUtils.deleteFile(content.getFilePath());
                }
            }
        }
    }
}
