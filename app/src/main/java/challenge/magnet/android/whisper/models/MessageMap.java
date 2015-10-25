package challenge.magnet.android.whisper.models;


public class MessageMap {

    private boolean left;
    private boolean sent;
    private String date;
    private String user ;
    private String latlng;
    private boolean delivered;

    public MessageMap() {}

    public void setUser(String user) {
        this.user = user;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isSent() {
        return sent;
    }

    public String getDate() {
        return date;
    }

    public String getLatlng() {
        return latlng;
    }

    public String getUser() {
        return user;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
