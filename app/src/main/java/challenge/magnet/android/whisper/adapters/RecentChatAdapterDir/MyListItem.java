package challenge.magnet.android.whisper.adapters.RecentChatAdapterDir;

import android.database.Cursor;

import challenge.magnet.android.whisper.databases.TablesDefn.RecentChatTableDefnDB;

public class MyListItem{
     private String userName;
     private String message;
     private String date;
    private String circleText;
    private String delivered;
    private String profilePic;
    private String invisible_for_badge_view;

    public void setUserName(String userName){
    this.userName=userName;
  }
  public String getUserName(){
    return userName;
  }
  
  public static MyListItem fromCursor(Cursor cursor) {
    if(cursor != null){
        cursor.moveToFirst();
        MyListItem listItem = new MyListItem();
        listItem.setUserName(cursor.getString(cursor.getColumnIndex(RecentChatTableDefnDB.RecentChatTableDfn.COLUMN_NAME_USER)));
        listItem.setDate(cursor.getString(cursor.getColumnIndex(RecentChatTableDefnDB.RecentChatTableDfn.COLUMN_NAME_DATE)));
        listItem.setProfilePic(cursor.getString(cursor.getColumnIndex(RecentChatTableDefnDB.RecentChatTableDfn.COLUMN_NAME_PROFILE_PIC)));
        return listItem;
    }
      else {
        throw  new NullPointerException("cursr passed is null");
    }
  }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCircleText() {
        return circleText;
    }

    public void setCircleText(String circleText) {
        this.circleText = circleText;
    }

    public String isDelivered() {
        return delivered;
    }

    public void setDelivered(String delivered) {
        this.delivered = delivered;
    }

    public String getInvisible_for_badge_view() {
        return invisible_for_badge_view;
    }

    public void setInvisible_for_badge_view(String invisible_for_badge_view) {
        this.invisible_for_badge_view = invisible_for_badge_view;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}