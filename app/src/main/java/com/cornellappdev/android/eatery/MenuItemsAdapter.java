package com.cornellappdev.android.eatery;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cornellappdev.android.eatery.model.MealMenuModel;

import java.util.List;

public class MenuItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    MealMenuModel menu;
    List<String> flatMenu;
    Context context;

    final int CATEGORY = 0, ITEM = 1;

    public MenuItemsAdapter(MealMenuModel menu, Context context){
        this.menu = menu;
        flatMenu = menu.getMenuAsList();
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view;

        if(viewType == CATEGORY){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_category_title_item, parent, false);
            viewHolder = new CategoryViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
            viewHolder = new ItemViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String item = flatMenu.get(position);
        float scale = context.getResources().getDisplayMetrics().density;
        switch (holder.getItemViewType()){
            case CATEGORY:
                CategoryViewHolder viewHolder1 = (CategoryViewHolder) holder;
                viewHolder1.categoryTv.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                viewHolder1.categoryTv.setTextSize(16);
                viewHolder1.categoryTv.setTextColor(ContextCompat.getColor(context, R.color.primary));
                viewHolder1.categoryTv.setPadding(
                        (int) (16 * scale + 0.5f), (int) (12 * scale + 0.5f), 0,
                        (int) (12 * scale + 0.5f));
                viewHolder1.categoryTv.setText(item);
                break;
            case ITEM:
                ItemViewHolder viewHolder2 = (ItemViewHolder) holder;
                viewHolder2.menuItemTv.setText(item);
                viewHolder2.menuItemTv.setTextSize(14);
                viewHolder2.menuItemTv.setTextColor(ContextCompat.getColor(context, R.color.primary));
                viewHolder2.menuItemTv.setPadding(
                        (int) (8 * scale + 0.5f), (int) (8 * scale + 0.5f), 0,
                        (int) (8 * scale + 0.5f));
                viewHolder2.starButton.setPadding((int) (16 * scale + 0.5f), (int) (8 * scale + 0.5f), 0,
                        (int) (8 * scale + 0.5f));
                viewHolder2.starButton.setOnClickListener(view -> {
                    if (viewHolder2.starButton.getDrawable().getConstantState() == context.getResources().getDrawable(R.drawable.ic_star_small).getConstantState()){
                        viewHolder2.starButton.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_star_filled));
                    } else {
                        viewHolder2.starButton.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_star_small));
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return flatMenu.size();
    }

    @Override
    public int getItemViewType(int position){
        String item = flatMenu.get(position);
        if(menu.containsCategory(item)){
            return CATEGORY;
        }
        return ITEM;
    }
}

class CategoryViewHolder extends RecyclerView.ViewHolder{

    TextView categoryTv;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        categoryTv = itemView.findViewById(R.id.category_title_tv);
    }
}

class ItemViewHolder extends RecyclerView.ViewHolder{

    ImageButton starButton;
    TextView menuItemTv;

    public ItemViewHolder(@NonNull View itemView){
        super(itemView);
        starButton = itemView.findViewById(R.id.star_button);
        menuItemTv = itemView.findViewById(R.id.menu_item_tv);

    }

}

