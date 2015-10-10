package challenge.magnet.android.whisper;

import com.sromku.simple.fb.Permission;

public class Constants {

    public static final String TAG="whisper";
    static final String CLIENT_ID = "65da106deaa54299a70f072831b0c664";
    static final String REDIRECT_URI = "whisper://instagram-callback";
    static final int REQUEST_CODE_INSTAGRAM_PICKER = 10;
    static final Permission[] permissions = new Permission[] {
            Permission.USER_PHOTOS,
            Permission.EMAIL,
            Permission.USER_FRIENDS,
            Permission.USER_ABOUT_ME,
            Permission.USER_VIDEOS

    };
    static final String APP_NS = "whisper_ns";
}
