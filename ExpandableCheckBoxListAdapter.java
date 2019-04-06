/*
 * Developed by Peter Brüesch on 21/08/18 20:09.
 * Last modified 25/07/18 13:33.
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package petertest.myapplication;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableCheckBoxListAdapter extends BaseExpandableListAdapter {

    private Context context;

    private List<MyItem> headers;//list of headers (that will be expanded). Item is a placeholder for individual Pojos.
    private HashMap<MyItem, List<MyItem>> listHashMap;//Hashmap that orders a list of items to each header-item
    private AdapterListener mListener;


    ArrayList<String> mCheckedItems = new ArrayList<>();//List of the names of the checked Items.
    ArrayList<Integer> checkedPositionsList = new ArrayList<>();//List of the group positions of checked Items. These are used to change the header layout.

    public ExpandableCheckBoxListAdapter(Context context, List<MyItem> headers, HashMap<MyItem, List<MyItem>> listHashMap, ArrayList<String> checkedItems,
                                  ArrayList<Integer> checkedPositions) {
        this.context = context;
        this.headers = headers;
        this.listHashMap = listHashMap;

        //We assume a static list, where the items will always be the same.
        //We have the state of the items (i.e. checked/unchecked) saved elsewhere (preferences or database).
        //When we inititialize the adapter we tell the adapter the state of these items. This way the adapter continues at the correct saved state.
        this.mCheckedItems = checkedItems;
        if (checkedPositions != null) {

            this.checkedPositionsList = checkedPositions;
        }
    }

    //Group count depends on how many headers we have.
    @Override
    public int getGroupCount() {
        return headers.size();
    }

    //Children count at position i. I.e. depending on the header.
    @Override
    public int getChildrenCount(int i) {
        return listHashMap.get(headers.get(i)).size();
    }

    //get specific group/header.
    @Override
    public Object getGroup(int i) {
        return headers.get(i);
    }

    //get specific child. That is, which position the child is (i1) at the position of specific header (i)
    @Override
    public Object getChild(int i, int i1) {
        return listHashMap.get(headers.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int i, boolean isExpanded, View view, ViewGroup viewGroup) {
        //get the header at position i.
        MyItem header = (MyItem) getGroup(i);

        //inflate correct layout
        if (view == null) {

            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.my_header_layout, null);
        }

        ImageView expandIcon = view.findViewById(R.id.iv_expandIcon);
        LinearLayout ll_header = view.findViewById(R.id.ll_header);
        TextView header_tv = view.findViewById(R.id.tv_header);

        //set expandIcons
        if (isExpanded) {


            expandIcon.setImageResource(R.drawable.ic_remove);
        } else expandIcon.setImageResource(R.drawable.ic_add);

        //set text
        header_tv.setText(header.getName());

        int checkCounter = 0;

        for (int k = 0; k < checkedPositionsList.size(); k++) {

            //we check if the group position (i) has a checked item.
            if (checkedPositionsList.get(k) == i) {

                //just add to counter if correct, then exit loop.
                checkCounter++;
                break;
            }
        }

        //set different backgrounds, depending whether a child item is checked or not.
        //This let's users see that they have checked child items, even when the list isn't expanded.
        if (checkCounter > 0) {

            ll_header.setBackground(ContextCompat.getDrawable(context,R.drawable.background_list_item_checked));
        } else
            ll_header.setBackground(ContextCompat.getDrawable(context,R.drawable.background_list_item_unchecked));

        return view;
    }

    @Override
    public View getChildView(final int i, final int i1, boolean isLastItem, View view, ViewGroup viewGroup) {
        final MyItem childItem = (MyItem) getChild(i, i1);

        final String name = childItem.getName();//get the item name in a variable because of multiple uses.

        Holder holder = null;

        if (view == null) {

            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.my_child_layout, null);
            holder = new Holder();
            holder.checkBox = view.findViewById(R.id.my_cb);
            holder.item_tv =  view.findViewById(R.id.my_item_name_tv);

            view.setTag(holder);
        } else {

            holder = (Holder) view.getTag();
            holder.checkBox.setOnCheckedChangeListener(null);

        }

        holder.item_tv.setText(name);

        //isSelected is the checked state that is an attribute of the MyItem Pojo.
        //The MyItem Pojo should have two constructors. One for the Group and one for the ChildItem.
        //The Group will usually only have the String name. The ChildItem will have String name and boolean isSelected as attributes.
        holder.checkBox.setChecked(childItem.isSelected());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                //update the selected/checked state of the specific childItem, by using the setter method in the Pojo.
                childItem.setSelected(isChecked);

                //add or remove the name and the position of the checked items.
                //then notify data set changed and tell the listener that we had a click.

                //The listener is an interface and it will be handled by the corresponding fragment/activity.
                //In this case the listener updates/saves the checked Items within your database/preferences by calling getCheckedItems.
                //This is useful for filters. When we load the app again, the adapter will "remember" which Items are checked because we load their state as attributes of this adapter.
                //Filters are thus always up-to-date depending on what their last state was.
                //As said, their state is saved in a db/preferences.
                if (isChecked) {

                    mCheckedItems.add(name);
                    checkedPositionsList.add(i);

                    notifyDataSetChanged();
                    mListener.onClick();

                } else {

                    mCheckedItems.remove(name);
                    checkedPositionsList.remove(Integer.valueOf(i));

                    notifyDataSetChanged();
                    mListener.onClick();
                }
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    static class Holder {

        CheckBox checkBox;
        TextView item_tv;
    }

    public ArrayList<String> getCheckedItems() {
        return mCheckedItems;
    }

    //will be called from fragment/activity so set an OnClickListener
    public void setListener(AdapterListener listener) {

        this.mListener = listener;
    }

    public interface AdapterListener {
        //tells the listener in activity/fragment that we have a click event. The adapter knows nothing of what to do with that click event.
        //handling the click event (such as saving in db/preferences) is thus left to the fragment.
        void onClick();
    }
}


