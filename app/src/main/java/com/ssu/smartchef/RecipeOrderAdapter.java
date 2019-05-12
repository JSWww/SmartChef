package com.ssu.smartchef;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipeOrderAdapter extends RecyclerView.Adapter<RecipeOrderAdapter.ItemViewHolder> {

    private ArrayList<IngredientData> listData = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recipe_order_item, viewGroup, false);
        return new RecipeOrderAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeOrderAdapter.ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.onBind(listData.get(i));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(IngredientData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void onBind(IngredientData data) {
        }
    }
}
