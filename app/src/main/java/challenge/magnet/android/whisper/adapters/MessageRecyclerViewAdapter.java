package challenge.magnet.android.whisper.adapters;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.api.MMXUser;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;
import java.util.List;

import challenge.magnet.android.whisper.R;
import challenge.magnet.android.whisper.activities.ImageViewActivity;
import challenge.magnet.android.whisper.activities.MapViewActivity;
import challenge.magnet.android.whisper.activities.ShowFriends;
import challenge.magnet.android.whisper.activities.VideoViewActivity;
import challenge.magnet.android.whisper.models.MessageImage;
import challenge.magnet.android.whisper.models.MessageMap;
import challenge.magnet.android.whisper.models.MessageText;
import challenge.magnet.android.whisper.models.MessageVideo;

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TEXT_TYPE = 0;
    private static final int IMAGE_TYPE = 1;
    private static final int MAP_TYPE = 2;
    private static final int VIDEO_TYPE = 3;
    private List<Object> messageItems;
    public Activity mActivity;


    public MessageRecyclerViewAdapter(Activity activity, List<Object> items) {
        this.messageItems = items;
        this.mActivity = activity;
    }

    class ViewHolderText extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView tvMessageText;
        private RelativeLayout wrapper;
        private TextView date;
        private ImageView replyStatus;

        public ViewHolderText(View itemView) {
            super(itemView);
            this.wrapper = (RelativeLayout) itemView.findViewById(R.id.wrapper);
            this.tvMessageText = (TextView) itemView.findViewById(R.id.message_text);
            this.date = (TextView)itemView.findViewById(R.id.time_text);
            this.replyStatus = (ImageView)itemView.findViewById(R.id.user_reply_status);
            this.wrapper.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            if ((view instanceof RelativeLayout) && (view != null)) {
                final TextView textView = (TextView)view.findViewById(R.id.message_text);
                if(textView != null) {
                    new MaterialDialog.Builder(mActivity)
                            .items(R.array.txt_long_click)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                    if(i == 0){
                                        // Copy text
                                        ClipboardManager clipboard = (ClipboardManager)
                                                mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_PLAIN, textView.getText().toString());
                                        clipboard.setPrimaryClip(clip);
                                        Toast.makeText(mActivity, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                                    }
                                    else if(i == 2) {
                                        //forward
                                        Intent intent = new Intent(mActivity, ShowFriends.class);
                                        mActivity.startActivity(intent);
                                    }
                                }
                            }).show();
                }
            }

            return true;
        }
    }

    class ViewHolderImage extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public ImageView ivMessageImage;
        private LinearLayout wrapper;

        public ViewHolderImage(View itemView) {
            super(itemView);
            this.wrapper = (LinearLayout) itemView.findViewById(R.id.wrapper);
            this.ivMessageImage = (ImageView) itemView.findViewById(R.id.ivMessageImage);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (messageItems.size() > 0) {
                MessageImage item = (MessageImage) messageItems.get(getAdapterPosition());
                 goToImageViewActivity(item.getText());

            }
        }

        @Override
        public boolean onLongClick(View view) {
            return false;
        }
    }

    class ViewHolderMap extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout wrapper;
        public ImageView ivMessageLocation;

        public ViewHolderMap(View itemView) {
            super(itemView);
            this.wrapper = (LinearLayout) itemView.findViewById(R.id.wrapper);
            this.ivMessageLocation = (ImageView) itemView.findViewById(R.id.ivMessageLocation);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (messageItems.size() > 0) {
                MessageMap item = (MessageMap) messageItems.get(getAdapterPosition());
                goToMapViewActivity(item.getLatlng());
            }
        }
    }

    class ViewHolderVideo extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout wrapper;
        public ImageView ivVideoPlayButton;

        public ViewHolderVideo(View itemView) {
            super(itemView);
            this.wrapper = (LinearLayout) itemView.findViewById(R.id.wrapper);
            this.ivVideoPlayButton = (ImageView) itemView.findViewById(R.id.ivVideoPlayButton);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (messageItems.size() > 0) {
                MessageVideo item = (MessageVideo) messageItems.get(getAdapterPosition());
                goToVideoViewActivity(item.getVideoUrl());
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.messageItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messageItems.get(position) instanceof MessageText) {
            return TEXT_TYPE;
        } else if (messageItems.get(position) instanceof MessageImage) {
            return IMAGE_TYPE;
        } else if (messageItems.get(position) instanceof MessageMap) {
            return MAP_TYPE;
        } else if (messageItems.get(position) instanceof MessageVideo) {
            return VIDEO_TYPE;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case IMAGE_TYPE:
                view = inflater.inflate(R.layout.item_chat_image, parent, false);
                viewHolder = new ViewHolderImage(view);
                break;
            case MAP_TYPE:
                view = inflater.inflate(R.layout.item_chat_map, parent, false);
                viewHolder = new ViewHolderMap(view);
                break;
            case VIDEO_TYPE:
                view = inflater.inflate(R.layout.item_chat_video, parent, false);
                viewHolder = new ViewHolderVideo(view);
                break;
            default:
                view = inflater.inflate(R.layout.item_chat_text, parent, false);
                viewHolder = new ViewHolderText(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (position >= messageItems.size()) {
            return;
        }
        switch (viewHolder.getItemViewType()) {
            case TEXT_TYPE:
                configureViewHolder1((ViewHolderText) viewHolder, position);
                break;
            case IMAGE_TYPE:
                configureViewHolder2((ViewHolderImage) viewHolder, position);
                break;
            case MAP_TYPE:
                configureViewHolder3((ViewHolderMap) viewHolder, position);
                break;
            case VIDEO_TYPE:
                configureViewHolder4((ViewHolderVideo) viewHolder, position);
                break;
        }
    }

    private void configureViewHolder1(ViewHolderText vh, int position) {
        MessageText item = (MessageText) messageItems.get(position);
           if (item != null) {
               vh.tvMessageText.setText(item.getText());
               vh.wrapper.setGravity(item.isLeft() ? Gravity.LEFT : Gravity.RIGHT);
               vh.wrapper.findViewById(R.id.bubble).setBackgroundResource(item.isLeft() ? R.drawable.balloon_incoming_normal : R.drawable.balloon_outgoing_normal);
               vh.date.setText(item.getDate());
                if(item.isSent() ){
                    vh.replyStatus.setImageResource(R.drawable.ic_single_tick);

                }
               if(item.isDelivered()) {
                   vh.replyStatus.setImageResource(R.drawable.ic_double_tick);
                   //item.setDelivered(false);
               }
           }
    }

    private void configureViewHolder2(ViewHolderImage vh, int position) {
        MessageImage item = (MessageImage) messageItems.get(position);
        if (item != null) {
            //vh.ivMessageImage.setBackgroundResource(item.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
            vh.wrapper.setGravity(item.isLeft() ? Gravity.LEFT : Gravity.RIGHT);
            if(item.getText().contains("http://")){
               Picasso.with(mActivity).load(item.getText()).into(vh.ivMessageImage);;
            }
            else {
                Picasso.with(mActivity).load(Uri.fromFile(new File(item.getText()))).into(vh.ivMessageImage);
            }

        }
    }

    private void configureViewHolder3(ViewHolderMap vh, int position) {
        final MessageMap item = (MessageMap) messageItems.get(position);
        if (item != null) {
            //vh.ivMessageLocation.setBackgroundResource(item.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
            vh.wrapper.setGravity(item.isLeft() ? Gravity.LEFT : Gravity.RIGHT);
            String loc = "http://maps.google.com/maps/api/staticmap?center="+item.getLatlng()+"&zoom=18&size=700x300&sensor=false&markers=color:blue%7Clabel:S%7C"+item.getLatlng();
            Picasso.with(mActivity).load(loc).into(vh.ivMessageLocation);
        }
    }

    private void configureViewHolder4(final ViewHolderVideo vh, int position) {
        final MessageVideo item = (MessageVideo) messageItems.get(position);
        if (item != null) {
            //vh.ivVideoPlayButton.setBackgroundResource(item.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
            vh.wrapper.setGravity(item.isLeft() ? Gravity.LEFT : Gravity.RIGHT);
        }
    }

    public void goToMapViewActivity(String latlng) {
        Intent intent;
        intent = new Intent(mActivity, MapViewActivity.class);
        intent.putExtra("latlng", latlng);
        mActivity.startActivity(intent);
    }

    public void goToImageViewActivity(String url) {
        Intent intent;
        intent = new Intent(mActivity, ImageViewActivity.class);
        intent.putExtra("imageUrl", url);
        mActivity.startActivity(intent);
    }

    public void goToVideoViewActivity(String url) {
        Intent intent;
        intent = new Intent(mActivity, VideoViewActivity.class);
        intent.putExtra("videoUrl", url);
        Log.i("video", url);
        mActivity.startActivity(intent);
    }

    public void add(Object obj) {
        messageItems.add(obj);
    }


}