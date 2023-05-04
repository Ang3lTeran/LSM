package com.example.opencv;

import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;





public class MainActivity extends AppCompatActivity implements CvCameraViewListener2 {

         CameraBridgeViewBase mOpenCvCameraView;
         Mat mRgba;

        Button mCaptureButton;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            mOpenCvCameraView = findViewById(R.id.camera_surface_view);
            mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
            mOpenCvCameraView.setCvCameraViewListener(this);

            mCaptureButton = findViewById(R.id.capture_button);
            mCaptureButton.setOnClickListener(v -> captureImage());
        }

        @Override
        public void onResume() {
            super.onResume();
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        }

        private final BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                if (status == LoaderCallbackInterface.SUCCESS) {
                    mOpenCvCameraView.enableView();
                } else {
                    super.onManagerConnected(status);
                }
            }
        };

        @Override
        public void onPause() {
            super.onPause();
            if (mOpenCvCameraView != null)
                mOpenCvCameraView.disableView();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (mOpenCvCameraView != null)
                mOpenCvCameraView.disableView();
        }

        @Override
        public void onCameraViewStarted(int width, int height) {
            mRgba = new Mat();
        }

        @Override
        public void onCameraViewStopped() {
            mRgba.release();
        }

        @Override
        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
            mRgba = inputFrame.rgba();
            return mRgba;
        }

        private void captureImage() {
            if (mRgba != null) {
                String filename = "image_" + System.currentTimeMillis() + ".png";
                Imgcodecs.imwrite(filename, mRgba);

                // check if dataset has 20 images, if yes, save the images in a separate folder
                if (checkDatasetCount()) {
                    // save images in separate folder
                    String folderName = "dataset_folder";
                    File folder = new File(Environment.getExternalStorageDirectory() + "/my_folder");
                    if (!folder.exists()) {

                    }


                    File imageFile = new File(filename);
                    String destPath = folder.getAbsolutePath() + "/" + imageFile.getName();
                    imageFile.renameTo(new File(destPath));
                }
            }
        }

        private boolean checkDatasetCount() {
            String folderName = "dataset_folder";
            File folder = new File(Environment.getExternalStorageDirectory() + "/" + folderName);
            if (!folder.exists()) {
                folder.mkdirs();
                return true;
            }

            File[] files = folder.listFiles();
            return files == null || files.length < 20;
        }
    }