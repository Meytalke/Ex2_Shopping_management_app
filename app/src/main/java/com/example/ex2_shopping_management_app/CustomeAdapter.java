package com.example.ex2_shopping_management_app;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ex2_shopping_management_app.models.ModelData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CustomeAdapter extends RecyclerView.Adapter<CustomeAdapter.ViewHolder> {

    private ArrayList<ModelData> productList;
    private Context context;
    private String username;

    public CustomeAdapter(ArrayList<ModelData> productList, Context context, String username) {
        this.productList = productList;
        this.context = context;
        this.username = username;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelData product = productList.get(position);

        holder.imageView.setImageResource(product.getImage());
        holder.nameTextView.setText(product.getProductName());
        String quantityText = String.valueOf(product.getQuantity());
        holder.quantityTextView.setText("Quantity: " +quantityText);

        holder.btnAddQuantity.setOnClickListener(v -> {
            int newQuantity = product.getQuantity() + 1;
            product.setQuantity(newQuantity);
            updateProductQuantityInFirebase(product.getProductName(), newQuantity);
            notifyItemChanged(position);
        });

        holder.btnRemoveQuantity.setOnClickListener(v -> {
            if (product.getQuantity() > 0) {
                int newQuantity = product.getQuantity() - 1;
                product.setQuantity(newQuantity);
                updateProductQuantityInFirebase(product.getProductName(), newQuantity);
                notifyItemChanged(position);
            } else {
                Toast.makeText(context, "Quantity cannot be less than 0", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnDeleteProduct.setOnClickListener(v -> {
            deleteProductFromFirebase(product.getProductName());
            productList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView quantityTextView;
        Button btnAddQuantity, btnRemoveQuantity, btnDeleteProduct;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageID);
            nameTextView = itemView.findViewById(R.id.nameID);
            quantityTextView = itemView.findViewById(R.id.quantityID);
            btnAddQuantity = itemView.findViewById(R.id.btnAddQuantity);
            btnRemoveQuantity = itemView.findViewById(R.id.btnRemoveQuantity);
            btnDeleteProduct = itemView.findViewById(R.id.btnDeleteProduct);
        }
    }
    private void updateProductQuantityInFirebase(String productId, int newQuantity) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference productRef = database.getReference("user_products")
                .child(username)
                .child("products")
                .child(productId);

        productRef.child("quantity").setValue(newQuantity)
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    private void deleteProductFromFirebase(String productId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference productRef = database.getReference("user_products")
                .child(username)
                .child("products")
                .child(productId);

        productRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }
}