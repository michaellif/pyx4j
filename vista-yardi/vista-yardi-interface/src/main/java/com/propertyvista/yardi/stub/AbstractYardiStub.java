/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi.stub;

import org.apache.axis2.client.Options;
import org.apache.axis2.client.Stub;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.description.MessageContextListener;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Consts;
import com.pyx4j.essentials.j2se.HostConfig.ProxyConfig;

import com.propertyvista.config.SystemConfig;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.TransactionLog;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.YardiConstants.Action;

public class AbstractYardiStub {

    private final static Logger log = LoggerFactory.getLogger(AbstractYardiStub.class);

    /**
     * Use to name transaction log files
     */
    private Action currentAction;

    private int requestCount = 0;

    private Long transactionId = 0l;

    protected void init(Action currentAction) {
        this.transactionId = TransactionLog.getNextNumber();
        this.currentAction = currentAction;
    }

    protected void addMessageContextListener(final String prefix, Stub stub, final StringBuilder envelopeBuffer) {
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

    protected void setTransportOptions(Stub stub) {
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

    protected String serviceWithPath(PmcYardiCredential yc, String path) {
        if (yc.serviceURLBase().getValue().endsWith("/")) {
            return yc.serviceURLBase().getValue() + path;
        } else {
            return yc.serviceURLBase().getValue() + "/" + path;
        }
    }
}
