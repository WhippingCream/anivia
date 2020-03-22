package co.locodo.anivia;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MainService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String fuck = intent.getStringExtra("toast");
        if(fuck!=null) Toast.makeText(this, fuck, Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
