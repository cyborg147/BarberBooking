package com.example.barberbooking.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.barberbooking.Common.Common;
import com.example.barberbooking.Interface.IRecyclerItemSelectedListener;
import com.example.barberbooking.Model.Barber;
import com.example.barberbooking.R;

import java.util.ArrayList;
import java.util.List;

public class MyBarberAdapter extends RecyclerView.Adapter<MyBarberAdapter.MyViewHolder> {

    Context context;
    List<Barber> barberList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyBarberAdapter(Context context, List<Barber> barberList) {
        this.context = context;
        this.barberList = barberList;
        cardViewList = new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_barber,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        myViewHolder.txt_barber_name.setText(barberList.get(i).getName());
        myViewHolder.ratingBar.setRating((float)barberList.get(i).getRating());
        if(!cardViewList.contains(myViewHolder.card_barber))
            cardViewList.add(myViewHolder.card_barber);

        myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                for(CardView cardView : cardViewList)
                {
                    cardView.setCardBackgroundColor(context.getResources()
                            .getColor((android.R.color.white)));
                }

                myViewHolder.card_barber.setCardBackgroundColor(
                        context.getResources()
                        .getColor(android.R.color.holo_orange_dark)
                );

                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_BARBER_SELECTED,barberList.get(pos));
                intent.putExtra(Common.KEY_STEP,2);
                localBroadcastManager.sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return barberList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_barber_name;
        RatingBar ratingBar;
        CardView card_barber;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_barber_name = (TextView)itemView.findViewById(R.id.txt_barber_name);
            ratingBar = (RatingBar)itemView.findViewById(R.id.rtb_barber);
            card_barber = (CardView)itemView.findViewById(R.id.card_barber);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelectedListener(view,getAdapterPosition());
        }
    }
}
