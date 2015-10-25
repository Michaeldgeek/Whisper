package challenge.magnet.android.whisper.models;

/**
 * Created by User Pc on 10/23/2015.
 */
public class InstagramUser {

    private String instaId;
    private String displayName;
    private String profilePic;

    public InstagramUser() {

    }

    public String getDisplayName() {
        return displayName;
    }

    public String getInstaId() {
        return instaId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setInstaId(String instaId) {
        this.instaId = instaId;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
