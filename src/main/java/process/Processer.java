package process;


public class Processer {
    public static void processUnit(String sensorName, int[] sensorCoordinate, int[] targetCoordinate) {
        if (sensorCoordinate != null && targetCoordinate != null) {
            int difX = targetCoordinate[0] - sensorCoordinate[0];//-500 - -500=0
            int difY = targetCoordinate[1] - sensorCoordinate[1];
            if (difX == 0 && difY == 0) {

                System.out.println("Hedef ve sensor aynı noktadadır.");

            } else {
                double angle =(Math.atan2(difX, difY) * (180 / Math.PI));
                if (angle <= 0) {
                    angle += 360;
                }

                System.out.println(sensorName + " için hedefin kerterizi Y Pozitif ekseninde saat yönündeki açısı " + angle + " derecedir.");
            }
        } else {
            System.out.println("Koordinat verileri eksik veya hatalı.");
        }
    }

}
