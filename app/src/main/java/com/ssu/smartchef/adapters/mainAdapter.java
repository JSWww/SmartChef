package com.ssu.smartchef.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ssu.smartchef.activities.RecipeClickActivity;
import com.ssu.smartchef.data.MainViewData;
import com.ssu.smartchef.R;

import java.util.ArrayList;

public class mainAdapter extends RecyclerView.Adapter<mainAdapter.ItemViewHolder> {
    public ArrayList<MainViewData> listData = new ArrayList<>();
    Context mContext;
    public mainAdapter(){ }
    public mainAdapter(Context context){
        mContext = context;
    }
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

    public void addItem(MainViewData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }
    public void setFilter(ArrayList<MainViewData> items) {
        listData.clear();
        listData.addAll(items);
        notifyDataSetChanged();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title;
        private TextView writer;
        private ImageView food;

        ItemViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.regist_title);
            writer = itemView.findViewById(R.id.writer);
            food = itemView.findViewById(R.id.food);
        }

        void onBind(MainViewData data) {
            title.setText(data.getTitle());
            writer.setText(data.getWriter());

            Glide.with(itemView)
                        .load(data.getIamgeURL())
                        .into(food);
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            Intent intent = new Intent(context, RecipeClickActivity.class);
            intent.putExtra("recipeID", getAdapterPosition() +"");
            context.startActivity(intent);
        }
    }

}
