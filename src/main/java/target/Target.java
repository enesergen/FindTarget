package target;

import java.io.*;
import java.net.Socket;
import java.util.Random;

public class Target {
    int[] coordinates;

    Target() {
        Random random = new Random();
        coordinates =
                new int[]
                        {
                                -500 + random.nextInt(1000),
                                -500 + random.nextInt(1000)
                        };
    }


    public void sendCoordinates(int port) {
        boolean isConnected = false;
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        while (!isConnected) {
            try {
                socket = new Socket("localhost", port);
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());
                isConnected = true;
                System.out.println("Connection is provided for port:" + port);
            } catch (IOException e) {
                System.out.println("Try to connect sensor");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        while (true) {
            try {
                if (isConnected) {
                    oos.writeObject(coordinates);
                    System.out.println("Sensor:" + (String) ois.readObject());
                    Thread.sleep(1000);
                } else {
                    socket = new Socket("localhost", port);
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    ois = new ObjectInputStream(socket.getInputStream());
                    isConnected = true;
                    Thread.sleep(1000);
                }
            } catch (IOException | ClassNotFoundException e) {
                isConnected = false;
                System.out.println("Connection failed. It will try to provide connection");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


    }

    public static void main(String[] args) {
        Target target = new Target();
        new Thread(() -> {
            target.sendCoordinates(8080);
        }).start();
        new Thread(() -> {
            target.sendCoordinates(8081);
        }).start();
    }
}
