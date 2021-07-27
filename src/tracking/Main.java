package tracking;

import org.opencv.core.Mat;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.util.Random;

public class Main {

    static String rootDir = "C:/Users/danma/Documents/Tracking Project/FruitDefense/src/tracking/";

    public static void main(String[] args) {

        Camera cam = new Camera(0);

        int frameCheck = 10;

        while(true) {

            cam.CaptureImage();
            Mat firstImg = cam.GetImageMatrix();
            int motionCount = 0;

            for (int i=0; i<frameCheck; i++) {

                cam.CaptureImage();
                boolean motion = cam.MotionDetect(firstImg);
                int[] hands = cam.Detect();
                if(motion && hands.length == 0) {
                    motionCount++;
                }
                if(hands.length > 0) {
                    System.out.println("Hand Detected!");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }

            }
            if(motionCount == frameCheck) {
                try {
                    MotionDetected();
                    System.out.println("Motion Detected!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public static void MotionDetected() throws InterruptedException {

        new Thread(new Runnable() {

            public void run() {
                String sfxPath[] = {"sfx-00.wav", "sfx-01.wav", "sfx-02.wav"};
                Random rand = new Random();
                int randint = rand.nextInt(sfxPath.length);
                String sfxToUse = sfxPath[randint];
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                            Main.class.getResourceAsStream(sfxToUse));

                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();

        Thread.sleep(5000);

    }

}
