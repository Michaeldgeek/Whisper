package challenge.magnet.android.whisper;

import com.sromku.simple.fb.Permission;

public class Constants {

    public static final String TAG="whisper";
    public static final String CLIENT_ID = "65da106deaa54299a70f072831b0c664";
    public static final String REDIRECT_URI = "whisper://instagram-callback";
    public static final int REQUEST_CODE_INSTAGRAM_PICKER = 10;
    public static final Permission[] permissions = new Permission[] {
            Permission.USER_PHOTOS,
            Permission.USER_FRIENDS,
            Permission.USER_ABOUT_ME,

    };
    public static final String APP_NS = "whisper_ns";
    public static final String CLIENT_SECRET = "c96d84410d3b4afc92ac7a46d62682a6";
    public static final String WHISPER_URL_REGISTER = "http://whisper.xp3.biz/registeruser.php";
    public static final String WHISPER_URL_UPDATE_FB_ID = "http://whisper.xp3.biz/fbid.php";
    public static final String WHISPER_URL_UPDATE_INSTAGRAM_ID = "http://whisper.xp3.biz/instagramid.php";
    public static final String WHISPER_URL_SAVE_PIX = "http://whisper.xp3.biz/savepix.php";
    public static final String WHISPER_URL_SYNC_DATA = "http://whisper.xp3.biz/syncdata.php";
    public static final String RECENT_CHAT_PROVIDER_AUTORITY = "content://challenge.magnet.android.whisper.provider";
}
