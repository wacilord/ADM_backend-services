package com.icrown.backendapi.dtos;

import com.icrown.gameapi.models.AGT_MerchantModel;
import com.icrown.gameapi.models.AgentModel;
import com.icrown.gameapi.models.SessionModel;
import com.icrown.gameapi.models.SubAccountModel;

/**
 * @author Frank
 */
public class GetAgentInfoResponse {
    private SessionModel sessionModel;
    private SubAccountModel subAccountModel;
    private AgentModel agentModel;
    private AGT_MerchantModel merchantModel;

    public SessionModel getSessionModel() {
        return sessionModel;
    }

    public void setSessionModel(SessionModel sessionModel) {
        this.sessionModel = sessionModel;
    }

    public SubAccountModel getSubAccountModel() {
        return subAccountModel;
    }

    public void setSubAccountModel(SubAccountModel subAccountModel) {
        this.subAccountModel = subAccountModel;
    }

    public AgentModel getAgentModel() {
        return agentModel;
    }

    public void setAgentModel(AgentModel agentModel) {
        this.agentModel = agentModel;
    }

    public AGT_MerchantModel getMerchantModel() {
        return merchantModel;
    }

    public void setMerchantModel(AGT_MerchantModel merchantModel) {
        this.merchantModel = merchantModel;
    }
}
