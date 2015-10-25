package challenge.magnet.android.whisper.models;

/**
 * Created by edwardyang on 9/10/15.
 */
public class MessageImage {

    private boolean left;
    private boolean sent;
    private String date;
    private String user ;
    private String text;
    private boolean delivered;

    public MessageImage() {

    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public boolean isSent() {
        return sent;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public boolean isLeft() {
        return left;
    }

    public String getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

    public String getUser() {
        return user;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
