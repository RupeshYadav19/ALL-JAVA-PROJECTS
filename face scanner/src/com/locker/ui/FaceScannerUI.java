package com.locker.ui;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.locker.util.FaceRecognitionHelper;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

public class FaceScannerUI extends JDialog {
    private Webcam webcam;
    private WebcamPanel panel;
    private FaceRecognitionHelper faceHelper;
    private Mat capturedFace = null;
    private AtomicBoolean isClosed = new AtomicBoolean(false);

    public FaceScannerUI(Frame owner, FaceRecognitionHelper helper) {
        super(owner, "Face Scanner", true);
        this.faceHelper = helper;
        setLayout(new BorderLayout());

        webcam = Webcam.getDefault();
        if (webcam != null) {
            if (!webcam.isOpen()) {
                webcam.setViewSize(new Dimension(640, 480));
            }
            panel = new WebcamPanel(webcam);
            panel.setFPSDisplayed(true);
            panel.setFillArea(true);
            add(panel, BorderLayout.CENTER);
        } else {
            add(new JLabel("No webcam detected!", JLabel.CENTER), BorderLayout.CENTER);
        }

        JButton captureBtn = new JButton("Capture Face");
        captureBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        captureBtn.setBackground(new Color(66, 133, 244));
        captureBtn.setForeground(Color.WHITE);
        captureBtn.addActionListener(e -> {
            if (webcam != null) {
                BufferedImage image = webcam.getImage();
                Mat frame = bufferedImageToMat(image);
                capturedFace = faceHelper.detectFace(frame);
                if (capturedFace != null) {
                    JOptionPane.showMessageDialog(this, "Face Captured Successfully!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "No face detected. Please try again.");
                }
            }
        });
        add(captureBtn, BorderLayout.SOUTH);

        setSize(700, 600);
        setLocationRelativeTo(owner);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isClosed.set(true);
                if (webcam != null && webcam.isOpen()) {
                    webcam.close();
                }
            }
        });
    }

    @Override
    public void dispose() {
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }
        super.dispose();
    }

    public Mat getCapturedFace() {
        return capturedFace;
    }

    private Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), org.opencv.core.CvType.CV_8UC3);
        byte[] data = ((java.awt.image.DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }
}
