package com.ssu.smartchef.adapters;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ssu.smartchef.R;

import com.ssu.smartchef.data.SpiceData;

import java.util.ArrayList;

public class SpiceAdapter extends RecyclerView.Adapter<SpiceAdapter.ItemViewHolder> {
    public ArrayList<SpiceData> listData = new ArrayList<>();
    public Context mcontext;
    public SpiceAdapter(Context mcontext){
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spice_item, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder itemViewHolder,int i) {
        itemViewHolder.onBind(listData.get(i));
        itemViewHolder.spiceName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listData.get(itemViewHolder.getAdapterPosition()).setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        itemViewHolder.spiceNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listData.get(itemViewHolder.getAdapterPosition()).setNum(Integer.parseInt(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void addItem(SpiceData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private EditText spiceName;
        private TextView spiceNum;

        ItemViewHolder(View itemView) {
            super(itemView);
            spiceName = itemView.findViewById(R.id.spiceName);
            spiceNum = itemView.findViewById(R.id.spiceNum);
        }

        void onBind(SpiceData data) {
            spiceName.setText(data.getName());
            spiceNum.setText(data.getNum()+"");
            spiceName.requestFocus();
        }


    }
}