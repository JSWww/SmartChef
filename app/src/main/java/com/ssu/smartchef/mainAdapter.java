package com.ssu.smartchef;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class mainAdapter extends RecyclerView.Adapter<mainAdapter.ItemViewHolder> {
    private ArrayList<Data> listData = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_item, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.onBind(listData.get(i));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
    void addItem(Data data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView writer;
        private TextView tag;
        private ImageView food;

        ItemViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            writer = itemView.findViewById(R.id.writer);
            tag = itemView.findViewById(R.id.tag);
            food = itemView.findViewById(R.id.food);
        }

        void onBind(Data data) {
            title.setText(data.getTitle());
            writer.setText(data.getWriter());
            tag.setText(data.getTag());
            food.setImageResource(data.getResId());
        }
    }
}
