package challenge.magnet.android.whisper.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.magnet.mmx.client.api.MMXUser;
import java.util.List;

import challenge.magnet.android.whisper.ChatActivity;
import challenge.magnet.android.whisper.R;
import challenge.magnet.android.whisper.models.User;


public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<UsersRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = UsersRecyclerViewAdapter.class.getSimpleName();
    private List<User> users;
    public Activity mActivity;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvUsername;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (users.size() > 0) {
                User user = users.get(getAdapterPosition());
                goToChatActivity(user);
            }
        }
    }

    public void goToChatActivity(User targetUser) {
        Intent intent;
        intent = new Intent(mActivity, ChatActivity.class);
        intent.putExtra("User", targetUser);
        intent.putExtra("set", "true");
        mActivity.startActivity(intent);
    }

    public UsersRecyclerViewAdapter(Activity activity, List<User> users) {
        this.users = users;
        this.mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_user, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (position >= users.size()) {
            return;
        }

        User item = users.get(position);
        holder.tvUsername.setText(item.getUsername());
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

    public void addAll(List<MMXUser> mmxUsers) {
        for (int i = 0; i < mmxUsers.size(); i++) {
            User user = new User();
            user.setUsername(mmxUsers.get(i).getUsername());
            users.add(user);
        }
    }
}
