package oop.dayplanner3;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.TimerTask;

public class CheckConnection extends TimerTask {
    private Context context;
    String log_tag = "CheckConnection";
    public CheckConnection(Context context){
        this.context = context;
    }
    public void run() {
        if(NetworkUtils.isNetworkAvailable(context)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, R.string.sync_start_text, Toast.LENGTH_LONG).show();
                }
            });

        }else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, R.string.sync_no_network_text, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
