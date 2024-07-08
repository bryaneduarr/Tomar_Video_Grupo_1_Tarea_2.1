package com.example.tomar_video_grupo_1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
  int REQUEST_CODE_VIDEO_CAPTURE = 2607;
  int PERMISSION_REQUEST_CODE = 1001;

  private VideoView videoView;
  private MediaController mediaController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_main);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });
    videoView = findViewById(R.id.videoView);
    mediaController = new MediaController(this);
    videoView.setMediaController(mediaController);

    findViewById(R.id.tomarVideOButton).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (revisarPermisos()) {
          grabarVideo();
        }
      }
    });
  }

  private boolean revisarPermisos() {
    int cameraPermiso = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
    int audioPermiso = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO);
    int storagePermiso = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

    if (cameraPermiso != PackageManager.PERMISSION_GRANTED || audioPermiso != PackageManager.PERMISSION_GRANTED || storagePermiso != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

      return false;
    }

    return true;
  }

  public void grabarVideo() {
    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

    if (intent.resolveActivity(getPackageManager()) != null) {
      startActivityForResult(intent, REQUEST_CODE_VIDEO_CAPTURE);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == PERMISSION_REQUEST_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        grabarVideo();
      } else {
        Toast.makeText(this, "Permisos denegados.", Toast.LENGTH_SHORT).show();
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == REQUEST_CODE_VIDEO_CAPTURE && resultCode == RESULT_OK) {
      Uri videoUri = data.getData();

      videoView.setVideoURI(videoUri);

      videoView.start();
    }
  }
}