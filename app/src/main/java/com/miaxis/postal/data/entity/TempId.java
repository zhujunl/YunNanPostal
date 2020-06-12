package com.miaxis.postal.data.entity;

public class TempId {

    private String personId;
    private String checkId;

    public TempId() {
    }

    public TempId(String personId, String checkId) {
        this.personId = personId;
        this.checkId = checkId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getCheckId() {
        return checkId;
    }

    public void setCheckId(String checkId) {
        this.checkId = checkId;
    }

    public static final class TempIdBuilder {
        private String personId;
        private String checkId;

        private TempIdBuilder() {
        }

        public static TempIdBuilder aTempId() {
            return new TempIdBuilder();
        }

        public TempIdBuilder personId(String personId) {
            this.personId = personId;
            return this;
        }

        public TempIdBuilder checkId(String checkId) {
            this.checkId = checkId;
            return this;
        }

        public TempId build() {
            TempId tempId = new TempId();
            tempId.setPersonId(personId);
            tempId.setCheckId(checkId);
            return tempId;
        }
    }
}
