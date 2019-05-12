package com.ssu.smartchef;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RegistAdapter extends RecyclerView.Adapter<RegistAdapter.ItemViewHolder> {
    private ArrayList<RecipeData> listData = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.regist_item, viewGroup, false);
        return new RegistAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistAdapter.ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.onBind(listData.get(i));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
    void addItem(RecipeData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }
    class ItemViewHolder extends RecyclerView.ViewHolder {


        ItemViewHolder(View itemView) {
            super(itemView);
        }

        void onBind(RecipeData data) {

        }
    }
}
