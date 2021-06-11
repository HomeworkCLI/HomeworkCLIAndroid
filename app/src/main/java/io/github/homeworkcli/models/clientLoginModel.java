package io.github.homeworkcli.models;

import java.io.Serializable;

public class clientLoginModel extends BaseModel {
    private clientLoginData data;

    public clientLoginData getData() {
        return this.data;
    }

    public void setData(clientLoginData data) {
        this.data = data;
    }

    public static class clientLoginData implements Serializable {
        private String cycoreId;
        private String displayName;
        private String id;
        private boolean needchange;
        private String schoolId;
        private String successType;
        private String token;
        private int userType;

        public String getId() {
            return this.id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSuccessType() {
            return this.successType;
        }

        public void setSuccessType(String successType) {
            this.successType = successType;
        }

        public String getToken() {
            return this.token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public boolean isNeedchange() {
            return this.needchange;
        }

        public void setNeedchange(boolean needchange) {
            this.needchange = needchange;
        }

        public String getSchoolId() {
            return this.schoolId;
        }

        public void setSchoolId(String schoolId) {
            this.schoolId = schoolId;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public int getUserType() {
            return this.userType;
        }

        public void setUserType(int userType) {
            this.userType = userType;
        }

        public String getCycoreId() {
            return this.cycoreId;
        }

        public void setCycoreId(String cycoreId) {
            this.cycoreId = cycoreId;
        }
    }
}
