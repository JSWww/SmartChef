package com.ssu.smartchef.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ssu.smartchef.R;
import com.ssu.smartchef.data.IngredientData;
import com.ssu.smartchef.data.RecipeStepData;

import java.util.ArrayList;

public class RecipeOrderAdapter extends RecyclerView.Adapter<RecipeOrderAdapter.ItemViewHolder> {

    private ArrayList<RecipeStepData> listData = new ArrayList<>();

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

    public void addItem(RecipeStepData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    public ArrayList<RecipeStepData> getListData() {
        return listData;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView step;
        private TextView stepTitle;
        private TextView stepExplain;
        private ImageView stepImage;
        private RecyclerView ingredientRecycler;
        private IngredientAdapter ingredientAdapter;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            step = itemView.findViewById(R.id.step);
            stepTitle = itemView.findViewById(R.id.stepTitle);
            stepExplain = itemView.findViewById(R.id.stepExplain);
            stepImage = itemView.findViewById(R.id.stepImage);

            ingredientRecycler = itemView.findViewById(R.id.ingredientRecycler);
            ingredientRecycler.setNestedScrollingEnabled(false);
            ingredientRecycler.setHasFixedSize(false);
            ingredientAdapter = new IngredientAdapter();
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            ingredientRecycler.setLayoutManager(linearLayoutManager);
            ingredientAdapter = new IngredientAdapter();
            ingredientRecycler.setAdapter(ingredientAdapter);

        }

        void onBind(RecipeStepData data) {
            if (ingredientAdapter.getItemCount() == 0) {
                step.setText("STEP " + (getAdapterPosition() + 1));
                stepTitle.setText(data.getStepTitle());
                stepExplain.setText(data.getStepExplain());

                Glide.with(itemView)
                        .load(data.getStepImageURL())
                        .into(stepImage);

                for (IngredientData ingredientData : data.getIngredientArrayList())
                    ingredientAdapter.addItem(ingredientData);
            }

            ingredientAdapter.notifyDataSetChanged();
        }
    }
}
