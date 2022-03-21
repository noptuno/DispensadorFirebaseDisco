package com.example.dispensadorfirebase.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.clase.SectorLocal;

import java.util.ArrayList;
import java.util.List;

public class AdapterDisplayGrande extends RecyclerView.Adapter<AdapterDisplayGrande.NoteViewHolder> {

    private List<SectorLocal> notes;
    private OnNoteSelectedListener onNoteSelectedListener;
    private OnNoteDetailListener onDetailListener;
    private int CantidadSectores;

    public AdapterDisplayGrande(int cantidad) {
        this.notes = new ArrayList<>();
        this.CantidadSectores = cantidad;
    }

    public AdapterDisplayGrande(List<SectorLocal> notes) {
        this.notes = notes;
    }


    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View elementoTitular;

        if (CantidadSectores ==1){
            elementoTitular = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_note_sectores_21_uno, parent, false);

        }else if (CantidadSectores ==2){
            elementoTitular = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_note_sectores_21_dos, parent, false);
        }else{

            elementoTitular = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_note_sectores_21_tres, parent, false);
        }


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

    public List<SectorLocal> getNotes() {
        return notes;
    }

    public void setNotes(List<SectorLocal> notes) {
        this.notes = notes;
    }

    public void setOnNoteSelectedListener(OnNoteSelectedListener onNoteSelectedListener) {
        this.onNoteSelectedListener = onNoteSelectedListener;
    }

    public void setOnDetailListener(OnNoteDetailListener onDetailListener) {
        this.onDetailListener = onDetailListener;
    }



    public interface OnNoteSelectedListener {
        void onClick(SectorLocal note);
    }

    public interface OnNoteDetailListener {
        void onDetail(SectorLocal note);
    }



    public SectorLocal getposicionactual(int position) {
        return notes.get(position);
    }



    public class NoteViewHolder extends RecyclerView.ViewHolder {


        private TextView nombre;
        private TextView numero;
        private LinearLayout layout;

        public NoteViewHolder(View item) {
            super(item);

            nombre = (TextView) item.findViewById(R.id.txtnombresec);
            numero = (TextView) item.findViewById(R.id.txtnumerosec);
            layout = (LinearLayout) item.findViewById(R.id.layoutsec);
        //falta color

        }

        public void bind(final SectorLocal sector) {

            nombre.setText(sector.getNombreSector());
            numero.setText("" +sector.getNumeroatendiendo());
            //layout.setBackgroundColor(Color.parseColor(sector.getColorSector()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onNoteSelectedListener != null) {

                        onNoteSelectedListener.onClick(sector);

                    }
                }
            });
        }
    }
}
