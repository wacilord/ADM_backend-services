package com.icrown.backendapi.services;

import com.icrown.common.services.AgentCommonService;
import com.icrown.common.services.TranslateCommonService;
import com.icrown.gameapi.daos.AgentDAO;
import com.icrown.gameapi.daos.AgtFunctionDAO;
import com.icrown.gameapi.daos.FunctionRelationDAO;
import com.icrown.gameapi.models.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;

import java.util.*;

import org.mockito.Mockito;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.fail;
@SpringBootTest(classes = AgtFunctionServiceTest.class)
@ComponentScan(basePackages = "com.icrown")
@EnableAutoConfiguration
public class AgtFunctionServiceTest {
    @Autowired
    AgtFunctionService agtFunctionService;
    @MockBean
    AgtFunctionDAO agtFunctionDAO;
    @MockBean
    FunctionRelationDAO functionRelationDAO;
    @MockBean
    TranslateCommonService translateService;;
    @Test
    public void getMenuList(){
        var resultSetMock1 = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock1.next()).thenReturn(true).thenReturn(false);
        var resultSetMock2 = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock2.next()).thenReturn(true).thenReturn(false);
        //var resultSetMock3 = Mockito.mock(SqlRowSet.class);
        //Mockito.when(resultSetMock3.next()).thenReturn(true).thenReturn(false);
        List<FunctionRelationModel> list = new ArrayList<>();

        when(agtFunctionDAO.getFunType()).thenReturn(resultSetMock1);
        when(agtFunctionDAO.getFun()).thenReturn(resultSetMock2);

        when(functionRelationDAO.getFunctionRelationBySatGuid(anyString())).thenReturn(list);
        when(translateService.getTranslationMap(anyString())).thenReturn(new HashMap<>());
        when(translateService.fuzzyTranslationByTranlationMap(any(), anyString())).thenReturn("FUN_Name");
        var response = agtFunctionService.getMenuList("satGuid", "zh-CN");
        assertTrue(response.getFunType().size() == 1);
        assertTrue(response.getFun().size() == 1);
        assertTrue(response.getAcl().size() == 0);

    }

    @Test
    public void checkPermission(){
        List<FunctionModel> list = new ArrayList<>();
        FunctionModel model = new FunctionModel();
        model.setFUN_API("backend/test");
        model.setFUN_Code("code1");
        model.setFUN_Level3(true);
        list.add(model);
        when(agtFunctionDAO.getAllFunction()).thenReturn(list);

        FunctionRelationModel model2 = new FunctionRelationModel();
        model2.setFUN_Code("code1");
        model2.setSAT_GUID("satGuid");
        List<FunctionRelationModel> list2 = new ArrayList<>();
        list2.add(model2);
        when(functionRelationDAO.getFunctionRelationBySatGuid(anyString())).thenReturn(list2);
        try{
            agtFunctionService.checkPermission("backend/test","satGuid","3","1");
            agtFunctionService.checkPermission("backend/test","satGuid","3","0");
        }catch (Exception ex){
            fail("fail");
        }
    }
}
