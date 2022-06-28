package com.icrown.backendapi.dtos;

import java.util.List;
import java.util.Map;

public class OrgTree {
    private String agent3;
    private Map<String,String> tree;

    public String getAgent3() {
        return agent3;
    }

    public void setAgent3(String agent3) {
        this.agent3 = agent3;
    }

    public Map<String, String> getTree() {
        return tree;
    }

    public void setTree(Map<String, String> tree) {
        this.tree = tree;
    }
}
