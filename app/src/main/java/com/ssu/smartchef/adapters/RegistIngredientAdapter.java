package com.ssu.smartchef.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import com.ssu.smartchef.R;
import com.ssu.smartchef.data.IngredientData;


import java.util.ArrayList;

public class RegistIngredientAdapter extends RecyclerView.Adapter<RegistIngredientAdapter.ItemViewHolder> {
    private ArrayList<IngredientData> listData = new ArrayList<>();

    @NonNull
    @Override
    public RegistIngredientAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.regist_ingredient_item, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistIngredientAdapter.ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.onBind(listData.get(i));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
    public ArrayList<IngredientData> getItem() {
        return listData;
    }
    public void addItem(IngredientData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        private EditText ingredient;
        private EditText weight;


        ItemViewHolder(View itemView) {
            super(itemView);
            ingredient = itemView.findViewById(R.id.regist_ingredient_name);
            weight = itemView.findViewById(R.id.regist_ingredient_weight);
        }

        void onBind(IngredientData data) {
            ingredient.setText(data.getIngredientName());
            weight.setText(data.getIngredientWeight());
        }
    }
}
