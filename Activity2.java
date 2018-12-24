package com.example.keenan.scanner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.String;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;

public class Activity2 extends AppCompatActivity implements MachineAdapter.PassDataToActivity {

    public static final String SEND_DATA = "com.example.keenan.scanner.SEND_DATA";
    public static final String MACHINE_LABEL = "com.example.keenan.scanner.MACHINE_LABEL";
    private RecyclerView mRecyclerView;
    private MachineAdapter mAdapter;
    private TextView mMachineLabel;
    private final String TITLE_ACTIVITY = "Machine: ";
    private HashMap<String, String> machineDataWithInput;
    private HashMap<String, String> generalMachine = new HashMap<>();
    private String getIntentMachineLabel;
    private String machineLabel;

    //dummy values for a machine object..
    private static final HashMap<String, String> machineMap;
    static
    {
        machineMap = new HashMap<String, String>();
        machineMap.put("Coolant", "");
        machineMap.put("Tool life", "");
        machineMap.put("Other Params", "");
        /*machineMap.put("Other2", "");
        machineMap.put("Other3", "");
        machineMap.put("Other4", "");
        machineMap.put("Other5", "");
        machineMap.put("Other6", "");
        machineMap.put("Other7", "");*/

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        generalMachine.putAll(machineMap);
        super.onCreate(savedInstanceState);
        Log.d("Activity2Oncreate", "Creating activity 2");
        setContentView(R.layout.recycler_view_act2);

        System.out.println("Machine data initialized to: " + generalMachine);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mMachineLabel = (TextView) findViewById(R.id.tv_machineLabel);

        // Get the Intent that started this activity and extract the string, adding it to the standard text "Machine"
        Intent intent = getIntent();
        getIntentMachineLabel = intent.getStringExtra(MainActivity.EXTRA_TEXT);
        machineLabel = TITLE_ACTIVITY + getIntentMachineLabel;
        mMachineLabel.setText(machineLabel);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MachineAdapter(generalMachine, Activity2.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    //code for the creation of the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity2, menu);
        return true;
    }
    //handles clicks on the menu buttons, Click listener for sending data to a database...
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String correctedValue;
        //menu items are identified by integer i.d's
        int menuItemSelected = item.getItemId();

        if (menuItemSelected == R.id.action_confirmData)  {
            Log.i("confirmedDataPressed", "confirmDataPressed..." );
            buildAlert();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void PassData(HashMap dataSet) {
        machineDataWithInput = dataSet;
    }

    private void buildAlert() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Confirm Data")
                .setMessage("Are you sure your data is correct? You won't get a chance to change it.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //these checks cover the case where they do not input any data
                        if (machineDataWithInput != null && getIntentMachineLabel != null) {
                            openPushToDatabaseActivity(machineDataWithInput, machineLabel);
                        } else if (getIntentMachineLabel == null) {
                            Toast.makeText(Activity2.this, "Machine type is null! You must go back and scan a machine first!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Activity2.this, "You must input data to continue.", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void openPushToDatabaseActivity(HashMap data, String label) {
        Intent intent = new Intent(this, pushToDatabaseActivity.class);
        Bundle extras = new Bundle();
        extras.putString(MACHINE_LABEL, label);
        extras.putSerializable(SEND_DATA, data);
        intent.putExtras(extras);
        startActivity(intent);
    }


}

