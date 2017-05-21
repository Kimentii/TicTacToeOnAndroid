package com.example.courseproject2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class setIPActivity extends AppCompatActivity {

    EditText editText;
    Button setIPButton;
    public class setIPButtonListener implements View.OnClickListener {
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.putExtra("IP", editText.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ip);

        editText = (EditText) findViewById(R.id.editText);
        setIPButton = (Button) findViewById(R.id.setIPButton);
        setIPButton.setOnClickListener(new setIPButtonListener());
    }

}
