package ph.sms.xenenergy.xenloc.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import ph.sms.xenenergy.xenloc.ChatActivity;
import ph.sms.xenenergy.xenloc.R;
import ph.sms.xenenergy.xenloc.model.ChatRecord;

/**
 * Created by xesi on 12/12/2019.
 */

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {
    private List<ChatRecord> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    // data is passed into the constructor
    public InboxAdapter(Context context, List<ChatRecord> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.inbox_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String message = mData.get(position).getMessage();
        String name = mData.get(position).getEmail();
        holder.tvName.setText(name);
        holder.tvMessage.setText(message);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvName;
        TextView tvMessage;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());

            DatabaseReference rootRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("data")
                    .child(mData.get(getAdapterPosition()).getEmail().replaceAll("[-+.^:,@]",""))
                    .child("token");

            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    String token = dataSnapshot.getValue(String.class);
                    Toast.makeText(context, "TOKEN: " + token, Toast.LENGTH_SHORT).show();
                    intent.putExtra("Email", mData.get(getAdapterPosition()).getEmail());
                    intent.putExtra("Token", token);
                    context.startActivity(intent);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                }
            };
            rootRef.addListenerForSingleValueEvent(postListener);

        }
    }

//    // convenience method for getting data at click position
//    String getItem(int id) {
//        return mData.get(id);
//    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}
