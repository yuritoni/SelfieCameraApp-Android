package com.example.selfiecameraapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {

    int MAx_NUMBER_OF_FACES = 10;
    float GLASSES_SCALE_CONST = 2.5f;
    float HAT_SCALE_CONST = 1.5f;
    float HAT_OFFSET = 2.5f;
    float TIE_SCALE_CONST = 1f;
    float TIE_OFFSET = 2.2f;

    int NUMBER_OF_DETECTED_FACES;
    Bitmap bitmap;

    FaceDetector.Face[] detectedFaces;
    ImageView imageView;

    ArrayList<StickerPointF> stickerPointFArrayList;
    byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        stickerPointFArrayList = new ArrayList<StickerPointF>();
        boolean isRecevied = getIntentData();
        if (isRecevied) {
            //konver menjadi bitmap
            Bitmap bitmap = convertToBitmap();

            //merotasi bitmap
            Bitmap rotatedBitmap = rotateBitmap(bitmap);

            //deteksi wajah

            int width = rotatedBitmap.getWidth();
            int height = rotatedBitmap.getHeight();

            detectedFaces = new FaceDetector.Face[MAx_NUMBER_OF_FACES];
            FaceDetector faceDetector = new FaceDetector(width, height, MAx_NUMBER_OF_FACES);
            NUMBER_OF_DETECTED_FACES = faceDetector.findFaces(rotatedBitmap, detectedFaces);

            decorateFaceOnBitmap(rotatedBitmap);
        }
    }

    private void decorateFaceOnBitmap(Bitmap rotatedBitmap) {
        Canvas canvas = new Canvas(rotatedBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

        for (int i = 0; i < NUMBER_OF_DETECTED_FACES; i++) {
            FaceDetector.Face face = detectedFaces[i];

            PointF midPoint = new PointF();
            face.getMidPoint(midPoint);

            Bitmap glasses_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.desain_ballon);
            glasses_bitmap = setObjectToFace(face, glasses_bitmap, GLASSES_SCALE_CONST);
            canvas.drawBitmap(glasses_bitmap, midPoint.x - glasses_bitmap.getWidth() / 2, midPoint.y - glasses_bitmap.getHeight() / 2, paint);

            Bitmap hatBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.desaing_music);
            hatBitmap=setObjectToFace(face,hatBitmap,HAT_SCALE_CONST);
            float hatTop=midPoint.y-HAT_OFFSET*face.eyesDistance();
            canvas.drawBitmap(hatBitmap,midPoint.x-hatBitmap.getWidth()/2,hatTop-hatBitmap.getHeight()/2,paint);


        }


    }

    private Bitmap setObjectToFace(FaceDetector.Face face, Bitmap glasses_bitmap, float glasses_scale_const) {

        float newWidth = face.eyesDistance() * glasses_scale_const;
        float scaleFactor = newWidth / glasses_bitmap.getWidth();
        return Bitmap.createScaledBitmap(glasses_bitmap, Math.round(newWidth), Math.round(glasses_bitmap.getHeight() * scaleFactor), false);
    }

    private Bitmap rotateBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return rotatedBitmap;
    }

    private Bitmap convertToBitmap() {
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inPreferredConfig = Bitmap.Config.RGB_565;
        bfo.inDither = false;
        bfo.inScaled = false;

        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, bfo);
        if (bm != null) {
            return bm;
        }
        return null;

    }

    private boolean getIntentData() {
        if (getIntent() != null) {
            bytes = getIntent().getByteArrayExtra("EMP");
            return true;
        }
        return false;
    }

    public class StickerPointF {
        Bitmap stickers;
        float x, y;

        StickerPointF(Bitmap stickers, float x, float y) {
            this.x = x;
            this.y = y;
            this.stickers = stickers;
        }

    }
}
