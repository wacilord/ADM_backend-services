package com.icrown.backendapi.dtos;

/**
 * @author Adi
 */
public class Fun {
    public String getFunCode() {
        return funCode;
    }

    public void setFunCode(String funCode) {
        this.funCode = funCode;
    }

    public String getFunIcon() {
        return funIcon;
    }

    public void setFunIcon(String funIcon) {
        this.funIcon = funIcon;
    }

    public String getFunName() {
        return funName;
    }

    public void setFunName(String funName) {
        this.funName = funName;
    }

    public boolean isLevel1() {
        return level1;
    }

    public void setLevel1(boolean level1) {
        this.level1 = level1;
    }

    public boolean isLevel2() {
        return level2;
    }

    public void setLevel2(boolean level2) {
        this.level2 = level2;
    }

    public boolean isLevel3() {
        return level3;
    }

    public void setLevel3(boolean level3) {
        this.level3 = level3;
    }

    public String getFunType() {
        return funType;
    }

    public void setFunType(String funType) {
        this.funType = funType;
    }

    public String getFunHtml() {
        return funHtml;
    }

    public void setFunHtml(String funHtml) {
        this.funHtml = funHtml;
    }

    public String getFunApi() {
        return funApi;
    }

    public void setFunApi(String funApi) {
        this.funApi = funApi;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public boolean isLevelSubAccount() {
        return levelSubAccount;
    }

    public void setLevelSubAccount(boolean levelSubAccount) {
        this.levelSubAccount = levelSubAccount;
    }

    public int getFunSort() {
        return funSort;
    }

    public void setFunSort(int funSort) {
        this.funSort = funSort;
    }


    private String parent;
    private String funCode;
    private String funIcon;
    private String funName;
    private boolean level1;
    private boolean level2;
    private boolean level3;


    private boolean levelSubAccount;
    private String funType;
    private String funHtml;
    private String funApi;

    private int funSort;
}
