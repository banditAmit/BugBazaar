package com.BugBazaar.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.BugBazaar.R;
import com.BugBazaar.ui.Product;

public class DetailedProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_product);
        //Toolbar title set
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Product Details");

        // Retrieve the product details passed from the adapter
        Intent intent = getIntent();
        Product product = intent.getParcelableExtra("product");

        // Use the product details to display the detailed information
        ImageView detailedImage = findViewById(R.id.detailedImage);
        TextView detailedName = findViewById(R.id.detailedName);
        TextView detailedDescription = findViewById(R.id.detailedDescription);
        TextView detailedPrice=findViewById(R.id.detailedPrice);

        detailedImage.setImageResource(product.getImageResId());
        detailedName.setText(product.getName());
        detailedDescription.setText(product.getDescription());
        detailedPrice.setText(product.getPrice());
    }
    //Code to handle backbutton
    public void onBackButtonClick(View view) {
        onBackPressed(); // Navigate back to the previous activity
    }
}