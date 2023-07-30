package sensor;

import process.Processer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Sensor implements Serializable {
    int[] coordinates;
    String sensorName;

    Sensor(String sensorName) {
        Random random = new Random();
        coordinates =
                new int[]
                        {
                                -500 + random.nextInt(1000),
                                -500 + random.nextInt(1000)
                        };
        this.sensorName = sensorName;
    }


    public void createServer(int port) {

        try (ServerSocket server = new ServerSocket(port)) {
            server.setReuseAddress(true);
            while (true) {
                Socket target = server.accept();
                target.setKeepAlive(true);
                System.out.println("Target Connected" + target.getInetAddress().getHostAddress());
                TargetHandler targetHandler = new TargetHandler(target, this);
                new Thread(targetHandler).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private record TargetHandler(Socket socket, Sensor sensor) implements Runnable {

        @Override
        public void run() {
            try (
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())
            ) {
                while (true) {
                    int[] receivedCoordinatesFromTarget = (int[]) ois.readObject();
                    System.out.printf
                            ("Sensor coordinates:(%d,%d) | Target coordinates:(%d,%d)%n",
                                    sensor.coordinates[0],
                                    sensor.coordinates[1],
                                    receivedCoordinatesFromTarget[0],
                                    receivedCoordinatesFromTarget[1]);
                    String message = "Coordinates was taken successfully by " + sensor.sensorName;
                    Processer.processUnit(sensor.sensorName, sensor.coordinates, receivedCoordinatesFromTarget);
                    oos.writeObject(message);
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        Sensor sensor1 = new Sensor("Sensor-1");
        new Thread(() -> {
            sensor1.createServer(8080);
        }).start();
        Sensor sensor2 = new Sensor("Sensor-2");
        new Thread(() -> {
            sensor2.createServer(8081);
        }).start();

    }
}
