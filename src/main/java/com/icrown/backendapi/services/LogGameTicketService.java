package com.icrown.backendapi.services;

import com.icrown.gameapi.daos.LogGameTicketDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * @author Frank
 */
@Service
public class LogGameTicketService {
    @Autowired
    LogGameTicketDAO logGameTicketDAO;

    public Optional<Date> getLastOnlineTime(String plyGuid){
        return logGameTicketDAO.lastOnlineTime(plyGuid);
    }

    public boolean getPlayerIsOnline(String plyGuid){
        return logGameTicketDAO.playerIsOnline(plyGuid);
    }
}
