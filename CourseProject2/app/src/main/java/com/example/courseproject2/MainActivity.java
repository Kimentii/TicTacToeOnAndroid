package com.example.courseproject2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.Semaphore;

public class MainActivity extends Activity {

    String IP = "10.0.2.2";
    final int BUTTON_HEIGHT = 150;
    final int BUTTON_WIDTH = 150;
    private Button[][] buttons = new Button[3][3];                      //поле из кнопок
    private TableLayout layout;
    private TextView text;                                              //текстовое поле
    Client client;                                                      //поле для работы с сокетом
    Semaphore semaphore;

    public class Listener implements View.OnClickListener {
        /**
         * Координаты слушателя
         */
        private int x = 0;
        private int y = 0;

        public Listener(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void onClick(View view) {
            if (buttons[x][y].getText() == "" && client != null && semaphore.tryAcquire()) {
                client.write(Integer.toString(x), Integer.toString(y));
                buttons[x][y].setText(client.getPlayer());
                text.setText("wait");
            }
        }
    }

    public class ClientListener implements View.OnClickListener {
        public void onClick(View view) {
            if (client == null) {
                semaphore = new Semaphore(1);
                semaphore.tryAcquire();
                Toast toast = Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT);
                client = new Client(6666, IP, buttons, text, toast, semaphore);
                client.execute();
            }
            if (client.isErrorInConnect()) {
                try {
                    client.cancel(true);
                } catch (Exception e) {
                    System.out.println("Error in ClientListener.");
                    e.printStackTrace();
                }
                Toast toast = Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT);
                client = new Client(6666, IP, buttons, text, toast, semaphore);
                client.execute();
                text.setText("Connecting...");
            }
        }
    }

    public class ExitButtonListener implements View.OnClickListener {
        public void onClick(View view) {
            finish();
        }
    }

    public class setIPButtonListener implements View.OnClickListener {
        public void onClick(View view) {
            if (client == null || client.isErrorInConnect()) {
                Intent intent = new Intent(getApplicationContext(), setIPActivity.class);
                startActivityForResult(intent, 1);
            }
        }
    }

    private void buildGameField() {
        TableRow row1 = new TableRow(this);                             //Первая колонка с текстом
        text = new TextView(this);
        text.setText("No connection.");
        row1.addView(text, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT));                   //Добавление пля тескта
        layout.addView(row1, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT));                    //Добавление строки в таблицу
        for (int i = 0; i < 3; i++) {
            TableRow row = new TableRow(this);                          //Cоздание строки таблицы
            for (int j = 0; j < 3; j++) {
                Button button = new Button(this);
                buttons[i][j] = button;                                         //Добавляем кнопку в поле
                button.setOnClickListener(new Listener(i, j));                  //Установка слушателя, реагирующего на клик по кнопке
                row.addView(button, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));                   //Добавление кнопки в строку таблицы
                button.setWidth(BUTTON_WIDTH);
                button.setHeight(BUTTON_HEIGHT);
            }
            layout.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT));                    //Добавление строки в таблицу
        }
    }

    private void buildServerClient() {
        TableRow row = new TableRow(this);

        Button clientButton = new Button(this);
        clientButton.setText("connect to server");
        clientButton.setOnClickListener(new ClientListener());
        row.addView(clientButton, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT));
        clientButton.setWidth(BUTTON_WIDTH);
        clientButton.setHeight(BUTTON_HEIGHT);

        Button exitButton = new Button(this);
        exitButton.setText("Exit");
        exitButton.setOnClickListener(new ExitButtonListener());
        row.addView(exitButton, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT));
        exitButton.setWidth(BUTTON_WIDTH);
        exitButton.setHeight(BUTTON_HEIGHT);

        Button setIPButton = new Button(this);
        setIPButton.setText("Set IP");
        setIPButton.setOnClickListener(new setIPButtonListener());
        row.addView(setIPButton, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT));
        setIPButton.setWidth(BUTTON_WIDTH);
        setIPButton.setHeight(BUTTON_HEIGHT);

        layout.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = (TableLayout) findViewById(R.id.main_l);
        buildGameField();
        buildServerClient();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        String ip = data.getStringExtra("IP").toString();
        if (ip.length() > 0) IP = ip;
    }

    @Override
    protected void onDestroy() {
        if (client != null) {
            try {
                client.write("closed");
                client.cancel(false);
            } catch (Exception e) {
                System.out.println("Error in exit button.");
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}