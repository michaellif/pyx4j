/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.yardi;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.Stub;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.description.MessageContextListener;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.ws.ItfResidentTransactions2_0;
import com.yardi.ws.ItfResidentTransactions2_0Stub;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Consts;
import com.pyx4j.essentials.j2se.HostConfig.ProxyConfig;

import com.propertyvista.config.SystemConfig;
import com.propertyvista.yardi.YardiConstants.Action;

public class YardiClient {

    private final static Logger log = LoggerFactory.getLogger(YardiClient.class);

    /**
     * Use to name transaction log files
     */
    private Action currentAction;

    private int requestCount = 0;

    public Long transactionId = 0l;

    public YardiClient() {
    }

    private String getServeviceURL() {
        //TODO make it configurable
        return "https://www.iyardiasp.com/8223thirddev/webservices/itfresidenttransactions20.asmx";
    }

    public ItfResidentTransactions2_0 getResidentTransactionsService() throws AxisFault {
        ItfResidentTransactions2_0Stub serviceStub = new ItfResidentTransactions2_0Stub(getServeviceURL());
        addMessageContextListener("ResidentTransactions", serviceStub, null);
        setTransportOptions(serviceStub);
        return serviceStub;
    }

    private void addMessageContextListener(final String prefix, Stub stub, final StringBuilder envelopeBuffer) {
        stub._getServiceClient().getAxisService().addMessageContextListener(new MessageContextListener() {

            private String fileName() {
                StringBuilder b = new StringBuilder();
                if (currentAction != null) {
                    b.append(currentAction.name()).append('-');
                }
                b.append(requestCount).append('-').append(prefix);
                return b.toString();
            }

            @Override
            public void attachServiceContextEvent(ServiceContext sc, MessageContext mc) {
                if (mc.getEnvelope() != null) {
                    requestCount++;
                    TransactionLog.log(transactionId, fileName() + "-request", mc.getEnvelope().toString(), "xml");
                    log.debug(prefix + " Service Context", mc.getEnvelope());
                }
            }

            @Override
            public void attachEnvelopeEvent(MessageContext mc) {
                log.debug(prefix + " Envelope Event", mc.getEnvelope());
                TransactionLog.log(transactionId, fileName() + "-response", mc.getEnvelope().toString(), "xml");
                if (envelopeBuffer != null) {
                    envelopeBuffer.append(mc.getEnvelope());
                }
            }

        });
    }

    private void setTransportOptions(Stub stub) {
        Options options = stub._getServiceClient().getOptions();
        if (options == null) {
            options = new Options();
        }
        options.setTimeOutInMilliSeconds(Consts.MIN2MSEC * YardiConstants.TIMEOUT);

        options.setProperty(HTTPConstants.HTTP_PROTOCOL_VERSION, HTTPConstants.HEADER_PROTOCOL_11);

        ProxyConfig proxyConfig = SystemConfig.instance().getProxyConfig();
        if (proxyConfig != null) {
            HttpTransportProperties.ProxyProperties proxy = new HttpTransportProperties.ProxyProperties();
            proxy.setProxyName(proxyConfig.getHost());
            proxy.setProxyPort(proxyConfig.getPort());
            if (CommonsStringUtils.isStringSet(proxyConfig.getUser())) {
                proxy.setUserName(proxyConfig.getUser());
                proxy.setPassWord(proxyConfig.getPassword());
            }

            options.setProperty(HTTPConstants.PROXY, proxy);
        }

        stub._getServiceClient().setOptions(options);
    }

    public Action getCurrentAction() {
        return currentAction;
    }

    public void setCurrentAction(Action currentAction) {
        this.currentAction = currentAction;
    }
}
