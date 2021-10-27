package com.miaxis.postal.data.entity;

public class DevicesStatusEntity {

    private String code;
    private String message;
    private DataDTO data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO {
        private int id;
        private String macAddress;
        private String lat;
        private String lng;
        private String orgCode;
        private String orgNode;
        private String status;
        private String disableRemark;
        private String createTime;
        private String updateTime;
        private String heartBeatTime;
        private String isLogin;
        private String systemOrgCode;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMacAddress() {
            return macAddress;
        }

        public void setMacAddress(String macAddress) {
            this.macAddress = macAddress;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public String getOrgCode() {
            return orgCode;
        }

        public void setOrgCode(String orgCode) {
            this.orgCode = orgCode;
        }

        public String getOrgNode() {
            return orgNode;
        }

        public void setOrgNode(String orgNode) {
            this.orgNode = orgNode;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDisableRemark() {
            return disableRemark;
        }

        public void setDisableRemark(String disableRemark) {
            this.disableRemark = disableRemark;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getHeartBeatTime() {
            return heartBeatTime;
        }

        public void setHeartBeatTime(String heartBeatTime) {
            this.heartBeatTime = heartBeatTime;
        }

        public String getIsLogin() {
            return isLogin;
        }

        public void setIsLogin(String isLogin) {
            this.isLogin = isLogin;
        }

        public String getSystemOrgCode() {
            return systemOrgCode;
        }

        public void setSystemOrgCode(String systemOrgCode) {
            this.systemOrgCode = systemOrgCode;
        }
    }
}
