package com.example.courseproject2;

import android.os.AsyncTask;
import android.widget.Button;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Семен on 5/8/2017.
 */

public class Client extends AsyncTask<Void, Integer, Void> {

    String IP = "10.0.2.2";
    int PORT;
    Button[][] buttons;
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    String player;
    String opponent;
    int x,y;

    public Client(int p, Button[][] b)
    {
        buttons = b;
        PORT = p;
    }

    public String getPlayer()
    {
        return player;
    }

    public void write(String x, String y)
    {
        try {
            out.writeUTF(x);
            out.writeUTF(y);
        }
        catch(Exception e)
        {
            System.out.println("Error in Client.write");
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            InetAddress ipAddress = InetAddress.getByName(IP); // создаем объект который отображает вышеописанный IP-адрес.
            clientSocket = new Socket(ipAddress, PORT); // создаем сокет используя IP-адрес и порт сервера.
            System.out.println("Connected to server");
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            player = in.readUTF();
            opponent = player == "O"?"X" :"O";
            System.out.println("I'm " + player);
            System.out.println("My opponent is " + opponent);
           /* if(player == "O")
            {
                x = Integer.parseInt(in.readUTF());
                y = Integer.parseInt(in.readUTF());
                publishProgress(x,y);
            }*/
            while(true)
            {
                x = Integer.parseInt(in.readUTF());
                y = Integer.parseInt(in.readUTF());
                System.out.println("Client:" + x);
                System.out.println("Client:" + y);
                publishProgress(x,y);
            }
        } catch (Exception e) {
            System.out.println("Error in Client");
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values)
    {
        super.onProgressUpdate(values);
        System.out.println("I'm setting value in " + values[0] + values[1]);
        buttons[values[0]][values[1]].setText(opponent);
    }
}
