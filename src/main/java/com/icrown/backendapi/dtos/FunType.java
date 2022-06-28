package com.icrown.backendapi.dtos;

/**
 * @author Adi
 */
public class FunType {
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

    private String funCode;
    private String funIcon;
    private String funName;
    private boolean level1;
    private boolean level2;
    private boolean level3;


    private int funSort;

    private boolean levelSubAccount;
}
