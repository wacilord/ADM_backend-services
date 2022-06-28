package com.icrown.backendapi.services;

import com.icrown.backendapi.dtos.Fun;
import com.icrown.backendapi.dtos.FunType;
import com.icrown.backendapi.dtos.MenuListResponse;
import com.icrown.common.services.TranslateCommonService;
import com.icrown.gameapi.commons.responses.ResponseCode;
import com.icrown.gameapi.commons.utils.APIException;
import com.icrown.gameapi.daos.AgtFunctionDAO;
import com.icrown.gameapi.daos.FunctionRelationDAO;
import com.icrown.gameapi.models.FunctionModel;
import com.icrown.gameapi.models.FunctionRelationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author adi
 */
@Service
public class AgtFunctionService {
    @Autowired
    AgtFunctionDAO agtFunctionDAO;
    @Autowired
    FunctionRelationDAO functionRelationDAO;
    @Autowired
    TranslateCommonService translateService;

    public MenuListResponse getMenuList(String satGuid, String language) {
        MenuListResponse response = new MenuListResponse();
        List<FunType> funTypes = new ArrayList<>();
        List<Fun> funs = new ArrayList<>();


        SqlRowSet rs = agtFunctionDAO.getFunType();
        Map<String,String> translationMap = translateService.getTranslationMap(language);
        String funName;
        while (rs.next()) {
            FunType funType = new FunType();
            funType.setFunCode(rs.getString("FUN_Code"));
            funType.setFunIcon(rs.getString("FUN_ICON"));
            funName = translateService.fuzzyTranslationByTranlationMap(translationMap, rs.getString("FUN_Name"));
            funType.setFunName(funName);
            funType.setLevel1(rs.getBoolean("FUN_Level1"));
            funType.setLevel2(rs.getBoolean("FUN_Level2"));
            funType.setLevel3(rs.getBoolean("FUN_Level3"));
            funType.setLevelSubAccount(rs.getBoolean("FUN_LevelSubAccount"));
            funType.setFunSort(rs.getInt("FUN_Sort"));
            funTypes.add(funType);
        }

        rs = agtFunctionDAO.getFun();

        while (rs.next()) {
            Fun fun = new Fun();
            fun.setFunCode(rs.getString("FUN_Code"));
            fun.setFunIcon(rs.getString("FUN_ICON"));
            funName = translateService.fuzzyTranslationByTranlationMap(translationMap, rs.getString("FUN_Name"));
            fun.setFunName(funName);
            fun.setLevel1(rs.getBoolean("FUN_Level1"));
            fun.setLevel2(rs.getBoolean("FUN_Level2"));
            fun.setLevel3(rs.getBoolean("FUN_Level3"));
            fun.setLevelSubAccount(rs.getBoolean("FUN_LevelSubAccount"));
            fun.setParent(rs.getString("FUN_Parent"));
            fun.setFunHtml(rs.getString("FUN_Html"));
            fun.setFunType(rs.getString("FUN_Type"));
            fun.setFunApi(rs.getString("FUN_API"));
            fun.setFunSort(rs.getInt("FUN_Sort"));
            funs.add(fun);
        }

        List<FunctionRelationModel> functionRelationModels = functionRelationDAO.getFunctionRelationBySatGuid(satGuid);
        List<String> acls = functionRelationModels.stream().map(FunctionRelationModel::getFUN_Code).collect(Collectors.toList());

        response.setFunType(funTypes);
        response.setFun(funs);
        response.setAcl(acls);

        return response;
    }

    public void checkPermission(String url, String satGuid, String level, String type) {
        var list = agtFunctionDAO.getAllFunction();
        String funCode = "";
        if (level.equals("1")) {
            list = list.stream().filter(ls -> ls.isFUN_Level1()).collect(Collectors.toList());
        }
        if (level.equals("2")) {
            list = list.stream().filter(ls -> ls.isFUN_Level2()).collect(Collectors.toList());
        }
        if (level.equals("3")) {
            list = list.stream().filter(ls -> ls.isFUN_Level3()).collect(Collectors.toList());
        }

        boolean result = false;

        for (FunctionModel fn : list) {
            String api = fn.getFUN_API().trim();
            List<String> apis = Arrays.asList(api.split(","));
            if (apis.contains(url)) {
                funCode = fn.getFUN_Code();
                if (type.equals("1")) {
                    result = checkForAgentPermission(list, funCode, level);
                } else {
                    result = checkForSubAccountPermission(funCode, satGuid);
                }
                if (result) {
                    break;
                }
            }
        }

        if (!result) {
            throw new APIException(ResponseCode.BACKENDAPI_NO_PERMISSION);
        }

    }


    private boolean checkForAgentPermission(List<FunctionModel> list, String funCode, String level) {
        boolean isExist = false;
        switch (level) {
            case "1":
                isExist = list.stream().anyMatch(f -> f.isFUN_Level1() && f.getFUN_Code().equals(funCode));
                break;
            case "2":
                isExist = list.stream().anyMatch(f -> f.isFUN_Level2() && f.getFUN_Code().equals(funCode));
                break;
            case "3":
                isExist = list.stream().anyMatch(f -> f.isFUN_Level3() && f.getFUN_Code().equals(funCode));
                break;
            default:
                isExist = false;
        }
        return isExist;
    }

    private boolean checkForSubAccountPermission(String funCode, String satGuid) {
        var list = functionRelationDAO.getFunctionRelationBySatGuid(satGuid);
        return list.stream().anyMatch(f -> f.getFUN_Code().equals(funCode));
    }


}
