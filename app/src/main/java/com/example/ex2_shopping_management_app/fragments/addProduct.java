package com.example.ex2_shopping_management_app.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ex2_shopping_management_app.MyData;
import com.example.ex2_shopping_management_app.R;
import com.example.ex2_shopping_management_app.models.Product;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class addProduct extends Fragment {

    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        Spinner productSpinner = view.findViewById(R.id.productSpinner);
        EditText quantityEditText = view.findViewById(R.id.productQuantityEditText);
        TextView productPriceText = view.findViewById(R.id.productPriceText);
        ImageView productImage = view.findViewById(R.id.productimageID);
        Button addProductButton = view.findViewById(R.id.addProductButton);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, MyData.name);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productSpinner.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("user_products");

        productSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedProduct = parentView.getItemAtPosition(position).toString();

                String price = MyData.price[position];
                productPriceText.setText("Price: " + price);

                int imageResId = MyData.picture[position];
                productImage.setImageResource(imageResId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        addProductButton.setOnClickListener(v -> {
            String selectedProduct = productSpinner.getSelectedItem().toString();
            String quantityText = quantityEditText.getText().toString().trim();

            if (quantityText.isEmpty()) {
                quantityEditText.setError("Please enter a quantity");
                return;
            }

            int quantity = Integer.parseInt(quantityText);

            String username = requireActivity()
                    .getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE)
                    .getString("username", "Guest");

            if ("Guest".equals(username)) {
                Toast.makeText(getContext(), "User not recognized. Please log in.", Toast.LENGTH_SHORT).show();
                return;
            }

            saveProductToFirebase(username, selectedProduct, quantity);
        });

        return view;
    }

    private void saveProductToFirebase(String username, String product, int quantity) {
        int position = -1;
        for (int i = 0; i < MyData.name.length; i++) {
            if (MyData.name[i].equals(product)) {
                position = i;
                break;
            }
        }

        int imageName = (position != -1 && position < MyData.picture.length) ? MyData.picture[position] : -1;

        DatabaseReference productRef = databaseReference
                .child(username)
                .child("products")
                .child(product);

        productRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                int existingQuantity = task.getResult().child("quantity").getValue(Integer.class);
                int updatedQuantity = existingQuantity + quantity;

                productRef.child("quantity").setValue(updatedQuantity)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Quantity updated successfully!", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(requireView()).navigate(R.id.action_addProduct_to_shoppingSystem);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to update quantity.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Product productData = new Product(product, quantity, imageName);

                productRef.setValue(productData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Product added successfully!", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(requireView()).navigate(R.id.action_addProduct_to_shoppingSystem);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to add product.", Toast.LENGTH_SHORT).show();
                        });
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error checking product existence.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        });
    }

}
