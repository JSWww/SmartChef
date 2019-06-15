package com.ssu.smartchef.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
    public ArrayList<RecipeStepData> listData = new ArrayList<>();
    private Context mContext;
    private Activity mActivity;
    public ImageView test;

    public RegistAdapter(Context mContext,Activity mActivity) {
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.regist_item, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder itemViewHolder, final int i) {
        itemViewHolder.onBind(listData.get(itemViewHolder.getAdapterPosition()));
        listData.get(itemViewHolder.getAdapterPosition()).setIngredientArrayList(itemViewHolder.itemListDataAdapter.getListData());

        itemViewHolder.title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listData.get(itemViewHolder.getAdapterPosition()).setStepTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        itemViewHolder.explain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listData.get(itemViewHolder.getAdapterPosition()).setStepExplain(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void addItem(RecipeStepData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private EditText title, explain;
        public ImageView food;
        public RecyclerView items;
        public ImageView add_btn;
        IngredientAdapter itemListDataAdapter = new IngredientAdapter();

        ItemViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.regist_step_title);
            explain = itemView.findViewById(R.id.regist_step_explain);
            food = itemView.findViewById(R.id.regist_step_food);
            items = itemView.findViewById(R.id.regist_ingredient_view);
            add_btn = itemView.findViewById(R.id.add_ingredient_btn);
            add_btn.setOnClickListener(this);
            food.setOnClickListener(this);
        }

        void onBind(RecipeStepData data) {
            title.setText(data.getStepTitle());
            explain.setText(data.getStepExplain());
            title.requestFocus();
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.add_ingredient_btn){
                items.setAdapter(itemListDataAdapter);
                itemListDataAdapter.addItem(new IngredientData());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(v.getContext());
                items.setLayoutManager(linearLayoutManager);
                itemListDataAdapter.notifyItemChanged(itemListDataAdapter.getItemCount());
            }
            else if(v.getId() == R.id.regist_step_food){
                test = food;
                Intent intent = new Intent();
                intent.setType("image/*");

                intent.setAction(Intent.ACTION_GET_CONTENT);
                mActivity.startActivityForResult(Intent.createChooser(intent,"이미지를 선택하세요"),1);
            }
        }

    }
}