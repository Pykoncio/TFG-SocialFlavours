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
import com.iesvegademijas.socialflavours.data.remote.dto.entities.Item;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ShoppingListItemAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Item> itemsList;
    private ShoppingListItemAdapterCallBack callBack;

    public ShoppingListItemAdapter(Context context, ArrayList<Item> itemsList)
    {
        super();
        this.context = context;
        this.itemsList = itemsList;
    }

    @Override
    public int getCount() {
        return itemsList.size();
    }

    @Override
    public Object getItem(int position) {
        if (itemsList == null)
        {
            return null;
        }
        else
        {
            return itemsList.size();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        ShoppingListItemWrapper shoppingListItemWrapper;

        if (item == null)
        {
            shoppingListItemWrapper = new ShoppingListItemWrapper();
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            item = inflater.inflate(R.layout.list_item_shopping_list_item, parent, false);
            shoppingListItemWrapper.tvItemName = item.findViewById(R.id.tv_item_name);
            shoppingListItemWrapper.tvItemUnits = item.findViewById(R.id.tv_item_units);
            shoppingListItemWrapper.editButton = item.findViewById(R.id.ib_li_modify_item);
            shoppingListItemWrapper.deleteButton = item.findViewById(R.id.ib_li_delete_item);
            item.setTag(shoppingListItemWrapper);
        }
        else
        {
            shoppingListItemWrapper = (ShoppingListItemWrapper) item.getTag();
        }

        Item itemList = itemsList.get(position);

        shoppingListItemWrapper.tvItemName.setText(itemList.getName());
        shoppingListItemWrapper.tvItemUnits.setText(String.valueOf(itemList.getQuantity()));

        shoppingListItemWrapper.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.editItem(position);
            }
        });

        shoppingListItemWrapper.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.deleteItem(position);
            }
        });

        return item;
    }

    static class ShoppingListItemWrapper {
        TextView tvItemName;
        TextView tvItemUnits;

        ImageButton editButton;
        ImageButton deleteButton;
    }

    public interface ShoppingListItemAdapterCallBack
    {
        void editItem(int position);
        void deleteItem(int position);
    }

    public void setCallback(ShoppingListItemAdapterCallBack shoppingListItemAdapterCallBack) {this.callBack = shoppingListItemAdapterCallBack;}
}
