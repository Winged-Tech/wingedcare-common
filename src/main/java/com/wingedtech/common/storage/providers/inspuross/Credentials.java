package com.wingedtech.common.storage.providers.inspuross;

/**
 * @author wangdashuai
 * Title: Credentials
 * ProjectName inspursdkoss
 * Description: todo
 * @date 2019/10/4  16:55
 */
class Credentials {
     private  String accessKey;
    private  String secretAccessKey;

    public Credentials(String accessKey, String secretAccessKey) {
        this.accessKey = accessKey;
        this.secretAccessKey = secretAccessKey;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    public void setSecretAccessKey(String secretAccessKey) {
        this.secretAccessKey = secretAccessKey;
    }
}
