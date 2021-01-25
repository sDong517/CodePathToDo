package com.example.codepathtodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;


    List<String> items;

    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Match the current variables to the buttons/plaintexts we have made
        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);

        //Add the strings into the list, which should automatically be displayed on the screen upon startup.
        loadItems();
        //items = new ArrayList<>();
        //items.add("Buy milk");
        //items.add("Go to the gym");
        //items.add("Have fun!");


        //Long pressed function
        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener(){
            @Override
            public void onItemLongClicked(int position) {
                //delete the item then notify adapter which position was deleted
                items.remove(position);
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(),"item has been removed!", Toast.LENGTH_SHORT).show();
                saveItems();

            }
        };

        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener(){

            @Override
            public void onItemClicked(int position) {
                Log.d("MainActivity", "Single click at " + position);
                //Create activity then pass the data and display it

                //intents open up another activity
                //MainActivity.this means the current instance
                //EditActivity means the class, meaning the class we would like to go into
                Intent i = new Intent(MainActivity.this, EditActivity.class);

                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION,position);

                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };

        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        //Upon typing into the plainText file and clicking the add button, the selected text will be added to the list
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = etItem.getText().toString();                      //getText returns editable, so turn to string to simply display
                //Add item to model, then notify the adapter that an item is inserted.
                items.add(todoItem);

                itemsAdapter.notifyItemInserted(items.size() - 1);
                etItem.setText("");

                Toast.makeText(getApplicationContext(),"item has been added!", Toast.LENGTH_SHORT).show();              //A toast is a way to give feedback upon user action, such as a popup
                saveItems();
            }
        });

    }

    //handle result of edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE){
            //retrieve the updated data
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            //extract original position of the edited item
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            //update the model
            items.set(position, itemText);
            //Then notify the adapter
            itemsAdapter.notifyItemChanged(position);
            //Persist the changes
            saveItems();
            Toast.makeText(getApplicationContext(), "Item updated!", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "Unknown call to on ActivityResult");
        }

    }

    private File getDataFile(){
        return new File(getFilesDir(), "data.txt");
    }

    //read every line of every line
    private void loadItems(){
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity","Error reading items", e);
            items = new ArrayList<>();
        }
    }

    //Saves item by writing into data file
    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(),items);
        } catch (IOException e) {
            Log.e("MainActivity","Error writing items", e);

        }
    }

}