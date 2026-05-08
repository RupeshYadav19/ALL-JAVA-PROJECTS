package com.locker.util;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import java.util.Arrays;

public class FaceRecognitionHelper {
    private CascadeClassifier faceCascade;

    public FaceRecognitionHelper() {
        // Load native library
        nu.pattern.OpenCV.loadShared();
        faceCascade = new CascadeClassifier("resources/haarcascade_frontalface_alt.xml");
        if (faceCascade.empty()) {
            System.err.println("Error: Could not load face cascade.");
        }
    }

    public Mat detectFace(Mat frame) {
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(grayFrame, grayFrame);

        faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0, new Size(30, 30), new Size());

        Rect[] facesArray = faces.toArray();
        if (facesArray.length > 0) {
            // Return the largest face
            Rect largestFace = facesArray[0];
            for (Rect rect : facesArray) {
                if (rect.width * rect.height > largestFace.width * largestFace.height) {
                    largestFace = rect;
                }
            }
            return new Mat(frame, largestFace);
        }
        return null;
    }

    public byte[] matToBytes(Mat mat) {
        MatOfByte buf = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, buf);
        return buf.toArray();
    }

    public Mat bytesToMat(byte[] bytes) {
        return Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_UNCHANGED);
    }

    public double compareFaces(Mat face1, Mat face2) {
        if (face1 == null || face2 == null)
            return 0.0;

        Mat gray1 = new Mat();
        Mat gray2 = new Mat();
        Imgproc.cvtColor(face1, gray1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(face2, gray2, Imgproc.COLOR_BGR2GRAY);

        // Normalize lighting
        Imgproc.equalizeHist(gray1, gray1);
        Imgproc.equalizeHist(gray2, gray2);

        // Resize to higher resolution for better detail matching
        Size size = new Size(160, 160);
        Imgproc.resize(gray1, gray1, size);
        Imgproc.resize(gray2, gray2, size);

        // Simple similarity check using Template Matching
        Mat res = new Mat();
        Imgproc.matchTemplate(gray1, gray2, res, Imgproc.TM_CCOEFF_NORMED);
        return res.get(0, 0)[0]; // 1.0 is perfect match
    }
}
