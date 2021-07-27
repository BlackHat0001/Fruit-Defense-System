package tracking;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

public class Camera {

    private VideoCapture capture;
    private Mat matrix;
    private Mat firstImg;
    private Mat currentImg;
    private Mat frameDelta;
    private Mat thresh;
    List<MatOfPoint> cnts = new ArrayList<MatOfPoint>();

    public Camera(int camID) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        this.capture = new VideoCapture(camID);
        this.matrix = new Mat();
        this.frameDelta = new Mat();
        this.firstImg = new Mat();
        this.currentImg = new Mat();
        this.thresh = new Mat();
    }

    public void CaptureImage() {
        Mat mat = new Mat();
        capture.read(mat);
        this.matrix = mat;
    }

    public Mat GetImageMatrix() { return matrix; }

    public BufferedImage GetImage() {
        return toBufferedImage(matrix);
    }

    public int[] Detect() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        MatOfRect faces = new MatOfRect();

        String xml = "C:\\Users\\danma\\Documents\\Tracking Project\\FruitDefense\\src\\tracking\\hand.xml";
        CascadeClassifier cascade = new CascadeClassifier(xml);

        cascade.detectMultiScale(matrix, faces);

        if(!faces.empty()) {
            int[] pos = new int[]{faces.toArray()[0].x, faces.toArray()[0].y, faces.toArray()[0].width, faces.toArray()[0].height};
            return pos;
        }

        return new int[] {};

    }

    public boolean MotionDetect(Mat prevImg) {

        Imgproc.cvtColor(prevImg, firstImg, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(firstImg, firstImg, new Size(21, 21), 0);

        Imgproc.cvtColor(matrix, currentImg, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(currentImg, currentImg, new Size(21, 21), 0);

        Core.absdiff(firstImg, currentImg, frameDelta);
        Imgproc.threshold(frameDelta, thresh, 25, 255, Imgproc.THRESH_BINARY);

        Imgproc.dilate(thresh, thresh, new Mat(), new Point(-1, -1), 2);
        Imgproc.findContours(thresh, cnts, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for(int i=0; i < cnts.size(); i++) {
            if(Imgproc.contourArea(cnts.get(i)) < 500) {
                continue;
            }

            return true;
        }

        return false;

    }

    private BufferedImage toBufferedImage(Mat m) {
        if (!m.empty()) {
            int type = BufferedImage.TYPE_BYTE_GRAY;
            if (m.channels() > 1) {
                type = BufferedImage.TYPE_3BYTE_BGR;
            }
            int bufferSize = m.channels() * m.cols() * m.rows();
            byte[] b = new byte[bufferSize];
            m.get(0, 0, b); // get all the pixels
            BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
            final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            System.arraycopy(b, 0, targetPixels, 0, b.length);
            return image;
        }

        return null;
    }

}
