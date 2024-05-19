package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class NotesActivity extends AppCompatActivity {

    FloatingActionButton mcreatenotesfab;
    private FirebaseAuth firebaseAuth;


    RecyclerView mrecyclerview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    FirestoreRecyclerAdapter<firebasemodel,NoteViewHolder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        mcreatenotesfab = findViewById(R.id.createnotesfab);
        firebaseAuth= firebaseAuth.getInstance();

//        particular user ka notes fetch kr rhe h
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

//        getSupportActionBar().setTitle("All Notes");
//          ~~~~~VIDEO NO *
        // calling this activity's function to
        // use ActionBar utility methods
//        ActionBar actionBar = getSupportActionBar();
//
//        // providing title for the ActionBar
//        actionBar.setTitle("  GfG | Action Bar");
        // ```````Get the ActionBar object```````
        ActionBar actionBar = getSupportActionBar();
       // Check if ActionBar is not null before setting title
        if (actionBar != null) {
            actionBar.setTitle("Your Notes");
        }

        mcreatenotesfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(NotesActivity.this, CreateNote.class));
            }
        });
//         below path is ssame as creation path in createNote
        Query query = firebaseFirestore.collection("notes").document(Objects.requireNonNull(firebaseAuth.getUid())).collection("myNotes").orderBy("title",Query.Direction.DESCENDING);

//        assigning data from firestore to recycler view
        FirestoreRecyclerOptions<firebasemodel> allusernotes = new  FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query,firebasemodel.class).build();

//        all data is inside allusernotes now we are going to set it into recycler view
        noteAdapter=new FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>(allusernotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull firebasemodel firebasemodel) {

                noteViewHolder.notetitle.setText(firebasemodel.getTitle());
                noteViewHolder.notecontent.setText(firebasemodel.getContent());

                String docId=noteAdapter.getSnapshots().getSnapshot(i).getId();

                ImageView popupbtn=noteViewHolder.itemView.findViewById(R.id.menupopbtn);

//                Random color
                int colourcode=getRandomColor();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    noteViewHolder.mnote.setBackgroundColor(noteViewHolder.itemView.getResources().getColor(colourcode,null));

                }
//                clickin on note
                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        we have to open note detail activity
                        Intent intent = new Intent(v.getContext(),notedetails.class);
//                        id pass kr rhe h hrr note kaaa
                        intent.putExtra("title",firebasemodel.getTitle());
                        intent.putExtra("content",firebasemodel.getContent());
                        intent.putExtra("noteId",docId);
                        v.getContext().startActivity(intent);
                        Toast.makeText(getApplicationContext(), "this is clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                popupbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(v.getContext(),v);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            popupMenu.setGravity((Gravity.END));
                            popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(@NonNull MenuItem item) {
//                                    edit pr click krne se ...
                                    Intent intent = new Intent(v.getContext(),editnoteactivity.class);
                                    intent.putExtra("title",firebasemodel.getTitle());
                                    intent.putExtra("content",firebasemodel.getContent());
                                    intent.putExtra("noteId",docId);
                                    v.getContext().startActivity(intent);
                                    v.getContext().startActivity(intent);
                                    return false;
                                }
                            });
                            popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(@NonNull MenuItem item) {
                                    //delete pr click krne se
//                                    Toast.makeText(v.getContext(), "Note deleted successfully", Toast.LENGTH_SHORT).show();
                                    DocumentReference documentReference =firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docId);
                                    documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(v.getContext(), "Notes deleted successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(v.getContext(), "Failed to delete", Toast.LENGTH_SHORT).show();

                                        }
                                    });

                                    return false;
                                }
                            });
                            popupMenu.show();
                        }
                    }
                });

            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout,parent,false);
                return new NoteViewHolder(view);
            }
        };

//        data connecting

        mrecyclerview=findViewById(R.id.recyclerview);
//        mrecyclerview.setHasFixedSize(true);
        staggeredGridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
        mrecyclerview.setAdapter(noteAdapter);

    }

    public class  NoteViewHolder extends RecyclerView.ViewHolder {

        private TextView notetitle;
        private TextView notecontent;
        LinearLayout mnote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            notetitle = itemView.findViewById(R.id.notetitle);
            notecontent=itemView.findViewById(R.id.notecontent);
            mnote = itemView.findViewById(R.id.note);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
//        return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(NotesActivity.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case  R.id.logout:
//                firebaseAuth.signOut();
//                finish();
//                startActivity(new Intent(NotesActivity.this, MainActivity.class));
//                // Handle the menu item click
////                return true;
//            // Handle other menu items...
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }


    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter!=null){
            noteAdapter.stopListening();
        }
    }

    private int getRandomColor(){
        List<Integer> colorcode=new ArrayList<>();
        colorcode.add(R.color.gray);
        colorcode.add(R.color.color1);
        colorcode.add(R.color.color2);
        colorcode.add(R.color.color3);
        colorcode.add(R.color.color4);
        colorcode.add(R.color.color5);
        colorcode.add(R.color.colorMatteShade6);
        colorcode.add(R.color.colorMatteShade10);
        colorcode.add(R.color.colorMatteShade7);
        colorcode.add(R.color.colorMatteShade8);
        colorcode.add(R.color.colorMatteShade9);
        colorcode.add(R.color.colorMatteShade10);
        colorcode.add(R.color.colorMatteBlueShade2);
        colorcode.add(R.color.colorMatteBlueShade1);
        colorcode.add(R.color.colorMatteBlueShade3);
        colorcode.add(R.color.colorMatteBlueShade4);
        colorcode.add(R.color.colorMatteBlueShade5);
        colorcode.add(R.color.colorMatteGreenBlueShade1);
        colorcode.add(R.color.colorMatteGreenBlueShade2);
        colorcode.add(R.color.colorMatteGreenBlueShade3);
        colorcode.add(R.color.colorMatteGreenBlueShade4);
        colorcode.add(R.color.colorMatteGreenBlueShade5);
        colorcode.add(R.color.colorAccentLight);


        Random random=new Random();
        int number=random.nextInt(colorcode.size());
        return colorcode.get(number);
    }
}