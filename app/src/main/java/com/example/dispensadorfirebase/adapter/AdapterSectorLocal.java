package com.example.dispensadorfirebase.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.clase.SectorLocal;

import java.util.ArrayList;
import java.util.List;

public class AdapterSectorLocal extends RecyclerView.Adapter<AdapterSectorLocal.NoteViewHolder> {

    private List<SectorLocal> notes;
    private OnNoteSelectedListener onNoteSelectedListener;
    private OnNoteDetailListener onDetailListener;

    public AdapterSectorLocal() {
        this.notes = new ArrayList<>();
    }

    public AdapterSectorLocal(List<SectorLocal> notes) {
        this.notes = notes;
    }


    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View elementoTitular = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note_sectores, parent, false);

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
        private TextView limite;
        private CheckBox checkBox;

private LinearLayout layout;

        public NoteViewHolder(View item) {
            super(item);

            nombre = (TextView) item.findViewById(R.id.txtnombresector);
            limite = (TextView) item.findViewById(R.id.txtlimite);
            checkBox = (CheckBox) item.findViewById(R.id.checkBox);
            layout = (LinearLayout) item.findViewById(R.id.layoutlocal);
        //falta color

        }

        public void bind(final SectorLocal sector) {

            nombre.setText(sector.getNombreSector());
            limite.setText("" +sector.getLimite());
            layout.setBackgroundColor(Color.parseColor(sector.getColorSector()));


            if (sector.getEstado() == 1){
                checkBox.setChecked(true);
            }else{
                checkBox.setChecked(false);
            }

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (onDetailListener != null) {

                        if (sector.getEstado() == 1){
                            sector.setEstado(0);
                            checkBox.setChecked(false);
                        }else{
                            sector.setEstado(1);
                            checkBox.setChecked(true);
                        }

                        onDetailListener.onDetail(sector);

                    }

                }
            });


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
