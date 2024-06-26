package tamanini.ferreira.galeria.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tamanini.ferreira.galeria.R;
import tamanini.ferreira.galeria.model.MyAdapter;
import tamanini.ferreira.galeria.util.Util;

public class MainActivity extends AppCompatActivity {

    List<String> photos = new ArrayList<>();

    MyAdapter myAdapter;

    static int RESULT_REQUEST_PERMISSION = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rvGallery), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //acessam o diretório “Pictures”
        //(Enviroment.PICTURES)
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // leem a lista de fotos já salvas
        File[] files = dir.listFiles();
        //adicionam na lista de fotos
        for(int i = 0; i < files.length; i++) {
            photos.add(files[i].getAbsolutePath());
        }

        // é criado o MainAdapter e ele é setado no RecycleView
        myAdapter = new MyAdapter(MainActivity.this, photos);

        RecyclerView rvGallery = findViewById(R.id.rvGallery);
        rvGallery.setAdapter(myAdapter);
        //calculam quantas colunas de fotos cabem na tela do celular
        float w = getResources().getDimension(R.dimen.itemWidth);
        int numberOfColumns = Util.calculateNoOfColumns(MainActivity.this, w);
        // configuram o RecycleView para exibir as fotos em GRID
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, numberOfColumns);
        rvGallery.setLayoutManager(gridLayoutManager);

        //obtem o elemento tbMain
        Toolbar toolbar = findViewById(R.id.tbMain);
        // indica para MainActivity que tbMain deve ser considerado como a ActionBar padrão da tela
        setSupportActionBar(toolbar);

        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        checkForPermissions(permissions);

    }
    static int RESULT_TAKE_PICTURE = 1;
    String currentPhotoPath;
    private void dispatchTakePictureIntent() {
        //criado um arquivo vazio dentro da pasta Pictures
        File f = null;
        try {
            f = createImageFile();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Não foi possível criar o arquivo", Toast.LENGTH_LONG).show();
            return;
        }
        //o local do mesmo é salvo no atributo de classe
        //currentPhotoPath
        currentPhotoPath = f.getAbsolutePath();

        if (f != null) {
            // gerado um endereço URI para o arquivo de foto
            Uri fUri = FileProvider.getUriForFile(MainActivity.this, "tamanini.ferreira.galeria.fileprovider", f);
            //Um Intent para disparar a app de câmera é criado
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //URI é passado para a app de câmera via Intent
            i.putExtra(MediaStore.EXTRA_OUTPUT, fUri);
            // a app de câmera é
            //efetivamente iniciada e a nossa app fica a espera do resultado, no caso a
            //foto
            startActivityForResult(i, RESULT_TAKE_PICTURE);
        }
        }
    //A criação em si do arquivo que vai guardar a imagem é feita pelo método
    //createImageFile
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File f = File.createTempFile(imageFileName, ".jpg", storageDir);
        return f;
        }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_TAKE_PICTURE) {
            if(resultCode == Activity.RESULT_OK) {
                photos.add(currentPhotoPath);
                myAdapter.notifyItemInserted(photos.size()-1);}
            else {
                File f = new File(currentPhotoPath);
                f.delete();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //cria um “inflador de menu”
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_tb, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.opCamera) {
            //será executado código que dispara a câmera do celular
            dispatchTakePictureIntent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //Esse método é chamado dentro do
    //método onBindViewHolder (MainAdapter) quando o usuário clica em uma foto
    public void startPhotoActivity(String photoPath) {
        Intent i = new Intent(MainActivity.this, PhotoActivity.class);
        i.putExtra("photo_path", photoPath);
        startActivity(i);
    }
    //aceita como entrada uma lista de permissões.
    private void checkForPermissions(List<String> permissions) {
        List<String> permissionsNotGranted = new ArrayList<>();

        for(String permission : permissions) {
            if( !hasPermission(permission)) {
                permissionsNotGranted.add(permission);
            }
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(permissionsNotGranted.size() > 0) {
                requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]),RESULT_REQUEST_PERMISSION);
            }
        }
    }
    //verifica se uma determinada permissão já foi concedida
    //ou não.
    private boolean hasPermission(String permission) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ActivityCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_GRANTED;
            }
        return false;
    }

    //esse método é chamado após o usuário
    //conceder ou não as permissões requisitadas
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        final List<String> permissionsRejected = new ArrayList<>();
        if(requestCode == RESULT_REQUEST_PERMISSION) {
            for(String permission : permissions) {
                if(!hasPermission(permission)) {
                    permissionsRejected.add(permission);
                }
            }
        }

        if(permissionsRejected.size() > 0) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                    new AlertDialog.Builder(MainActivity.this).setMessage("Para usar essa app é preciso conceder essas permissões").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), RESULT_REQUEST_PERMISSION);
                        }
                    }).create().show();
                }
            }
        }
    }
}
    




