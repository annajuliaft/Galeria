package tamanini.ferreira.galeria.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import tamanini.ferreira.galeria.R;
import tamanini.ferreira.galeria.util.Util;

public class PhotoActivity extends AppCompatActivity {

    String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_photo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ConstraintLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent i = getIntent();
        photoPath = i.getStringExtra("photo_path");

        Bitmap bitmap = Util.getBitmap(photoPath);
        ImageView imPhoto = findViewById(R.id.imPhoto);
        imPhoto.setImageBitmap(bitmap);

        Toolbar toolbar = findViewById(R.id.tbPhoto);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_activity_tb, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected() {
        return onOptionsItemSelected(null);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.opShare:
                sharePhoto();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}