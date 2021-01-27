package com.amlaakoman.amlaakapp.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.List;

public class Common {
    public static String getAppBath(Context context) {

        File dir = new File(android.os.Environment.getExternalStorageDirectory()
                + File.separator
                + "report"
                + File.separator);
        if (!dir.exists()) dir.mkdir();
        return dir.getPath() + File.separator;

    }

    public static void openFilePdf(Context context, File url) {
        Uri uri = FileProvider.getUriForFile(
                context,
                context.getApplicationContext().getPackageName() + ".fileprovider", url);
        Intent intent = new Intent(Intent.ACTION_VIEW);

        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
        );

        for (ResolveInfo resolveInfo : resolveInfoList) {
            String name = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(name, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }
}
