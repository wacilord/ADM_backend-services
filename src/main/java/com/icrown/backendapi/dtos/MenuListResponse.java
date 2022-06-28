package com.icrown.backendapi.dtos;

import java.util.List;

/**
 * @author Adi
 */
public class MenuListResponse {

    public List<FunType> getFunType() {
        return funType;
    }

    public void setFunType(List<FunType> funType) {
        this.funType = funType;
    }

    public List<Fun> getFun() {
        return fun;
    }

    public void setFun(List<Fun> fun) {
        this.fun = fun;
    }

    public List<String> getAcl() {
        return acl;
    }

    public void setAcl(List<String> acl) {
        this.acl = acl;
    }

    private List<FunType> funType;
    private List<Fun> fun;
    private List<String> acl;

}
