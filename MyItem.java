/*
 * Developed by Peter Brüesch on 21/08/18 20:09.
 * Last modified 25/07/18 13:33.
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package petertest.myapplication;

public class MyItem {

    private String name;

    private boolean isSelected;

    public MyItem(String name) {
        this.setName(name);
    }

    public MyItem(String name, boolean isSelected) {
        this.name = name;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setName(String name) {
        this.name = name;
    }
}
