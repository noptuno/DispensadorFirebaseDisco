package com.example.dispensadorfirebase.adapter;

import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.clase.SectorLocal;

import java.util.ArrayList;
import java.util.List;

public class AdapterSupervisorPrincipal extends RecyclerView.Adapter<AdapterSupervisorPrincipal.NoteViewHolder> {

    private List<SectorLocal> notes;
    private OnNoteSelectedListener onNoteSelectedListener;
    private OnNoteDetailListener onDetailListener;
    private int CantidadSectores;

    public AdapterSupervisorPrincipal() {
        this.notes = new ArrayList<>();

    }

    public AdapterSupervisorPrincipal(List<SectorLocal> notes) {
        this.notes = notes;
    }


    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View elementoTitular;

            elementoTitular = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_note_sectores_supervisor, parent, false);


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
private CardView card;
private Button btnNotificacion;

        public NoteViewHolder(View item) {
            super(item);

            nombre = (TextView) item.findViewById(R.id.txtssector_supervisor);
            numero = (TextView) item.findViewById(R.id.txtscantidad_supervisor);
            card = (CardView) item.findViewById(R.id.cardview);
            btnNotificacion = item.findViewById(R.id.btnnoti);

        //falta color

        }

        public void bind(final SectorLocal sector) {

            nombre.setText(sector.getNombreSector());
            numero.setText("" +sector.getCantidadEspera());
            card.setBackgroundColor(Color.parseColor(sector.getColorSector()));


            if (sector.getNotificacion()==1 && sector.getNotificaciondeshabilitar()==0){
                btnNotificacion.setEnabled(true);
            }else{
                btnNotificacion.setEnabled(false);
            }


            btnNotificacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (onDetailListener != null) {
                        sector.setNotificaciondeshabilitar(1);
                        onDetailListener.onDetail(sector);

                    }

                }
            });


        }
    }
}
