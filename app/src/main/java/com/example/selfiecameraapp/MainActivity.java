package com.example.selfiecameraapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Camera camera;
    CameraPreview cameraPreview;
    ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        camera = getCameraInstance();

        cameraPreview = new CameraPreview(this,camera);
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        frameLayout.addView(cameraPreview);

        imageButton = (ImageButton)findViewById(R.id.captureImgButton);

    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Intent i = new Intent(MainActivity.this,EditActivity.class);
            i.putExtra("SMF",data);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    };

    public void capturePic(View view){
        camera.takePicture(null,null,mPictureCallback);
    }

    private static Camera getCameraInstance() {
        Camera cam = null;
        try {
            cam = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cam;
    }

    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        Camera cam;
        SurfaceHolder holder;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            this.cam = camera;
            holder = getHolder();
            holder.addCallback(this);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }


        @Override
        public void surfaceCreated(SurfaceHolder holder) {

            if (cam != null) {
                cam.setDisplayOrientation(90);
                try {
                    cam.setPreviewDisplay(holder);
                    cam.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (holder.getSurface() == null) {
                return;
            }
            cam.stopPreview();
            try {
                cam.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            cam.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (cam != null) {
                cam.startPreview();
                cam.release();
                cam=null;
            }

        }
    }

    {

    }
}
