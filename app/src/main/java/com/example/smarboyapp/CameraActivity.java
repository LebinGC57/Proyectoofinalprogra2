package com.example.smarboyapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PictureCallback {

    private static final String TAG = "CameraActivity";
    private static final int PERMISSION_REQUEST_CODE = 200;
    
    private SurfaceView surfaceView;
    private ImageView imagePreview;
    private Button btnCapture, btnSave, btnRetake, btnBack;
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private Bitmap capturedImage;
    private boolean isPreviewMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_camera);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupClickListeners();
        
        if (checkCameraPermission()) {
            initializeCamera();
        } else {
            requestCameraPermission();
        }
    }

    private void initializeViews() {
        surfaceView = findViewById(R.id.surfaceView);
        imagePreview = findViewById(R.id.imagePreview);
        btnCapture = findViewById(R.id.btnCapture);
        btnSave = findViewById(R.id.btnSave);
        btnRetake = findViewById(R.id.btnRetake);
        btnBack = findViewById(R.id.btnBack);
        
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void setupClickListeners() {
        btnCapture.setOnClickListener(v -> {
            if (camera != null && isPreviewMode) {
                capturePhoto();
            }
        });

        btnSave.setOnClickListener(v -> {
            if (capturedImage != null) {
                savePhoto();
            } else {
                Toast.makeText(this, "No hay foto para guardar", Toast.LENGTH_SHORT).show();
            }
        });

        btnRetake.setOnClickListener(v -> {
            retakePhoto();
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }

    private void initializeCamera() {
        try {
            camera = Camera.open();
            if (camera != null) {
                camera.setDisplayOrientation(90);
                Log.d(TAG, "Cámara inicializada correctamente");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al inicializar la cámara: " + e.getMessage());
            Toast.makeText(this, "Error al abrir la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    private void capturePhoto() {
        if (camera != null) {
            try {
                camera.takePicture(null, null, this);
                Toast.makeText(this, "Capturando foto...", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Error al capturar foto: " + e.getMessage());
                Toast.makeText(this, "Error al capturar foto", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void savePhoto() {
        if (capturedImage != null) {
            try {
                // Crear directorio si no existe
                File imagesDir = new File(getExternalFilesDir(null), "ChefConsciente");
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs();
                }

                // Generar nombre único para la imagen
                String fileName = "ingredientes_" + System.currentTimeMillis() + ".jpg";
                File imageFile = new File(imagesDir, fileName);

                // Guardar imagen
                FileOutputStream fos = new FileOutputStream(imageFile);
                capturedImage.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();

                // Retornar resultado a ChefConscienteActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("image_path", imageFile.getAbsolutePath());
                setResult(RESULT_OK, resultIntent);
                
                Toast.makeText(this, "Foto guardada exitosamente", Toast.LENGTH_SHORT).show();
                finish();

            } catch (IOException e) {
                Log.e(TAG, "Error al guardar foto: " + e.getMessage());
                Toast.makeText(this, "Error al guardar foto", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void retakePhoto() {
        // Volver al modo preview
        isPreviewMode = true;
        imagePreview.setVisibility(View.GONE);
        surfaceView.setVisibility(View.VISIBLE);
        
        btnCapture.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.GONE);
        btnRetake.setVisibility(View.GONE);
        
        // Reiniciar preview de cámara
        if (camera != null) {
            try {
                camera.startPreview();
            } catch (Exception e) {
                Log.e(TAG, "Error al reiniciar preview: " + e.getMessage());
            }
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        try {
            // Convertir bytes a Bitmap
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2; // Reducir tamaño para mejor rendimiento
            capturedImage = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            
            // Rotar imagen si es necesario
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            capturedImage = Bitmap.createBitmap(capturedImage, 0, 0, 
                capturedImage.getWidth(), capturedImage.getHeight(), matrix, true);
            
            // Mostrar preview de la imagen capturada
            imagePreview.setImageBitmap(capturedImage);
            imagePreview.setVisibility(View.VISIBLE);
            surfaceView.setVisibility(View.GONE);
            
            // Cambiar botones
            isPreviewMode = false;
            btnCapture.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
            btnRetake.setVisibility(View.VISIBLE);
            
            Toast.makeText(this, "Foto capturada exitosamente", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            Log.e(TAG, "Error al procesar foto: " + e.getMessage());
            Toast.makeText(this, "Error al procesar foto", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (camera != null) {
            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
                Log.d(TAG, "Preview de cámara iniciado");
            } catch (IOException e) {
                Log.e(TAG, "Error al configurar preview: " + e.getMessage());
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (camera != null) {
            try {
                camera.stopPreview();
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (Exception e) {
                Log.e(TAG, "Error al cambiar preview: " + e.getMessage());
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeCamera();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (camera == null && checkCameraPermission()) {
            initializeCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
}
