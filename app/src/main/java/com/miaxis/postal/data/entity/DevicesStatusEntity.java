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
        private String macAddress;
        private String status;
        private String disableRemark;
        private String enableRemark;

        public String getMacAddress() {
            return macAddress;
        }

        public void setMacAddress(String macAddress) {
            this.macAddress = macAddress;
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

        public String getEnableRemark() {
            return enableRemark;
        }

        public void setEnableRemark(String enableRemark) {
            this.enableRemark = enableRemark;
        }
    }
}
