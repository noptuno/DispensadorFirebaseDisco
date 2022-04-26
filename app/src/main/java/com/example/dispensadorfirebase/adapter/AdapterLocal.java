package com.example.dispensadorfirebase.adapter;

import android.content.Context;

import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.clase.Local;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdapterLocal extends RecyclerView.Adapter<AdapterLocal.NoteViewHolder> {

    private List<Local> notes;
    private OnNoteSelectedListener onNoteSelectedListener;
    private OnNoteDetailListener onDetailListener;
    private Context context;
    public AdapterLocal() {
        this.notes = new ArrayList<>();
    }

    public AdapterLocal(List<Local> notes) {
        this.notes = notes;
    }


    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View elementoTitular = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note_locales, parent, false);

        context = elementoTitular.getContext();
        return new NoteViewHolder(elementoTitular);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder view, int pos) {
        view.bind(notes.get(pos));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public List<Local> getNotes() {
        return notes;
    }

    public void setNotes(List<Local> notes) {
        this.notes = notes;
    }

    public void setFilter(List<Local> notes){
        this.notes.addAll(notes);
        notifyDataSetChanged();
    }




    public void setOnNoteSelectedListener(OnNoteSelectedListener onNoteSelectedListener) {
        this.onNoteSelectedListener = onNoteSelectedListener;
    }

    public void setOnDetailListener(OnNoteDetailListener onDetailListener) {
        this.onDetailListener = onDetailListener;
    }


    public interface OnNoteSelectedListener {
        void onClick(Local note);
    }

    public interface OnNoteDetailListener {
        void onDetail(Local note);
    }





    public Local getposicionactual(int position) {
        return notes.get(position);
    }


 

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView nombre;
        private TextView numero;
        private CheckBox checkBox;
        private TextView estado;
        private ImageView imglogolocal;
private LinearLayout layout;

        public NoteViewHolder(View item) {
            super(item);

            nombre = (TextView) item.findViewById(R.id.txtnombresector);
            numero = (TextView) item.findViewById(R.id.txtnumero);
            checkBox = (CheckBox) item.findViewById(R.id.checkBox);
            estado = (TextView) item.findViewById(R.id.txtEstado);
            imglogolocal =item.findViewById(R.id.imglogolocal);

            layout = item.findViewById(R.id.layoutlocal);
        //falta color

        }

        public void bind(final Local local) {

            nombre.setText(local.getNombreLocal());
            numero.setText("" +local.getNumeroLocal());
            estado.setText(local.getEstadoLocal());

            Uri fondo = Uri.parse(local.getLogo());
            CargarImagen(fondo,imglogolocal);

            Random random = new Random();

            int color = Color.argb(255,random.nextInt(256),(random.nextInt(256)),(random.nextInt(256)));
            layout.setBackgroundColor(color);





            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onNoteSelectedListener != null) {
                        onNoteSelectedListener.onClick(local);
                    }
                }
            });
        }
    }

    private void CargarImagen(Uri fondo, ImageView img){

        Glide.with(context).load(fondo).into(img);




    }


}
