package com.icrown.backendapi.services;

import com.icrown.gameapi.commons.responses.ResponseCode;
import com.icrown.gameapi.commons.utils.APIException;
import com.icrown.gameapi.daos.SessionDAO;
import com.icrown.gameapi.models.SessionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Frank
 */
@Service
public class SessionService {
    @Autowired
    SessionDAO sessionDAO;
    public SessionModel getSessionByToken(String token){
        var optSession = sessionDAO.getSessionByToken(token);
        return optSession.orElseThrow(()->{
           throw new APIException(ResponseCode.BACKENDAPI_TOKEN_ERROR);
        });
    }

    public void expiredSessionByToken(String token, Date expiredDateTime) {
        if(!sessionDAO.expiredSessionByToken(token, expiredDateTime)){
            throw new APIException(ResponseCode.BACKENDAPI_TOKEN_ERROR);
        }
    }

}
