package target;

import java.io.*;
import java.net.Socket;
import java.util.Random;

public class Target {
    private int[] coordinates;

    public int[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(int[] coordinates) {
        this.coordinates = coordinates;
    }

    /*Target() {
            Random random = new Random();
            coordinates =
                    new int[]
                            {
                                    -500 + random.nextInt(1000),
                                    -500 + random.nextInt(1000)
                            };
        }
    */
    Target() {
        coordinates = new int[]{-501, 501};
    }



    public void sendCoordinates(int port) {
        boolean isConnected = false;
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        var temp = getCoordinates().clone();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
                    if(temp[0]==501){
                        temp[0]=-500;
                    }
                    if(temp[1]==-501){
                        temp[1]=500;
                    }
                    oos.writeObject(temp);
                    oos.flush();
                    oos.reset();
                    temp[0]++;
                    temp[1]--;
                    System.out.println("Sensor:" + (String) ois.readObject());
                    Thread.sleep(125);
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
            target.sendCoordinates(5432);
        }).start();
        new Thread(() -> {
            target.sendCoordinates(5433);
        }).start();
    }
}
