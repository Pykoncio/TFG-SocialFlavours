package com.iesvegademijas.socialflavours.data.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iesvegademijas.socialflavours.R;
import com.iesvegademijas.socialflavours.data.remote.dto.foodRelated.ShoppingList;

import java.util.ArrayList;

public class ShoppingListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ShoppingList> shoppingLists;
    private ShoppingListAdapterCallBack callBack;

    public ShoppingListAdapter(Context context, ArrayList<ShoppingList> shoppingLists)
    {
        super();
        this.context = context;
        this.shoppingLists = shoppingLists;
    }

    @Override
    public int getCount() {
        return shoppingLists.size();
    }

    @Override
    public Object getItem(int position) {
        if (shoppingLists == null)
        {
            return null;
        }
        else
        {
            return shoppingLists.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        ShoppingListWrapper shoppingListWrapper;

        if (item == null)
        {
            shoppingListWrapper = new ShoppingListWrapper();
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            item = inflater.inflate(R.layout.list_item_shopping_list, parent, false);
            shoppingListWrapper.shoppingListName = item.findViewById(R.id.tv_shopping_list_name);
            shoppingListWrapper.deleteButton = item.findViewById(R.id.ib_li_delete);
            item.setTag(shoppingListWrapper);
        }
        else
        {
            shoppingListWrapper = (ShoppingListWrapper) item.getTag();
        }

        ShoppingList shoppingList = shoppingLists.get(position);

        shoppingListWrapper.shoppingListName.setText(shoppingList.getListName());

        shoppingListWrapper.shoppingListName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.goToList(position);
            }
        });

        shoppingListWrapper.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.deleteShoppingList(position);
            }
        });

        return item;
    }

    static class ShoppingListWrapper
    {
        TextView shoppingListName;
        ImageButton deleteButton;
    }

    public interface ShoppingListAdapterCallBack
    {
        void goToList(int position);
        void deleteShoppingList(int position);
    }

    public void setCallback(ShoppingListAdapterCallBack callBack) {this.callBack = callBack;}
}
