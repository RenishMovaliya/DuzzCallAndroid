package com.logycraft.duzzcalll.Util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

//import com.duzzelcall.linphone.core.Address;
//import com.duzzelcall.linphone.core.Friend;
//import com.duzzelcall.linphone.core.FriendList;
//import com.duzzelcall.linphone.core.tools.Log;
//import com.duzzelcall.managinig.LinphoneManager;

import com.logycraft.duzzcalll.LinphoneManager;

import org.linphone.core.Address;
import org.linphone.core.Friend;
import org.linphone.core.FriendList;
import org.linphone.core.tools.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* loaded from: classes2.dex */
public class FileUtils {
    public static String getNameFromFilePath(String filePath) {
        if (filePath == null) {
            return null;
        }
        int i = filePath.lastIndexOf(47);
        if (i <= 0) {
            return filePath;
        }
        String name = filePath.substring(i + 1);
        return name;
    }

    public static String getExtensionFromFileName(String fileName) {
        int i;
        if (fileName == null || (i = fileName.lastIndexOf(46)) <= 0) {
            return null;
        }
        String extension = fileName.substring(i + 1);
        return extension;
    }

    public static Boolean isExtensionImage(String path) {
        String extension = getExtensionFromFileName(path);
        if (extension != null) {
            extension = extension.toLowerCase();
        }
        return Boolean.valueOf(extension != null && extension.matches("(png|jpg|jpeg|bmp|gif)"));
    }

    public static String getFilePath(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        String result = null;
        String name = getNameFromUri(uri, context);
        try {
            File localFile = createFile(context, name);
            InputStream remoteFile = context.getContentResolver().openInputStream(uri);
            Log.i("[File Utils] Trying to copy file from " + uri.toString() + " to local file " + localFile.getAbsolutePath());
            if (copyToFile(remoteFile, localFile)) {
                Log.i("[File Utils] Copy successful");
                result = localFile.getAbsolutePath();
            } else {
                Log.e("[File Utils] Copy failed");
            }
            remoteFile.close();
        } catch (IOException e) {
            Log.e("[File Utils] getFilePath exception: ", e);
        }
        return result;
    }

    private static String getNameFromUri(Uri uri, Context context) {
        if (uri == null) {
            return null;
        }
        if (!uri.getScheme().equals("content")) {
            if (!uri.getScheme().equals("file")) {
                return null;
            }
            String name = uri.getLastPathSegment();
            return name;
        }
        Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);
        if (returnCursor == null) {
            return null;
        }
        returnCursor.moveToFirst();
        int nameIndex = returnCursor.getColumnIndex("_display_name");
        String name2 = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name2;
    }

    private static boolean copyToFile(InputStream inputStream, File destFile) {
        if (inputStream == null || destFile == null) {
            return false;
        }
        try {
            OutputStream out = new FileOutputStream(destFile);
            byte[] buffer = new byte[4096];
            while (true) {
                int bytesRead = inputStream.read(buffer);
                if (bytesRead >= 0) {
                    out.write(buffer, 0, bytesRead);
                } else {
                    out.close();
                    return true;
                }
            }
        } catch (IOException e) {
            Log.e("[File Utils] copyToFile exception: " + e);
            return false;
        }
    }

    private static File createFile(Context context, String fileName) {
        if (fileName == null) {
            return null;
        }
        if (TextUtils.isEmpty(fileName)) {
            fileName = getStartDate();
        }
        if (!fileName.contains(".")) {
            fileName = fileName + ".unknown";
        }
        File root = context.getExternalCacheDir();
        if (root != null && !root.exists()) {
            boolean result = root.mkdirs();
            if (!result) {
                Log.e("[File Utils] Couldn't create directory " + root.getAbsolutePath());
            }
        }
        return new File(root, fileName);
    }

    public static Uri getCVSPathFromLookupUri(String content) {
        Friend[] friends;
        if (content == null) {
            return null;
        }
        String contactId = getNameFromFilePath(content);
        FriendList[] friendList = LinphoneManager.getCore().getFriendsLists();
        for (FriendList list : friendList) {
            for (Friend friend : list.getFriends()) {
                if (friend.getRefKey().equals(contactId)) {
                    String contactVcard = friend.getVcard().asVcard4String();
                    return createCvsFromString(contactVcard);
                }
            }
        }
        return null;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = {"_data"};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow("_data");
            String result = cursor.getString(column_index);
            cursor.close();
            return result;
        }
        return null;
    }

    public static void deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return;
        }
        File file = new File(filePath);
        if (file.exists()) {
            try {
                if (file.delete()) {
                    Log.i("[File Utils] File deleted: ", filePath);
                } else {
                    Log.e("[File Utils] Can't delete ", filePath);
                }
                return;
            } catch (Exception e) {
                Log.e("[File Utils] Can't delete ", filePath, ", exception: ", e);
                return;
            }
        }
        Log.e("[File Utils] File ", filePath, " doesn't exists");
    }

    public static String getStorageDirectory(Context mContext) {
        File path = null;
        if (Environment.getExternalStorageState().equals("mounted")) {
            Log.w("[File Utils] External storage is mounted");
            String directory = Environment.DIRECTORY_DOWNLOADS;
            path = mContext.getExternalFilesDir(directory);
        }
        if (path == null) {
            Log.w("[File Utils] Couldn't get external storage path, using internal");
            path = mContext.getFilesDir();
        }
        return path.getAbsolutePath();
    }

    public static String getRecordingsDirectory(Context mContext) {
        return getStorageDirectory(mContext);
    }

    public static String getCallRecordingFilename(Context context, Address address) {
        String fileName = getRecordingsDirectory(context) + "/";
        String name = address.getDisplayName() == null ? address.getUsername() : address.getDisplayName();
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
        return (fileName + name + "_") + format.format(new Date()) + ".mkv";
    }

    private static Uri createCvsFromString(String vcardString) {
        String contactName = getContactNameFromVcard(vcardString);
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File vcfFile = new File(externalStorageDirectory, contactName + ".cvs");
        try {
            FileWriter fw = new FileWriter(vcfFile);
            fw.write(vcardString);
            fw.close();
            return Uri.fromFile(vcfFile);
        } catch (IOException e) {
            Log.e("[File Utils] createCVSFromString exception: " + e);
            return null;
        }
    }

    private static String getContactNameFromVcard(String vcard) {
        if (vcard != null) {
            String contactName = vcard.substring(vcard.indexOf("FN:") + 3);
            return contactName.substring(0, contactName.indexOf("\n") - 1).replace(";", "").replace(" ", "");
        }
        return null;
    }

    private static String getStartDate() {
        try {
            return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(new Date());
        } catch (RuntimeException e) {
            return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        }
    }

    public static String getMimeFromFile(String path) {
        if (isExtensionImage(path).booleanValue()) {
            return "image/" + getExtensionFromFileName(path);
        }
        return "file/" + getExtensionFromFileName(path);
    }
}
