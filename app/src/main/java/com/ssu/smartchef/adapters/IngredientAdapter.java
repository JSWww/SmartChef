package com.ssu.smartchef.adapters;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.ssu.smartchef.data.IngredientData;
import com.ssu.smartchef.R;

import java.util.ArrayList;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ItemViewHolder> {

    public ArrayList<IngredientData> listData = new ArrayList<>();
    private int seletedPosition = -1;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ingredient_item, viewGroup, false);
        return new ItemViewHolder(view);
    }
    public void setPos(int pos){
        seletedPosition = pos;
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder itemViewHolder, final int i) {
        itemViewHolder.onBind(listData.get(i));
        if(seletedPosition == i){
            itemViewHolder.ingredientText.setPaintFlags(itemViewHolder.ingredientText.getPaintFlags()| Paint.FAKE_BOLD_TEXT_FLAG);
            itemViewHolder.ingredientWeight.setPaintFlags(itemViewHolder.ingredientWeight.getPaintFlags()| Paint.FAKE_BOLD_TEXT_FLAG);
        }
        else {
            itemViewHolder.ingredientText.setPaintFlags(itemViewHolder.ingredientText.getPaintFlags()&~ Paint.FAKE_BOLD_TEXT_FLAG);
            itemViewHolder.ingredientWeight.setPaintFlags(itemViewHolder.ingredientWeight.getPaintFlags()&~ Paint.FAKE_BOLD_TEXT_FLAG);
        }
        itemViewHolder.ingredientText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listData.get(i).setIngredientName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        itemViewHolder.ingredientWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains("g"))
                    listData.get(i).setIngredientWeight(Double.parseDouble(s.subSequence(0, s.length() - 1).toString()));
                else
                    listData.get(i).setIngredientWeight(Double.parseDouble(s.toString()));
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

    public void addItem(IngredientData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private EditText ingredientText;
        private EditText ingredientWeight;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            ingredientText = itemView.findViewById(R.id.regist_ingredient_name);
            ingredientWeight = itemView.findViewById(R.id.regist_ingredient_weight);
        }

        void onBind(IngredientData data) {

            if (data.isEditable()) {
                ingredientText.setText(data.getIngredientName());

                if (data.getIngredientWeight() != 0)
                    ingredientWeight.setText((int)(data.getIngredientWeight()) + "");
            }
            else {
                ingredientText.setText(data.getIngredientName());
                if (data.getIngredientWeight() == (long) data.getIngredientWeight())
                    ingredientWeight.setText(String.format("%.0fg", data.getIngredientWeight()));
                else
                    ingredientWeight.setText(String.format("%.1fg", data.getIngredientWeight()));
                ingredientText.setEnabled(false);
                ingredientWeight.setEnabled(false);
            }
        }
    }

    public ArrayList<IngredientData> getListData() {
        return listData;
    }
}
