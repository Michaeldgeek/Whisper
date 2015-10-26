package challenge.magnet.android.whisper.adapters.RecentChatAdapterDir;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import challenge.magnet.android.whisper.R;
import de.hdodenhof.circleimageview.CircleImageView;


public class MyListCursorAdapter extends CursorRecyclerViewAdapter<MyListCursorAdapter.ViewHolder>{
    private Context context;

    public MyListCursorAdapter(Context context,Cursor cursor){
       super(context,cursor);
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public TextView message;
        public TextView date;
        public TextView circleText;
        public ImageView deliveredIcon;
        public CircleImageView profilePic;
        public View invisible_for_badge_view;

        public ViewHolder(View view) {
            super(view);
            username = (TextView) view.findViewById(R.id.username);
            message = (TextView) view.findViewById(R.id.message);
            date = (TextView) view.findViewById(R.id.date);
            circleText = (TextView) view.findViewById(R.id.circle_text);
            deliveredIcon = (ImageView)view.findViewById(R.id.delivered_icon);
            profilePic = (CircleImageView)view.findViewById(R.id.profile_thumb);
            invisible_for_badge_view = view.findViewById(R.id.invisible_for_badge_view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        MyListItem myListItem = MyListItem.fromCursor(cursor);
        viewHolder.username.setText(myListItem.getUserName());
        viewHolder.date.setText(myListItem.getDate());
        //delivered goes here. implementation skipped
        Picasso.with(context).load(myListItem.getProfilePic()).into(viewHolder.profilePic);
       // viewHolder.invisible_for_badge_view.setTag(myListItem.getInvisible_for_badge_view());
    }
}