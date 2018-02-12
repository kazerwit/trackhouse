package org.trackhouse.trackhouse;

import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


//TODO: Search view is white and doesn't show with black background, fix this
//TODO: and set up search view capability
public class Listen extends AppCompatActivity {

    private RecyclerView userRecycleView;
    private DatabaseReference databaseReference;
    private SearchView searchView;
    private static final String TAG = "Listen Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        Log.d(TAG, "onCreate successful");

        //create firebase database reference and keep synced
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.keepSynced(true);
        Log.d(TAG, "database reference and sync successful");

        //set up recyclerview
        userRecycleView = (RecyclerView) findViewById(R.id.user_list_recycle);
        searchView = (SearchView) findViewById(R.id.search_view);
        userRecycleView.setHasFixedSize(true);
        userRecycleView.setLayoutManager(new LinearLayoutManager(this));
        Log.d(TAG, "recycle view setup successful");

    }

    /**
     * Runs on starting activity. Creates the Firebase recycler adapter
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "super.onStart successful");
        FirebaseRecyclerAdapter<UserListView, UserListViewHolder>firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<UserListView, UserListViewHolder>
                (UserListView.class, R.layout.user_list_items,UserListViewHolder.class,databaseReference) {
            /**
             * Populates the view holder using setUsername and getUsername from the UserListView class
             * @param viewHolder
             * @param model
             * @param position
             */
            @Override
            protected void populateViewHolder(UserListViewHolder viewHolder, UserListView model, int position) {
                viewHolder.setUsername(model.getUsername());
                Log.d(TAG, "populate view holder successful");
            }
        };

        //sets firebase recycler adapter to the recycler view
        userRecycleView.setAdapter(firebaseRecyclerAdapter);
        Log.d(TAG, "set adapter successful");
    }

    /**
     * Creates static class for view holder
     */
    public static class UserListViewHolder extends RecyclerView.ViewHolder {
        View mView;

        /**
         * Creates itemView in UserListViewHolder
         * @param itemView
         */
        public UserListViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
            Log.d(TAG, "set mView to itemView successful");
        }

        /**
         * Sets username to text view using mView from view holder
         * @param username
         */
        //TODO: create setDistance for distance later. Also add getters and setters to UserListView class
        public void setUsername(String username) {
            TextView usernameView = (TextView)mView.findViewById(R.id.username);
            usernameView.setText(username);
            Log.d(TAG, "setUsername successful");
        }

    }
}
