package com.salbe.dare;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.salbe.dare.models.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CardDisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CardDisplayFragment extends Fragment implements MenuProvider {

    public static final String CURRENT_USER_UID = "CURRENT_USER_UID";
    public static final String DATABASE_URL = "https://dare-7b4dc-default-rtdb.europe-west1.firebasedatabase.app";
    public static final String TAG = "CardDisplayFragment";
    private final Map<String, Card> publicCards = new HashMap<>();
    private final Map<String, Card> privateCards = new HashMap<>();
    private FirebaseDatabase database;
    private ArrayList<Card> allCards = new ArrayList<>();
    private String currentUserUid;
    private MenuHost menuHost;


    public CardDisplayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param currentUserUid uid of current user.
     * @return A new instance of fragment CardDisplayFragment.
     */
    public static CardDisplayFragment newInstance(String currentUserUid) {
        CardDisplayFragment fragment = new CardDisplayFragment();
        Bundle args = new Bundle();
        args.putString(CURRENT_USER_UID, currentUserUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance(DATABASE_URL);
        if (getArguments() != null) {
            currentUserUid = getArguments().getString(CURRENT_USER_UID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_display, container, false);
        menuHost = requireActivity();
        menuHost.addMenuProvider(this);
        Button button = (Button) view.findViewById(R.id.generate_button);
        TextView text = (TextView) view.findViewById(R.id.card_message_text_view);
        button.setOnClickListener(v -> {
            text.setVisibility(View.INVISIBLE);
            String message = allCards.get(new Random().nextInt(allCards.size())).message;
            text.setText(message);
            text.setVisibility(View.VISIBLE);
        });
        return view;
    }


    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.clear();
        menuInflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        int i = menuItem.getItemId();
        if (i == R.id.action_logout) {
            menuHost.removeMenuProvider(this);
            FirebaseAuth.getInstance().signOut();
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_cardDisplayFragment_to_EmailPasswordFragment);
            return true;
        } else {
            return false;
        }
    }

    private void updateAllCards() {
        allCards = new ArrayList<>();
        allCards.addAll(publicCards.values());
        allCards.addAll(privateCards.values());
    }

    @Override
    public void onStart() {
        super.onStart();
        Query publicCardsQuery = database.getReference().child("public-cards");
        Query privateCardsQuery = database.getReference().child("private-cards").child(currentUserUid);
        privateCardsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                String key = dataSnapshot.getKey();
                Log.d(TAG, "onChildAdded:" + key);

                // A new card has been added, add it to the displayed list
                Card card = dataSnapshot.getValue(Card.class);
                privateCards.put(key, card);
                updateAllCards();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                String key = dataSnapshot.getKey();
                Log.d(TAG, "onChildChanged:" + key);

                // A card has changed, use the key to determine if we are displaying this
                // card and if so displayed the changed comment.
                Card card = dataSnapshot.getValue(Card.class);
                privateCards.put(key, card);
                updateAllCards();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A card has changed, use the key to determine if we are displaying this
                // card and if so remove it.
                String key = dataSnapshot.getKey();
                privateCards.remove(key);
                updateAllCards();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postCards:onCancelled", databaseError.toException());
            }
        });
        publicCardsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                String key = dataSnapshot.getKey();
                Log.d(TAG, "onChildAdded:" + key);

                // A new card has been added, add it to the displayed list
                Card card = dataSnapshot.getValue(Card.class);
                publicCards.put(key, card);
                updateAllCards();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                String key = dataSnapshot.getKey();
                Log.d(TAG, "onChildChanged:" + key);

                // A card has changed, use the key to determine if we are displaying this
                // card and if so displayed the changed comment.
                Card card = dataSnapshot.getValue(Card.class);
                publicCards.put(key, card);
                updateAllCards();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A card has changed, use the key to determine if we are displaying this
                // card and if so remove it.
                String key = dataSnapshot.getKey();
                publicCards.remove(key);
                updateAllCards();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postCards:onCancelled", databaseError.toException());
            }
        });
    }
}