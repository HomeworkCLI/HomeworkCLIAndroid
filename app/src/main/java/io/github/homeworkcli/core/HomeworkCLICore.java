package io.github.homeworkcli.core;

import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.Enumeration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import io.github.homeworkcli.models.clientLoginModel;
import io.github.homeworkcli.models.clientLoginModel.clientLoginData;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HomeworkCLICore {
    private OkHttpClient httpClient = new OkHttpClient();

    private String appVersion = "v3.8.9.4";

    private String baseUrl = UrlFactory.baseUrl;
    private String cycoreId = "";
    private String displayName = "";
    private String mac = getMacFromHardware();
    private String machine = Build.BRAND + "_" + Build.MODEL;
    private String osVersion = Build.VERSION.RELEASE;
    private String schoolId = "";
    private String token = "";
    private String userid = "";

    public HomeworkCLICore() { }

    public HomeworkCLICore(String userid) {
        this.userid = userid;
    }

    public HomeworkCLICore(String userid, String token) {
        this.userid = userid;
        this.token = token;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public String getCycoreId() {
        return cycoreId;
    }

    public void setCycoreId(String cycoreId) {
        this.cycoreId = cycoreId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public HomeworkCLICore(clientLoginModel clientLoginModel) {
        clientLoginData clientLoginData = clientLoginModel.getData();
        this.cycoreId = clientLoginModel.getData().getCycoreId();
        this.displayName = clientLoginModel.getData().getDisplayName();
        this.schoolId = clientLoginModel.getData().getSchoolId();
        this.token = clientLoginModel.getData().getToken();
        this.userid = clientLoginModel.getData().getId();
    }

    public Call clientLogin(String username, String password, Boolean isforce, int usertype) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        String encryptedPassword;
        Cipher cipher = Cipher.getInstance("/DES/CBC/PKCS5Padding");
        cipher.init(1, SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec("jevicjob".getBytes(StandardCharsets.UTF_8))), new IvParameterSpec("jevicjob".getBytes(StandardCharsets.UTF_8)));
        encryptedPassword = Base64.encodeToString(cipher.doFinal(password.getBytes(StandardCharsets.UTF_8)), 0);
        return post(UrlFactory.clientLogin,
                new FormBody.Builder()
                        .add("loginvalue", username)
                        .add("pwd", encryptedPassword)
                        .add("device", "mobile")
                        .add("isforce", isforce ? "true" : "false")
                        .add("usertype", String.valueOf(usertype))
                        .add("appVersion", this.appVersion));
    }

    public Call shareDoc(String userfor, String classids, String docid, String studentids) {
        return post(this.baseUrl + "jcservice/Courseware/shareDoc",
                new FormBody.Builder()
                        .add("userfor", userfor)
                        .add("classids", classids)
                        .add("docid", docid)
                        .add("studentids", studentids)
                        .add("userid", this.userid)
                        .add("appVersion", this.appVersion));
    }

    public Call saveDocNew(DocInfo docInfo) {
        return post(this.baseUrl + "jcservice/Doc/saveDocNew",
                new FormBody.Builder()
                        .add("docInfoJson", new Gson().toJson(docInfo))
                        .add("appVersion", this.appVersion));
    }

    public Call getOssSecretKeyNew() throws NoSuchAlgorithmException {
        long timestamp = System.currentTimeMillis();
        MessageDigest md5 = null;
        md5 = MessageDigest.getInstance("MD5");
        byte[] bytes = md5.digest((this.userid + "appId" + timestamp + "456FDB96EBB94035A926827139EA4216").getBytes());
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            String temp = Integer.toHexString(b & 0xff);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            result.append(temp);
        }
        return postNoEncrypt(UrlFactory.getOssSecretKeyNew,
                new FormBody.Builder()
                        .add("userId", this.userid)
                        .add("timestamp", String.valueOf(timestamp))
                        .add("appId", "appId")
                        .add("sign", result.toString()));
    }

    private Call post(String url, FormBody.Builder data) {
        return this.httpClient.newCall(new Request.Builder().url(url).post(encryptFormData(data)).build());
    }

    private Call postNoEncrypt(String url, FormBody.Builder data) {
        return this.httpClient.newCall(new Request.Builder().url(url).post(data.build()).build());
    }

    private FormBody encryptFormData(FormBody.Builder form) {
        form.add("safeid", this.userid);
        form.add("safetime", String.valueOf(System.currentTimeMillis()));
        form.add("mac", this.mac);
        form.add("machine", this.machine);
        form.add("platform", "Android");
        form.add("osVersion", this.osVersion);
        form.add("apiVersion", "1.0");
        if (!this.token.isEmpty()) {
            form.add("token", this.token);
        }
        return form.build();
    }

    private static String getMacFromHardware() {
        try {
            Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface)enumeration.nextElement();
                byte[] arrayOfByte = networkInterface.getHardwareAddress();
                if (arrayOfByte == null || arrayOfByte.length == 0) {
                    continue;
                }

                StringBuilder StringBuilder = new StringBuilder();
                for (byte b : arrayOfByte) {
                    StringBuilder.append(String.format("%02X:", b));
                }
                if (StringBuilder.length() > 0) {
                    StringBuilder.deleteCharAt(StringBuilder.length() - 1);
                }
                String str = StringBuilder.toString();
                if (networkInterface.getName().equals("wlan0")) {
                    return str;
                }
            }
        } catch (SocketException socketException) {
            return "03:03:03:03:03:03";
        }
        return "03:03:03:03:03:03";
    }

    public static class DocInfo
    {
        private String title;
        private String doctype;
        private long docsize;
        private String dir;
        private String key;
        private String md5code;
        private String guid;
        private boolean isconverth5;
        private boolean ispublish;
        private String agent;
        private String iflyknowledge;
        private String bankname;
        private String category1;
        private String category2;
        private String categoryid;
        private String categoryname;
        private boolean isschool;
        private String creator;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDoctype() {
            return doctype;
        }

        public void setDoctype(String doctype) {
            this.doctype = doctype;
        }

        public long getDocsize() {
            return docsize;
        }

        public void setDocsize(long docsize) {
            this.docsize = docsize;
        }

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getMd5code() {
            return md5code;
        }

        public void setMd5code(String md5code) {
            this.md5code = md5code;
        }

        public String getGuid() {
            return guid;
        }

        public void setGuid(String guid) {
            this.guid = guid;
        }

        public boolean isIsconverth5() {
            return isconverth5;
        }

        public void setIsconverth5(boolean isconverth5) {
            this.isconverth5 = isconverth5;
        }

        public boolean isIspublish() {
            return ispublish;
        }

        public void setIspublish(boolean ispublish) {
            this.ispublish = ispublish;
        }

        public String getAgent() {
            return agent;
        }

        public void setAgent(String agent) {
            this.agent = agent;
        }

        public String getIflyknowledge() {
            return iflyknowledge;
        }

        public void setIflyknowledge(String iflyknowledge) {
            this.iflyknowledge = iflyknowledge;
        }

        public String getBankname() {
            return bankname;
        }

        public void setBankname(String bankname) {
            this.bankname = bankname;
        }

        public String getCategory1() {
            return category1;
        }

        public void setCategory1(String category1) {
            this.category1 = category1;
        }

        public String getCategory2() {
            return category2;
        }

        public void setCategory2(String category2) {
            this.category2 = category2;
        }

        public String getCategoryid() {
            return categoryid;
        }

        public void setCategoryid(String categoryid) {
            this.categoryid = categoryid;
        }

        public String getCategoryname() {
            return categoryname;
        }

        public void setCategoryname(String categoryname) {
            this.categoryname = categoryname;
        }

        public boolean isIsschool() {
            return isschool;
        }

        public void setIsschool(boolean isschool) {
            this.isschool = isschool;
        }

        public String getCreator() {
            return creator;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }
    }
}
