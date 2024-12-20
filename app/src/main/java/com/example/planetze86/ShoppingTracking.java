package com.example.planetze86;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class ShoppingTracking extends AppCompatActivity {

    FirebaseManager firebaseManager = new FirebaseManager();
    private boolean isProgrammaticClick = false;
    ShoppingActivityElement toBeEdited;
    @Override
    protected void onResume() {
        super.onResume();

        String activityId = getIntent().getStringExtra("ACTIVITY_ID");
        String selectedDate = getIntent().getStringExtra("SELECTED_DATE_UPDATE");

        if (activityId != null && selectedDate != null) {
            firebaseManager.retrieveActivitiesByType(
                    selectedDate,
                    "Shopping",
                    ShoppingActivityElement.class,
                    activities -> {
                        for (ShoppingActivityElement activity : activities) {
                            if (activity.getId().equals(activityId)) {
                                prefillFields(activity);
                                break;
                            }
                        }
                    }
            );
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        String activityId = getIntent().getStringExtra("ACTIVITY_ID");
        String selectedDateUpdate = getIntent().getStringExtra("SELECTED_DATE_UPDATE");

        if (activityId != null && selectedDateUpdate != null) {
            firebaseManager.retrieveActivitiesByType(
                    selectedDateUpdate,
                    "Shopping",
                    ShoppingActivityElement.class,
                    activities -> {
                        for (ShoppingActivityElement activity : activities) {
                            if (activity.getId().equals(activityId)) {
                                prefillFields(activity);
                                break;
                            }
                        }
                    }
            );
        }
        if ((selectedDate != null && !selectedDate.isEmpty()) || (selectedDateUpdate != null && !selectedDateUpdate.isEmpty())) {
            Toast.makeText(this, "Selected Date: " + ((selectedDate != null) ? selectedDate : selectedDateUpdate), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No date selected", Toast.LENGTH_SHORT).show();
        }
        setContentView(R.layout.activity_shoppingconsumption_tracking);

        // Button references
        Button buyNewClothesButton = findViewById(R.id.button_buy_new_clothes);
        Button buyElectronicsButton = findViewById(R.id.button_buy_electronics);
        Button otherPurchasesButton = findViewById(R.id.button_other_purchases);
        Button energyBillsButton = findViewById(R.id.button_energy_bills);
        ImageButton backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ShoppingTracking.this, EcoTracker.class);
            startActivity(intent);
        });
        // Dialog for Buying New Clothes
        buyNewClothesButton.setOnClickListener(v -> {
            Dialog dialog = new Dialog(ShoppingTracking.this);
            dialog.setContentView(R.layout.activity_new_clothing_logging);

            dialog.getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
            if(isProgrammaticClick){
                prefillClothingDialog(dialog,toBeEdited);
            }
            else {
                EditText etClothes = dialog.findViewById(R.id.et_clothes);
                Button btnSaveClothes = dialog.findViewById(R.id.btn_save_clothes);

                btnSaveClothes.setOnClickListener(view -> {
                    String clothesCountStr = etClothes.getText().toString();

                    if (clothesCountStr.isEmpty()) {
                        Toast.makeText(ShoppingTracking.this, "Please enter the number of items", Toast.LENGTH_SHORT).show();
                    } else {
                        int clothesCount = Integer.parseInt(clothesCountStr);
                        // Create ShoppingActivityElement
                        ShoppingActivityElement shoppingActivity = new ShoppingActivityElement(
                                selectedDate,
                                "Clothing",
                                "", // No subcategory for clothing
                                clothesCount,
                                0
                        );

                        // Save to Firebase
                        firebaseManager.saveActivity(selectedDate, "Shopping", shoppingActivity);

                        Toast.makeText(ShoppingTracking.this, "Saved: " + clothesCount + " clothing items", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        // Dialog for Buying Electronics
        buyElectronicsButton.setOnClickListener(v -> {
            Dialog dialog = new Dialog(ShoppingTracking.this);
            dialog.setContentView(R.layout.activity_new_electronics_logging);

            dialog.getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);

            if(isProgrammaticClick){
                prefillElectronicsDialog(dialog,toBeEdited);
            }
            else {
                AutoCompleteTextView actvDeviceType = dialog.findViewById(R.id.actv_device_type);
                EditText etDevices = dialog.findViewById(R.id.et_devices);
                Button btnSaveElectronics = dialog.findViewById(R.id.btn_save_electronics);

                // Populate dropdown for device type
                String[] deviceTypes = {"Smartphone", "Laptop", "Tablet", "Television"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ShoppingTracking.this, android.R.layout.simple_dropdown_item_1line, deviceTypes);
                actvDeviceType.setAdapter(adapter);
                actvDeviceType.setOnClickListener(w -> actvDeviceType.showDropDown());

                btnSaveElectronics.setOnClickListener(view -> {
                    String deviceType = actvDeviceType.getText().toString();
                    String devicesCountStr = etDevices.getText().toString();

                    if (deviceType.isEmpty() || devicesCountStr.isEmpty()) {
                        Toast.makeText(ShoppingTracking.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    } else {
                        int devicesCount = Integer.parseInt(devicesCountStr);


                        // Create ShoppingActivityElement
                        ShoppingActivityElement shoppingActivity = new ShoppingActivityElement(
                                selectedDate,
                                "Electronics",
                                deviceType,
                                devicesCount,
                                0
                        );

                        // Save to Firebase
                        firebaseManager.saveActivity(selectedDate, "Shopping", shoppingActivity);

                        Toast.makeText(ShoppingTracking.this, "Saved: " + devicesCount + " " + deviceType + "(s)", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        // Dialog for Other Purchases
        otherPurchasesButton.setOnClickListener(v -> {
            Dialog dialog = new Dialog(ShoppingTracking.this);
            dialog.setContentView(R.layout.activity_other_purchases_logging);

            dialog.getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);

            if(isProgrammaticClick){
                prefillMiscellaneousDialog(dialog,toBeEdited);
            }
            else {
                AutoCompleteTextView actvPurchaseType = dialog.findViewById(R.id.actv_purchase_type);
                EditText etPurchases = dialog.findViewById(R.id.et_purchases);
                Button btnSaveOther = dialog.findViewById(R.id.btn_save_other);

                // Populate dropdown for purchase type
                String[] purchaseTypes = {"Furniture", "Appliances", "Miscellaneous"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ShoppingTracking.this, android.R.layout.simple_dropdown_item_1line, purchaseTypes);
                actvPurchaseType.setAdapter(adapter);
                actvPurchaseType.setOnClickListener(w -> actvPurchaseType.showDropDown());

                btnSaveOther.setOnClickListener(view -> {
                    String purchaseType = actvPurchaseType.getText().toString();
                    String purchasesCountStr = etPurchases.getText().toString();

                    if (purchaseType.isEmpty() || purchasesCountStr.isEmpty()) {
                        Toast.makeText(ShoppingTracking.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    } else {
                        int purchasesCount = Integer.parseInt(purchasesCountStr);


                        // Create ShoppingActivityElement
                        ShoppingActivityElement shoppingActivity = new ShoppingActivityElement(
                                selectedDate,
                                "Miscellaneous",
                                purchaseType,
                                purchasesCount,
                                0
                        );

                        // Save to Firebase
                        firebaseManager.saveActivity(selectedDate, "Shopping", shoppingActivity);

                        Toast.makeText(ShoppingTracking.this, "Saved: " + purchasesCount + " " + purchaseType + "(s)", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        energyBillsButton.setOnClickListener(v -> {
            // Create a custom dialog
            Dialog dialog = new Dialog(ShoppingTracking.this);
            dialog.setContentView(R.layout.activity_energy_bills);

            // Set dialog width and height
            dialog.getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);

            if(isProgrammaticClick){
                prefillEnergyBillsDialog(dialog,toBeEdited);
            }
            else {
                // Access elements in the dialog
                AutoCompleteTextView actvBillType = dialog.findViewById(R.id.actv_bill_type);
                EditText etBillAmount = dialog.findViewById(R.id.et_bill_amount);
                Button btnSaveBill = dialog.findViewById(R.id.btn_save_energy_bill);

                // Populate dropdown for bill type
                String[] billTypes = {"Gas", "Electricity", "Water"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ShoppingTracking.this, android.R.layout.simple_dropdown_item_1line, billTypes);
                actvBillType.setAdapter(adapter);
                actvBillType.setOnClickListener(w -> actvBillType.showDropDown());

                // Save button logic
                btnSaveBill.setOnClickListener(view -> {
                    String billType = actvBillType.getText().toString();
                    String billAmountStr = etBillAmount.getText().toString();

                    if (billType.isEmpty() || billAmountStr.isEmpty()) {
                        Toast.makeText(ShoppingTracking.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    } else {
                        double billAmount = Double.parseDouble(billAmountStr);

                        // Create ShoppingActivityElement for energy bills
                        ShoppingActivityElement shoppingActivity = new ShoppingActivityElement(
                                selectedDate,
                                billType,
                                billAmount
                        );

                        // Save to Firebase
                        firebaseManager.saveActivity(selectedDate, "Shopping", shoppingActivity);

                        Toast.makeText(ShoppingTracking.this, "Saved: " + billType + " bill of $" + billAmount, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

    }
    private void prefillFields(ShoppingActivityElement activity) {
        toBeEdited = activity;

        isProgrammaticClick = true;
        switch (activity.getItemType()) {
            case "Clothing":
                findViewById(R.id.button_buy_new_clothes).performClick();
                break;

            case "Electronics":
                findViewById(R.id.button_buy_electronics).performClick();
                break;

            case "Miscellaneous":
                findViewById(R.id.button_other_purchases).performClick();
                break;
            case "Energy Bill":
                findViewById(R.id.button_energy_bills).performClick();
                break;

            default:
                Toast.makeText(this, "Unsupported category for pre-fill.", Toast.LENGTH_SHORT).show();
                break;
        }
        isProgrammaticClick = false;
    }
    private void prefillClothingDialog(Dialog dialog, ShoppingActivityElement activity) {
        EditText etClothes = dialog.findViewById(R.id.et_clothes);
        Button btnSaveClothes = dialog.findViewById(R.id.btn_save_clothes);

        etClothes.setText(String.valueOf(activity.getQuantity()));

        btnSaveClothes.setOnClickListener(view -> {
            String clothesCountStr = etClothes.getText().toString();

            if (!clothesCountStr.isEmpty()) {
                activity.setQuantity(Integer.parseInt(clothesCountStr));
                firebaseManager.updateActivity(activity.getDate(), "Shopping", activity);
                dialog.dismiss();
                finish();
            } else {
                Toast.makeText(this, "Please fill the fields.", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
    private void prefillElectronicsDialog(Dialog dialog, ShoppingActivityElement activity) {
        AutoCompleteTextView actvDeviceType = dialog.findViewById(R.id.actv_device_type);
        EditText etDevices = dialog.findViewById(R.id.et_devices);
        Button btnSaveElectronics = dialog.findViewById(R.id.btn_save_electronics);

        actvDeviceType.setText(activity.getSubCategory(), false);
        etDevices.setText(String.valueOf(activity.getQuantity()));

        btnSaveElectronics.setOnClickListener(view -> {
            String deviceType = actvDeviceType.getText().toString();
            String devicesCountStr = etDevices.getText().toString();

            if (!deviceType.isEmpty() && !devicesCountStr.isEmpty()) {
                activity.setSubCategory(deviceType);
                activity.setQuantity(Integer.parseInt(devicesCountStr));
                firebaseManager.updateActivity(activity.getDate(), "Shopping", activity);
                dialog.dismiss();
                finish();
            } else {
                Toast.makeText(this, "Please fill the fields.", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void prefillMiscellaneousDialog(Dialog dialog, ShoppingActivityElement activity) {
        AutoCompleteTextView actvPurchaseType = dialog.findViewById(R.id.actv_purchase_type);
        EditText etPurchases = dialog.findViewById(R.id.et_purchases);
        Button btnSaveOther = dialog.findViewById(R.id.btn_save_other);

        actvPurchaseType.setText(activity.getSubCategory(), false);
        etPurchases.setText(String.valueOf(activity.getQuantity()));

        btnSaveOther.setOnClickListener(view -> {
            String purchaseType = actvPurchaseType.getText().toString();
            String purchasesCountStr = etPurchases.getText().toString();

            if (!purchaseType.isEmpty() && !purchasesCountStr.isEmpty()) {
                activity.setSubCategory(purchaseType);
                activity.setQuantity(Integer.parseInt(purchasesCountStr));
                firebaseManager.updateActivity(activity.getDate(), "Shopping", activity);
                dialog.dismiss();
                finish();
            } else {
                Toast.makeText(this, "Please fill the fields.", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void prefillEnergyBillsDialog(Dialog dialog, ShoppingActivityElement activity) {
        AutoCompleteTextView actvBillType = dialog.findViewById(R.id.actv_bill_type);
        EditText etBillAmount = dialog.findViewById(R.id.et_bill_amount);
        Button btnSaveBill = dialog.findViewById(R.id.btn_save_energy_bill);

        actvBillType.setText(activity.getSubCategory(), false);
        etBillAmount.setText(String.valueOf(activity.getCost()));

        btnSaveBill.setOnClickListener(view -> {
            String billType = actvBillType.getText().toString();
            String billAmountStr = etBillAmount.getText().toString();

            if (!billType.isEmpty() && !billAmountStr.isEmpty()) {
                activity.setSubCategory(billType);
                activity.setCost(Double.parseDouble(billAmountStr));
                firebaseManager.updateActivity(activity.getDate(), "Shopping", activity);
                dialog.dismiss();
                finish();
            } else {
                Toast.makeText(this, "Please fill the fields.", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }


}
