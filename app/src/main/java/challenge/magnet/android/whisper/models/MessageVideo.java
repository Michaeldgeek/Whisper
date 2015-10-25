package challenge.magnet.android.whisper.models;


public class MessageVideo {

    private boolean left;
    private boolean sent;
    private String date;
    private String user ;
    private String videoUrl;
    private boolean delivered;

    public MessageVideo() {}

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public String getUser() {
        return user;
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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

}
