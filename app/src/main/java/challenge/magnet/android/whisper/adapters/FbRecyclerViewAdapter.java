package challenge.magnet.android.whisper.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.squareup.picasso.Picasso;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnProfileListener;

import java.util.List;

import challenge.magnet.android.whisper.ChatActivity;
import challenge.magnet.android.whisper.R;
import challenge.magnet.android.whisper.models.FbUser;
import info.hoang8f.widget.FButton;

/**
 * Created by User Pc on 10/12/2015.
 */
public class FbRecyclerViewAdapter extends RecyclerView.Adapter<FbRecyclerViewAdapter.ViewHolder> {
     List<FbUser> users;
     Activity mActivity;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CardView cardView;
        public TextView tvUsername;
        public ImageView fbProfilePic;
        public CircularProgressButton addFButton;
        public TextView fbIdHolder;

        public ViewHolder(View itemView) {
            super(itemView);
            this.cardView = (CardView)itemView.findViewById(R.id.wrapper_card);
            this.tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            this.fbProfilePic = (ImageView)itemView.findViewById(R.id.profile_pic);
            this.addFButton = (CircularProgressButton) itemView.findViewById(R.id.add_me);
            this.fbIdHolder = (TextView)itemView.findViewById(R.id.fb_id_holder);
            this.addFButton.setIndeterminateProgressMode(true);
            this.cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // add the user
            if(view != null && view instanceof CardView) {
                Intent intent = new Intent(mActivity, ChatActivity.class);
                intent.putExtra("username",this.fbIdHolder.getText().toString());
                intent.putExtra("picture", (String) this.fbProfilePic.getTag());
                mActivity.startActivity(intent);
            }
        }

    }

    public FbRecyclerViewAdapter(Activity activity, List<FbUser> users) {
        this.users = users;
        this.mActivity = activity;
    }

    @Override
    public FbRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_user, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FbRecyclerViewAdapter.ViewHolder holder, int position) {
        if (position >= users.size()) {
            return;
        }
        else {
            FbUser profile = users.get(position);
            holder.tvUsername.setText(profile.getFbDisplayName());
            holder.fbIdHolder.setText(profile.getId());
            holder.fbProfilePic.setTag(profile.getfbProfilePic());
            Picasso.with(mActivity).load(profile.getfbProfilePic()).into(holder.fbProfilePic);
        }
    }


    @Override
    public int getItemCount() {
        if (users != null) {
            return users.size();
        } else {
            return 0;
        }
    }

    public void clear() {
        users.clear();
    }

    public void addAll(List<FbUser> profileList) {
        for (int i = 0; i < profileList.size(); i++) {
            FbUser fbUser = new FbUser();
            fbUser.setFbDisplayName(profileList.get(i).getFbDisplayName());
            fbUser.setfbProfilePic(profileList.get(i).getfbProfilePic());
            fbUser.setId(profileList.get(i).getId()); // actually returns the username.
            users.add(fbUser);
        }
    }
}
