package com.ssu.smartchef.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.ssu.smartchef.R;
import com.ssu.smartchef.data.IngredientData;
import com.ssu.smartchef.data.RecipeStepData;

import java.util.ArrayList;

public class RegistAdapter extends RecyclerView.Adapter<RegistAdapter.ItemViewHolder> {
    private ArrayList<RecipeStepData> listData = new ArrayList<>();
    private Context mContext;

    public RegistAdapter(Context mContext) {
        this.mContext = mContext;
    }

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

    public void addItem(RecipeStepData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }


    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private EditText title, explain;
        private ImageView food;
        public RecyclerView items;
        public ImageView add_btn;
        RegistIngredientAdapter itemListDataAdapter = new RegistIngredientAdapter();

        ItemViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.regist_step_title);
            explain = itemView.findViewById(R.id.regist_step_explain);
            food = itemView.findViewById(R.id.regist_step_food);
            items = itemView.findViewById(R.id.regist_ingredient_view);
            add_btn = itemView.findViewById(R.id.add_ingredient_btn);
            itemView.setOnClickListener(this);
        }

        void onBind(RecipeStepData data) {

        }

        @Override
        public void onClick(View v) {
            ViewGroup.LayoutParams layoutParams = items.getLayoutParams();
            itemListDataAdapter.addItem(new IngredientData());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(v.getContext());
            items.setAdapter(itemListDataAdapter);
            items.setLayoutManager(linearLayoutManager);
            items.setLayoutParams(layoutParams);
            itemListDataAdapter.notifyDataSetChanged();
        }
    }
}