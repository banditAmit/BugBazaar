package com.BugBazaar.ui.myorders;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryDatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "OrderHistory.db";

    public static class OrderHistoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "order_history";
        public static final String COLUMN_ORDER_ID = "order_id";  // Rename it to store order IDs
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRODUCT_QUANTITY = "product_quantity";
        public static final String COLUMN_FINAL_COST = "final_cost";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + OrderHistoryEntry.TABLE_NAME + " (" +
                    OrderHistoryEntry._ID + " INTEGER PRIMARY KEY," +
                    OrderHistoryEntry.COLUMN_ORDER_ID + " TEXT," + // Add space and comma here
                    OrderHistoryEntry.COLUMN_PRODUCT_NAME + " TEXT," +
                    OrderHistoryEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER," +
                    OrderHistoryEntry.COLUMN_FINAL_COST + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + OrderHistoryEntry.TABLE_NAME;

    public OrderHistoryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public List<String> getAllOrderProducts(String orderId) {
        List<String> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Define the columns to retrieve from the order items table
        String[] projection = {
                OrderHistoryEntry.COLUMN_PRODUCT_NAME

        };

        String selection = OrderHistoryEntry.COLUMN_ORDER_ID + " = ?";
        String[] selectionArgs = { orderId };
        // Query the database to retrieve order products for the specified order ID
        Cursor cursor = db.query(
                OrderHistoryEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        // Iterate through the cursor to create a list of product names
        while (cursor.moveToNext()) {
            String productName = cursor.getString(cursor.getColumnIndexOrThrow(OrderHistoryEntry.COLUMN_PRODUCT_NAME));
            products.add(productName);
        }

        cursor.close();
        return products;
    }
    public List<Integer> getAllOrderProductQuantities(String orderId) {
        List<Integer> quantities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                OrderHistoryEntry.COLUMN_PRODUCT_QUANTITY
        };

        String selection = OrderHistoryEntry.COLUMN_ORDER_ID + " = ?";
        String[] selectionArgs = { orderId };

        Cursor cursor = db.query(
                OrderHistoryEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            int productQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(OrderHistoryEntry.COLUMN_PRODUCT_QUANTITY));
            quantities.add(productQuantity);
        }

        cursor.close();
        return quantities;
    }

    public int getFinalCostForOrder(String orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int finalCost = 0;

        String[] projection = {
                OrderHistoryEntry.COLUMN_FINAL_COST
        };

        String selection = OrderHistoryEntry.COLUMN_ORDER_ID + " = ?";
        String[] selectionArgs = { orderId };

        Cursor cursor = db.query(
                OrderHistoryEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            finalCost = cursor.getInt(cursor.getColumnIndexOrThrow(OrderHistoryEntry.COLUMN_FINAL_COST));
        }

        cursor.close();
        return finalCost;
    }
    public List<OrderHistoryItem> getAllOrderItemsz() {
        List<OrderHistoryItem> orderItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Define the columns to retrieve from the order items table
        String[] projection = {
                OrderHistoryEntry.COLUMN_ORDER_ID,
                OrderHistoryEntry.COLUMN_PRODUCT_NAME,
                OrderHistoryEntry.COLUMN_PRODUCT_QUANTITY,
                OrderHistoryEntry.COLUMN_FINAL_COST
        };

        // Query the database to retrieve all order items
        Cursor cursor = db.query(
                OrderHistoryEntry.TABLE_NAME,
                projection,
                null,  // No selection
                null,  // No selectionArgs
                null,
                null,
                OrderHistoryEntry.COLUMN_ORDER_ID + " DESC"
        );

        String currentOrderID = null;
        List<String> productNames = new ArrayList<>();
        List<Integer> productQuantities = new ArrayList<>();
        int finalCost = 0;

        while (cursor.moveToNext()) {
            String orderID = cursor.getString(cursor.getColumnIndexOrThrow(OrderHistoryEntry.COLUMN_ORDER_ID));
            String productName = cursor.getString(cursor.getColumnIndexOrThrow(OrderHistoryEntry.COLUMN_PRODUCT_NAME));
            int productQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(OrderHistoryEntry.COLUMN_PRODUCT_QUANTITY));
            int currentFinalCost = cursor.getInt(cursor.getColumnIndexOrThrow(OrderHistoryEntry.COLUMN_FINAL_COST));

            if (currentOrderID == null || !currentOrderID.equals(orderID)) {
                // If a new order ID is encountered, create a new OrderHistoryItem
                if (currentOrderID != null) {
                    OrderHistoryItem orderItem = new OrderHistoryItem(currentOrderID, productNames, productQuantities, finalCost);
                    orderItems.add(orderItem);
                }

                // Initialize values for the new order
                currentOrderID = orderID;
                productNames = new ArrayList<>();
                productQuantities = new ArrayList<>();
                finalCost = 0;
            }

            // Add the product name and quantity to the lists
            productNames.add(productName);
            productQuantities.add(productQuantity);
            // Accumulate the final cost
            finalCost += currentFinalCost;
        }

        // Add the last order to the list
        if (currentOrderID != null) {
            OrderHistoryItem orderItem = new OrderHistoryItem(currentOrderID, productNames, productQuantities, finalCost);
            orderItems.add(orderItem);
        }

        cursor.close();
        return orderItems;
    }






    public String findLastOrderID(SQLiteDatabase db) {
        String lastOrderID = "ORDER-100"; // A default order ID if no records exist yet
        Cursor cursor = db.query(
                OrderHistoryEntry.TABLE_NAME,
                new String[]{OrderHistoryEntry.COLUMN_ORDER_ID},
                null,
                null,
                null,
                null,
                OrderHistoryEntry._ID + " DESC",
                "1"
        );

        if (cursor.moveToFirst()) {
            lastOrderID = cursor.getString(cursor.getColumnIndexOrThrow(OrderHistoryEntry.COLUMN_ORDER_ID));
        }

        cursor.close();
        return lastOrderID;
    }



    public String incrementOrderID(String lastOrderID) {
        String numericPart = lastOrderID.substring("ORDER-".length());
        int orderNumber = Integer.parseInt(numericPart);
        orderNumber++;
        return "ORDER-" + orderNumber;
    }
}