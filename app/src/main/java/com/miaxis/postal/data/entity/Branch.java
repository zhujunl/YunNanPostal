package com.miaxis.postal.data.entity;


public class Branch {

    public String comcode;
    public String orgName;
    public boolean isSelected = false;


    //    private Long id;
    //    private String orgCode;
    public String orgNode;
    //    private String orgName;
    //    private String orgType;
    //    private String phoneNo;
    //    private String city;
    //    private String systemOrgCode;
    //    private String systemOrgNode;
    //    private int subCount;
    //    private boolean isParent;
    //    private boolean open=true;
    //    private String parentCode;
    //    private String orgAddress;
    //    private String level;
    //    private String comcode;
    //    private String remark;


    public Branch() {
    }

    //    public Branch(String comcode, String orgName) {
    //        this.comcode = comcode;
    //        this.orgName = orgName;
    //    }
    //
    //    public Branch(String comcode, String orgName, boolean isSelected) {
    //        this.comcode = comcode;
    //        this.orgName = orgName;
    //        this.isSelected = isSelected;
    //    }


    @Override
    public String toString() {
        return "Branch{" +
                "comcode='" + comcode + '\'' +
                ", orgName='" + orgName + '\'' +
                ", isSelected=" + isSelected +
                ", orgNode='" + orgNode + '\'' +
                '}';
    }
}
