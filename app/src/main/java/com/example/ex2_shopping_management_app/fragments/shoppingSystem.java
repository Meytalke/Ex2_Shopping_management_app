package com.example.ex2_shopping_management_app.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ex2_shopping_management_app.CustomeAdapter;
import com.example.ex2_shopping_management_app.R;
import com.example.ex2_shopping_management_app.models.ModelData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class shoppingSystem extends Fragment {
    private String username;
    private RecyclerView recyclerView;
    private ArrayList<ModelData> productList;
    private CustomeAdapter adapter;
    private DatabaseReference databaseReference;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public shoppingSystem() {
    }

    public static shoppingSystem newInstance(String param1, String param2) {
        shoppingSystem fragment = new shoppingSystem();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_system, container, false);
        TextView userNameTextView = view.findViewById(R.id.userNameTextView);
        recyclerView = view.findViewById(R.id.resView);
        Button addProductButtonInShopping = view.findViewById(R.id.addProductButtonInShoppoing);

        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
        username = prefs.getString("username", "Guest");
        userNameTextView.setText("Welcome, " + username);

        productList = new ArrayList<>();
        adapter = new CustomeAdapter(productList, getContext(),username);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        fetchProductsFromFirebase();

        addProductButtonInShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_shoppingSystem_to_addProduct);
            }
        });

        return view;
    }

    private void fetchProductsFromFirebase() {
        DatabaseReference userProductsRef = FirebaseDatabase.getInstance().getReference("user_products").child(username).child("products");

        userProductsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                productList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelData product = dataSnapshot.getValue(ModelData.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("Error fetching data: " + error.getMessage());
            }
        });
    }
}