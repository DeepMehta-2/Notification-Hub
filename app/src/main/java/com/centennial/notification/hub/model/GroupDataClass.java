package com.centennial.notification.hub.model;

public class GroupDataClass {

    int Group_ID;
    String Group_Name;
    String Group_App_Name;
    String Group_Pkg_Name;
    int is_inGroup;

    public GroupDataClass() {
    }

    public GroupDataClass(String group_Name) {
        Group_Name = group_Name;
    }

//    public GroupDataClass(int group_ID, String group_Name, String group_App_Name, String group_Pkg_Name) {
//        Group_ID = group_ID;
//        Group_Name = group_Name;
//        Group_App_Name = group_App_Name;
//        Group_Pkg_Name = group_Pkg_Name;
//    }


    public GroupDataClass(int group_ID, String group_Name, String group_App_Name, String group_Pkg_Name, int is_inGroup) {
        Group_ID = group_ID;
        Group_Name = group_Name;
        Group_App_Name = group_App_Name;
        Group_Pkg_Name = group_Pkg_Name;
        this.is_inGroup = is_inGroup;
    }

    public int getGroup_ID() {
        return Group_ID;
    }

    public void setGroup_ID(int group_ID) {
        Group_ID = group_ID;
    }

    public String getGroup_Name() {
        return Group_Name;
    }

    public void setGroup_Name(String group_Name) {
        Group_Name = group_Name;
    }

    public String getGroup_App_Name() {
        return Group_App_Name;
    }

    public void setGroup_App_Name(String group_App_Name) {
        Group_App_Name = group_App_Name;
    }

    public String getGroup_Pkg_Name() {
        return Group_Pkg_Name;
    }

    public void setGroup_Pkg_Name(String group_Pkg_Name) {
        Group_Pkg_Name = group_Pkg_Name;
    }

    public int getIs_inGroup() {
        return is_inGroup;
    }

    public void setIs_inGroup(int is_inGroup) {
        this.is_inGroup = is_inGroup;
    }
}

