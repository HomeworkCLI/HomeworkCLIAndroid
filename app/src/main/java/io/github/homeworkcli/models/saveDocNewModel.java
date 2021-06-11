package io.github.homeworkcli.models;

public class saveDocNewModel extends BaseModel{
    private DataModel data;

    public DataModel getData() {
        return this.data;
    }

    public void setData(DataModel data) {
        this.data = data;
    }

    public static class DataModel {
        private String docid;
        private String fileid;

        public String getDocid() {
            return this.docid;
        }

        public void setDocid(String docid) {
            this.docid = docid;
        }

        public String getFileid() {
            return this.fileid;
        }

        public void setFileid(String fileid) {
            this.fileid = fileid;
        }
    }
}
