package challenge.magnet.android.whisper;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.api.MMXUser;


public class App extends Application {

    private static App Instance;
    public static volatile Handler applicationHandler = null;

    @Override
    public void onCreate() {
        super.onCreate();
        MMX.init(this, R.raw.whisper);
        Instance=this;
        applicationHandler = new Handler(getInstance().getMainLooper());
        NativeLoader.initNativeLibs(App.getInstance());
        MMX.registerWakeupBroadcast(new Intent("MY_WAKEUP_ACTION"));

    }

    public static App getInstance()
    {
        return Instance;
    }
}
