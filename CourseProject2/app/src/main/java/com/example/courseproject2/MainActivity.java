package com.example.courseproject2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

public class MainActivity extends Activity {

    private Button[][] buttons = new Button[3][3];
    private TableLayout layout;
    Client client;

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
            if (buttons[x][y].getText() == "") {
                client.write(Integer.toString(x), Integer.toString(y));
                buttons[x][y].setText(client.getPlayer());
            }
        }
    }

    public class ClientListener implements View.OnClickListener {

        public ClientListener() {
        }

        public void onClick(View view) {
            if (client == null) {
                client = new Client(6666, buttons);
                client.execute();
            }
        }
    }

    private void buildGameField() {
        for (int i = 0, lenI = 3; i < lenI; i++) {
            TableRow row = new TableRow(this);                          //Cоздание строки таблицы
            for (int j = 0, lenJ = 3; j < lenJ; j++) {
                Button button = new Button(this);
                buttons[i][j] = button;                                         //Добавляем кнопку в поле
                button.setOnClickListener(new Listener(i, j));                  //Установка слушателя, реагирующего на клик по кнопке
                row.addView(button, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));                   //Добавление кнопки в строку таблицы
                button.setWidth(107);
                button.setHeight(107);
            }
            layout.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));                    //Добавление строки в таблицу
        }
    }

    private void buildServerClient() {
        TableRow row = new TableRow(this);

        Button clientButton = new Button(this);
        clientButton.setText("connect to server");
        clientButton.setOnClickListener(new ClientListener());
        row.addView(clientButton, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        clientButton.setWidth(100);
        clientButton.setHeight(100);

        layout.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = (TableLayout) findViewById(R.id.main_l);
        buildGameField();
        buildServerClient();
    }
}
