package com.example.courseproject2;

import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client extends AsyncTask<Void, String, Void> {

    String IP = "10.0.2.2";                                 //локальный IP
    int PORT;                                               //номер порта
    Button[][] buttons;                                     //ссылка на кнопки(игровое поле)
    TextView text;                                          //текстовое сообщение
    DataInputStream in;                                     //поток для чтения из сокета
    DataOutputStream out;                                   //поток для записи в сокет
    Socket clientSocket = new Socket();                                //слиентский сокет
    String player;                                          //символ игрока
    String opponent;                                        //символ противника
    String x, y;                                            //координаты хода
    Toast toast;                                            //всплывающее сообщение
    boolean isMyTurn;                                       //флаг для синхронизации

    /*
    Конструктор
     */
    public Client(int p, Button[][] b, TextView tv, Toast t) {
        buttons = b;
        PORT = p;
        text = tv;
        toast = t;
    }

    /*
    Обновление игрового поля
     */
    private void resetGameField() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++)
                buttons[i][j].setText("");
        }
    }

    /*
    Возвращает очередь
     */
    public boolean getTurn() {
        return isMyTurn;
    }

    /*
    Передает очередь другому игроку
     */
    public void setTurn(Boolean t) {
        isMyTurn = t;
    }

    /*
    Возвращает символ игрока
     */
    public String getPlayer() {
        return player;
    }

    /*
    Функция записи в сокет
     */
    public void write(String x, String y) {
        try {
            if (clientSocket != null) {
                out.writeUTF(x);
                out.writeUTF(y);
            }
        } catch (java.net.SocketException e) {
            System.out.println("Socket closed");
        } catch (Exception e) {
            System.out.println("Error in Client.write");
            e.printStackTrace();
        }
    }

    public void closeSocket() {
        if (clientSocket != null) {
            try {
                clientSocket.shutdownInput();
                clientSocket.shutdownOutput();
                clientSocket.close();
                System.out.println("client is closed");
            } catch (java.net.SocketException e) {
                System.out.println("java.net.SocketException");
                try {
                    clientSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (Exception e) {
                System.out.println("Error in client.onCancelled()");
                e.printStackTrace();
            }
        }
    }

    /*
    Работа с сокетом
     */
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            InetAddress ipAddress = InetAddress.getByName(IP); // создаем объект который отображает вышеописанный IP-адрес.
            System.out.println("Trying connect to server");
            clientSocket.connect(new InetSocketAddress(IP, PORT), 10000);
            System.out.println("Connected to server");
            toast.setText("Connected to server");
            toast.show();
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            player = in.readUTF();
            if (player.toCharArray()[0] == 'O') {
                isMyTurn = false;
                opponent = "X";
            } else {
                isMyTurn = true;
                opponent = "O";
            }
            publishProgress("You are " + player);
            System.out.println("I'm " + player);
            System.out.println("My opponent is " + opponent);
            while (true) {
                if (isCancelled()) return null;
                x = in.readUTF();
                if (x.length() > 1) {
                    publishProgress(x);
                    if (player.toCharArray()[0] == 'O') {
                        isMyTurn = false;
                    } else {
                        isMyTurn = true;
                    }
                    continue;
                }
                y = in.readUTF();
                System.out.println("Client:" + x);
                System.out.println("Client:" + y);
                publishProgress(x, y);
                isMyTurn = true;
                publishProgress("MASSAGE", "your turn");
            }
        } catch (java.net.ConnectException e) {
            publishProgress("ERROR", "Connection failed.");

        } catch (java.net.SocketTimeoutException e) {
            publishProgress("ERROR", "Timeout.");
        } catch (java.io.EOFException e) {
            System.out.println("Opponent closed application.");
            publishProgress("Opponent closed\n application.");
        } catch (java.net.SocketException e) {
            System.out.println("Server closed.");
            closeSocket();
            setTurn(false);
            publishProgress("ERROR", "Server closed.");
        } catch (Exception e) {
            System.out.println("Error in Client");
            e.printStackTrace();
        }
        return null;
    }

    /*
    Функция, которая имеет доступ к UI
     */
    @Override
    protected void onProgressUpdate(String... strings) {
        super.onProgressUpdate(strings);
        if (strings[0] == "ERROR") {
            toast.setText(strings[1]);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            text.setText(strings[1]);
            return;
        }
        if (strings[0] == "MASSAGE") {
            text.setText(strings[1]);
            return;
        }
        if (strings[0].length() == 1) {
            System.out.println("I'm setting value in " + strings[0] + strings[1]);
            int x, y;
            x = Integer.parseInt(strings[0]);
            y = Integer.parseInt(strings[1]);
            buttons[x][y].setText(opponent);
        } else {
            text.setText(strings[0]);
            resetGameField();
        }
    }

    /*
    Метод выполняющийся после вызова cancel в главном потоке
     */
    @Override
    protected void onCancelled() {
        super.onCancelled();
        closeSocket();
    }
}