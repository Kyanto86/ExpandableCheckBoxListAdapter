package test.myapplication;

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
