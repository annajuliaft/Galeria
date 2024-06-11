package tamanini.ferreira.galeria.model;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tamanini.ferreira.galeria.R;
import tamanini.ferreira.galeria.activity.MainActivity;
import tamanini.ferreira.galeria.util.Util;

public class MyAdapter extends RecyclerView.Adapter {

    MainActivity mainActivity;
    List<String> photos;

    public MyAdapter(MainActivity mainActivity, List<String> photos) {
        this.mainActivity = mainActivity;
        this.photos = photos;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mainActivity);
        View v = inflater.inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    @Override
    //A função preenche o ImageView com a foto correspondente.
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ImageView imPhoto = holder.itemView.findViewById(R.id.imItem);
        //obtidos as dimensões que a
        //imagem vai ter na lista
        int w = (int)
                mainActivity.getResources().getDimension(R.dimen.itemWidth);
        int h = (int)
                mainActivity.getResources().getDimension(R.dimen.itemHeight);
        //carrega a imagem em um Bitmap
        Bitmap bitmap = Util.getBitmap(photos.get(position), w, h);
        //o Bitmap é setado no ImageView
        imPhoto.setImageBitmap(bitmap);
        //é definido o que acontece quando o usuário clica em cima de uma imagem
        imPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.startPhotoActivity(photos.get(position));
            }
        });
    }
}
