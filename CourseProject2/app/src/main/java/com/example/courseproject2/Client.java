package com.example.courseproject2;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends AsyncTask<Void, String, Void> {

    String IP = "10.0.2.2";
    int PORT;
    Button[][] buttons;
    TextView text;
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    String player;
    String opponent;
    String x, y;
    Toast toast;

    public Client(int p, Button[][] b, TextView tv, Toast t) {
        buttons = b;
        PORT = p;
        text = tv;
        toast = t;
    }

    private void resetGameField() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++)
                buttons[i][j].setText("");
        }
    }

    public String getPlayer() {
        return player;
    }

    public void write(String x, String y) {
        try {
            if (clientSocket != null) {
                out.writeUTF(x);
                out.writeUTF(y);
            }
        } catch (Exception e) {
            System.out.println("Error in Client.write");
            e.printStackTrace();
        }
    }

    public void close() {
        if(clientSocket != null) {
            try {
                clientSocket.shutdownInput();
                clientSocket.shutdownOutput();
                clientSocket.close();
                System.out.println("client is closed");
            } catch (Exception e) {
                System.out.println("Error in client.close()");
                e.printStackTrace();
            }
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            InetAddress ipAddress = InetAddress.getByName(IP); // создаем объект который отображает вышеописанный IP-адрес.
            System.out.println("Trying connect to server");
            clientSocket = new Socket(ipAddress, PORT); // создаем сокет используя IP-адрес и порт сервера.
            System.out.println("Connected to server");
            toast.setText("Connected to server");
            toast.show();
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            player = in.readUTF();
            if (player.toCharArray()[0] == 'O') opponent = "X";
            else opponent = "O";
            publishProgress("You are " + player);
            System.out.println("I'm " + player);
            System.out.println("My opponent is " + opponent);
            while (true) {
                x = in.readUTF();
                if (x.length() > 1) {
                    publishProgress(x);
                    continue;
                }
                y = in.readUTF();
                System.out.println("Client:" + x);
                System.out.println("Client:" + y);
                publishProgress(x, y);
            }
        } catch (java.net.ConnectException e) {
            publishProgress("ERROR", "failed to connect to server.");
        }catch(java.io.EOFException e)
        {
            System.out.println("End of game.");
            publishProgress("Opponent closed\n application.");
        }
        catch (Exception e) {
            System.out.println("Error in Client");
            e.printStackTrace();
        }
        return null;
    }

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
        if (strings.length == 2) {
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
}
