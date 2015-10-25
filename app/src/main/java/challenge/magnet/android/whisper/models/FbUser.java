package challenge.magnet.android.whisper.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.sromku.simple.fb.entities.Profile;

import java.util.ArrayList;

/**
 * Created by User Pc on 10/12/2015.
 */
public class FbUser extends Profile implements Parcelable {

    public FbUser() {
    }

    private String id;
    private String email;
    private String username;
    private String fbProfilePic;
    private String FirstName;
    private String LastName;
    private String fbDisplayName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;

    }

    public String getfbProfilePic() {
        return fbProfilePic;
    }

    public void setfbProfilePic(String fbProfilePic) {
        this.fbProfilePic = fbProfilePic;
    }


    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }


    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getFbDisplayName() {
        return fbDisplayName;
    }

    public void setFbDisplayName(String fbDisplayName) {
        this.fbDisplayName = fbDisplayName;
    }

    protected FbUser(Parcel in) {
        id = in.readString();
        email = in.readString();
        username = in.readString();
        fbProfilePic = in.readString();
        FirstName = in.readString();
        LastName = in.readString();
        fbDisplayName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(email);
        dest.writeString(username);
        dest.writeString(fbProfilePic);
        dest.writeString(FirstName);
        dest.writeString(LastName);
        dest.writeString(fbDisplayName);
    }

    @SuppressWarnings("unused")
    public static final Creator<FbUser> CREATOR = new Creator<FbUser>() {
        @Override
        public FbUser createFromParcel(Parcel in) {
            return new FbUser(in);
        }

        @Override
        public FbUser[] newArray(int size) {
            return new FbUser[size];
        }
    };
}
