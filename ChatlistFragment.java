package com.example.myemechanic;


import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatlistFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ModelChatList> chatListList;
    List<Technician> technicianList;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    AdapterChatList adapterChatList;
    List<ModelChat> chatList;

    public ChatlistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.from(container.getContext())
                .inflate(R.layout.fragment_chatlist, container, false);
        // Inflate the layout for this fragment
      // View view = inflater.inflate(R.layout.fragment_chatlist, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        // getting current user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = view.findViewById(R.id.chatlistrecycle);
        chatListList = new ArrayList<>();
        chatList = new ArrayList<>();
      //  technicianList = new ArrayList<Technician>();
        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatListList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelChatList modelChatList = ds.getValue(ModelChatList.class);
                    if (!modelChatList.getId().equals(firebaseUser.getUid())) {
                        chatListList.add(modelChatList);
                    }

                }

                loadChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    // loading the user chat layout using chat node
    private void loadChats() {
      technicianList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Technicians");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                technicianList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Technician user = dataSnapshot1.getValue(Technician.class);
                    for (ModelChatList chatList : chatListList) {
                        assert user != null;
                        if (user.getUserid() != null && user.getUserid().equals(chatList.getId())) {

                           // technicianList=new ArrayList<Technician>();
                            technicianList.add(user);
                            break;
                        }else {
                            Log.d(TAG, "onDataChange: empty data found");
                        }
                    }
                    //adapterChatList = new AdapterChatList(getActivity(), technicianList);
                    //recyclerView.setAdapter(adapterChatList);



                    recyclerView.setHasFixedSize(true);
                    adapterChatList= new AdapterChatList(getActivity(), technicianList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(adapterChatList);

                    // getting last message of the user
                    for (int i = 0; i < technicianList.size(); i++) {
                        lastMessage(technicianList.get(i).getUserid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void lastMessage(final String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String lastmess = "default";
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ModelChat chat = dataSnapshot1.getValue(ModelChat.class);
                    if (chat == null) {
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if (sender == null || receiver == null) {
                        continue;
                    }
                    // checking for the type of message if
                    // message type is image then set
                    // last message as sent a photo
                    if (chat.getReceiver().equals(firebaseUser.getUid()) &&
                            chat.getSender().equals(uid) ||
                            chat.getReceiver().equals(uid) &&
                                    chat.getSender().equals(firebaseUser.getUid())) {
                        if (chat.getType().equals("images")) {
                            lastmess = "Sent a Photo";
                        } else {
                            lastmess = chat.getMessage();
                        }
                    }
                }
                adapterChatList.setlastMessageMap(uid, lastmess);;
               //requireActivity().runOnUiThread(() -> adapterChatList.notifyDataSetChanged());
              // adapterChatList.setlastMessageMap(uid, lastmess);
               adapterChatList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

}
