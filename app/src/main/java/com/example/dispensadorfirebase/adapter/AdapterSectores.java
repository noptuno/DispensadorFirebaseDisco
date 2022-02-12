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
import com.example.dispensadorfirebase.clase.Sectores;

import java.util.ArrayList;
import java.util.List;

public class AdapterSectores extends RecyclerView.Adapter<AdapterSectores.NoteViewHolder> {

    private List<Sectores> notes;
    private OnNoteSelectedListener onNoteSelectedListener;
    private OnNoteDetailListener onDetailListener;

    public AdapterSectores() {
        this.notes = new ArrayList<>();
    }

    public AdapterSectores(List<Sectores> notes) {
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

    public List<Sectores> getNotes() {
        return notes;
    }

    public void setNotes(List<Sectores> notes) {
        this.notes = notes;
    }

    public void setOnNoteSelectedListener(OnNoteSelectedListener onNoteSelectedListener) {
        this.onNoteSelectedListener = onNoteSelectedListener;
    }

    public void setOnDetailListener(OnNoteDetailListener onDetailListener) {
        this.onDetailListener = onDetailListener;
    }


    public interface OnNoteSelectedListener {
        void onClick(Sectores note);
    }

    public interface OnNoteDetailListener {
        void onDetail(Sectores note);
    }

    public Sectores getposicionactual(int position) {
        return notes.get(position);
    }





    public class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView nombre, habilitado;
        private TextView limite;
        private CheckBox checkBox;

private LinearLayout layout;

        public NoteViewHolder(View item) {
            super(item);

            nombre = (TextView) item.findViewById(R.id.txtnombresector);
            limite = (TextView) item.findViewById(R.id.txtlimite);
            checkBox = (CheckBox) item.findViewById(R.id.checkBox);
            layout = (LinearLayout) item.findViewById(R.id.layout);
            habilitado  = (TextView) item.findViewById(R.id.textView3);


        }

        public void bind(final Sectores sector) {

            habilitado.setVisibility(View.GONE);
            nombre.setText(sector.getNombre());
            limite.setText("" +sector.getLimite());
            layout.setBackgroundColor(Color.parseColor(sector.getColor()));
            checkBox.setVisibility(View.GONE);


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
