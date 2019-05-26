package com.ssu.smartchef.adapters;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ssu.smartchef.data.IngredientData;
import com.ssu.smartchef.R;

import java.util.ArrayList;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ItemViewHolder> {

    private ArrayList<IngredientData> listData = new ArrayList<>();


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.regist_ingredient_item, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientAdapter.ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.onBind(listData.get(i));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void addItem(IngredientData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView ingredientText;
        private TextView ingredientWeight;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            ingredientText = itemView.findViewById(R.id.regist_ingredient_name);
            ingredientWeight = itemView.findViewById(R.id.regist_ingredient_weight);
        }

        void onBind(IngredientData data) {
            ingredientText.setText(data.getIngredientName());
            ingredientWeight.setText(data.getIngredientWeight());

            if (!data.isEditable()) {
                ingredientText.setEnabled(false);
                ingredientWeight.setEnabled(false);
            }
        }
    }
}
