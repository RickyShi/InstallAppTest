package test.ricky.edu.installapptest;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class UpdateService extends Service {

    DownloadManager manager;

    DownloadCompleteReceiver receiver;

    private void initDownManager() {

        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        receiver = new DownloadCompleteReceiver();

        Request down = new Request(
                Uri.parse("http://dslsrv8.cs.missouri.edu/~rs79c/Dish/a.apk"));

        down.setAllowedNetworkTypes(Request.NETWORK_MOBILE
                | Request.NETWORK_WIFI);

        down.setNotificationVisibility(Request.VISIBILITY_VISIBLE);

        down.setVisibleInDownloadsUi(true);

        down.setDestinationInExternalFilesDir(this,
                Environment.DIRECTORY_DOWNLOADS, "a.apk");
        Log.d("ricky", "environment download path: " + Environment.DIRECTORY_DOWNLOADS);
                manager.enqueue(down);

        registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        initDownManager();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {

        if (receiver != null)
            unregisterReceiver(receiver);

        super.onDestroy();
    }

    class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    Log.d("ricky","download completed!");
                long downId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                Log.d("ricky", "install path: " + manager.getUriForDownloadedFile(downId).toString());
                installAPK(manager.getUriForDownloadedFile(downId));

                UpdateService.this.stopSelf();

            }
        }

        private void installAPK(Uri apk) {

            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(apk, "application/vnd.android.package-archive");
            startActivity(intent);

        }

    }
}