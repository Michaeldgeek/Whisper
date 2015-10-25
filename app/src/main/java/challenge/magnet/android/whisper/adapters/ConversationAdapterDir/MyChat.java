package challenge.magnet.android.whisper.adapters.ConversationAdapterDir;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import challenge.magnet.android.whisper.databases.TablesDefn.RecentConversations;

/**
 * Created by User Pc on 10/17/2015.
 */
public class MyChat implements Parcelable {
    private String message;
    private String date;
    private String delivered;
    private String type;

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

    public String getDelivered() {
        return delivered;
    }

    public void setDelivered(String delivered) {
        this.delivered = delivered;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(message);
        dest.writeString(date);
        dest.writeString(delivered);
        dest.writeString(type);
    }


    public static MyChat fromCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.moveToFirst();
            MyChat myChat = new MyChat();
            myChat.setMessage(cursor.getString(cursor.getColumnIndex(RecentConversations.TableDefn.COLUMN_NAME_MESSAGE)));
            myChat.setDate(cursor.getString(cursor.getColumnIndex(RecentConversations.TableDefn.COLUMN_NAME_DATE)));
            myChat.setDelivered(cursor.getString(cursor.getColumnIndex(RecentConversations.TableDefn.COLUMN_NAME_DELIVERED)));
            myChat.setType(cursor.getString(cursor.getColumnIndex(RecentConversations.TableDefn.COLUMN_NAME_MESSAGE_TYPE)));
            return myChat;
        }
        else {
            return null;
        }

    }
}