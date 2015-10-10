package challenge.magnet.android.whisper.models;


public class MessageImage {

    public boolean left;
    public String imageUrl;
    public boolean isInstagram;

    public MessageImage(boolean left, String imageUrl, boolean isInstagram) {
        super();
        this.left = left;
        this.imageUrl = imageUrl;
        this.isInstagram = isInstagram;
    }

}
