/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 17, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.axis2.AxisFault;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.ws.operations.GetPropertyConfigurations;
import com.yardi.ws.operations.GetPropertyConfigurationsResponse;
import com.yardi.ws.operations.Ping;
import com.yardi.ws.operations.PingResponse;

import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.YardiClient;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.bean.Messages;
import com.propertyvista.yardi.bean.Properties;

public class YardiAbstarctService {

    private final static Logger log = LoggerFactory.getLogger(YardiAbstarctService.class);

    /**
     * The Ping function accepts no parameters, but will return the
     * assembly name of the function being called. Use it to test
     * connectivity.
     * 
     * @throws RemoteException
     * @throws AxisFault
     */
    public static void ping(YardiClient c) throws AxisFault, RemoteException {
        c.transactionId++;
        c.setCurrentAction(Action.ping);

        Ping ping = new Ping();
        PingResponse pr = c.getResidentTransactionsService().ping(ping);
        log.info("Connection to Yardi works: {}", pr.getPingResult());
    }

    protected List<String> getPropertyCodes(YardiClient client, PmcYardiCredential yc) throws YardiServiceException {
        List<String> propertyCodes = new ArrayList<String>();
        Properties properties = getPropertyConfigurations(client, yc);
        for (com.propertyvista.yardi.bean.Property property : properties.getProperties()) {
            if (StringUtils.isNotEmpty(property.getCode())) {
                propertyCodes.add(property.getCode());
            }
        }
        return propertyCodes;
    }

    /**
     * Allows export of the Property Configuration with the
     * Database. The Unique Interface Entity name is needed in order
     * to return the Property ID's the third-party has access to.
     * 
     * This is just a list of properties with not much information in it:
     * Property has code, address, marketing name, accounts payable and accounts receivable
     * 
     * @throws RemoteException
     * @throws AxisFault
     * @throws JAXBException
     */

    protected Properties getPropertyConfigurations(YardiClient c, PmcYardiCredential yc) throws YardiServiceException {

        try {
            c.transactionId++;
            c.setCurrentAction(Action.GetPropertyConfigurations);

            GetPropertyConfigurations l = new GetPropertyConfigurations();
            l.setUserName(yc.username().getValue());
            l.setPassword(yc.credential().getValue());
            l.setServerName(yc.serverName().getValue());
            l.setDatabase(yc.database().getValue());
            l.setPlatform(yc.platform().getValue().name());
            l.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
            GetPropertyConfigurationsResponse response = c.getResidentTransactionsService().getPropertyConfigurations(l);
            if (response.getGetPropertyConfigurationsResult() == null) {
                throw new Error("Received response is null");
            }

            String xml = response.getGetPropertyConfigurationsResult().getExtraElement().toString();

            if (log.isDebugEnabled()) {
                log.debug("GetPropertyConfigurations Result: {}", xml);
            }

            if (Messages.isMessageResponse(xml)) {
                Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
                if (messages.isError()) {
                    throw new YardiServiceException(messages.toString());
                } else {
                    log.info(messages.toString());
                }
            }

            Properties properties = MarshallUtil.unmarshal(Properties.class, xml);

            if (log.isDebugEnabled()) {
                log.debug("\n--- GetPropertyConfigurations ---\n{}\n", properties);
            }

            return properties;

        } catch (JAXBException e) {
            throw new Error(e);
        } catch (RemoteException e) {
            throw new Error(e);
        }
    }

}
