package challenge.magnet.android.whisper.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXMessage;

public class MessageRecieverWakeUp extends BroadcastReceiver {
    @Override
    // called when the device is sleep is interrupted by gcm
    public void onReceive(Context context, Intent intent) {


    }
}
