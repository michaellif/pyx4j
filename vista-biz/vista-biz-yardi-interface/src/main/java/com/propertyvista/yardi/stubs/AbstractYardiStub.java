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
package com.propertyvista.yardi.stubs;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

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
import com.pyx4j.commons.ConverterUtils;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.essentials.j2se.HostConfig.ProxyConfig;
import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.biz.system.yardi.YardiConfigurationFacade;
import com.propertyvista.biz.system.yardi.YardiResponseException;
import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.SystemConfig;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.config.VistaSystemsSimulationConfig;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.TransactionLog;
import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.beans.Messages;

abstract class AbstractYardiStub implements ExternalInterfaceLoggingStub {

    private final static Logger log = LoggerFactory.getLogger(AbstractYardiStub.class);

    private final static Set<String> testSystemsUrl = new HashSet<String>();

    /**
     * Use to name transaction log files
     */
    private Action currentAction;

    private int requestCount = 0;

    private Long transactionId = 0l;

    protected long requestsTime;

    private final List<String> recordedTracastionsLogs = new ArrayList<String>();

    static {
        testSystemsUrl.add("http://yardi.birchwoodsoftwaregroup.com/");
        testSystemsUrl.add("http://yardi.birchwoodsoftwaregroup.com:8080/");
        testSystemsUrl.add("http://yardi2.birchwoodsoftwaregroup.com:8080/");
        testSystemsUrl.add("https://www.iyardiasp.com/8223");
        testSystemsUrl.add("https://testvyr.realstar.ca/");
        testSystemsUrl.add("http://192.168.50.10");
    }

    protected void init(Action currentAction) {
        this.transactionId = TransactionLog.getNextNumber();
        this.currentAction = currentAction;
    }

    protected void validateWriteAccess(PmcYardiCredential yc) {
        if (!VistaDeployment.isVistaProduction()) {
            validateWriteAccess(yc.serviceURLBase().getValue());
            validateWriteAccess(yc.residentTransactionsServiceURL().getValue());
            validateWriteAccess(yc.sysBatchServiceURL().getValue());
            validateWriteAccess(yc.maintenanceRequestsServiceURL().getValue());
            validateWriteAccess(yc.ilsGuestCardServiceURL().getValue());
            validateWriteAccess(yc.ilsGuestCard20ServiceURL().getValue());
        }
    }

    private void validateWriteAccess(String url) {
        if (CommonsStringUtils.isEmpty(url)) {
            return;
        }
        boolean allow = false;
        for (String urlBase : testSystemsUrl) {
            if (url.startsWith(urlBase)) {
                allow = true;
                break;
            }
        }
        if (!allow) {
            throw new AssertionError("Write access to " + url + " from test system is forbidden");
        }
    }

    private String logSec(String xml) {
        return xml.replaceAll("<ns(\\d):Password>.*</ns(\\d):Password>", "<ns$1:Password>***</ns$2:Password>");
    }

    protected void addMessageContextListener(final String prefix, Stub stub, final StringBuilder envelopeBuffer) {
        stub._getServiceClient().getAxisService().addMessageContextListener(new MessageContextListener() {

            protected long requestStartTime;

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
                    String fileName = TransactionLog.log(transactionId, fileName() + "-request", logSec(mc.getEnvelope().toString()), "xml");
                    if (fileName != null) {
                        recordedTracastionsLogs.add(fileName);
                        log.debug("{} Service Context in file {}", prefix, fileName);
                    } else {
                        log.debug("{} Service Context {}", prefix, logSec(mc.getEnvelope().toString()));
                    }
                    requestStartTime = System.currentTimeMillis();
                }
            }

            @Override
            public void attachEnvelopeEvent(MessageContext mc) {
                String fileName = TransactionLog.log(transactionId, fileName() + "-response", mc.getEnvelope().toString(), "xml");
                if (fileName != null) {
                    recordedTracastionsLogs.add(fileName);
                    log.debug("{} Envelope Event in file {}", prefix, fileName);
                } else {
                    log.debug("{} Envelope Event {}", prefix, mc.getEnvelope());
                }
                if (envelopeBuffer != null) {
                    envelopeBuffer.append(mc.getEnvelope());
                }

                if (VistaSystemsSimulationConfig.getConfiguration().yardiInterfaceNetworkSimulation().enabled().getValue(false)) {
                    log.warn("YardiInterfaceNetworkSimulation start delay {} milliseconds", VistaSystemsSimulationConfig.getConfiguration()
                            .yardiInterfaceNetworkSimulation().delay().getValue(1000));
                    try {
                        Thread.sleep(VistaSystemsSimulationConfig.getConfiguration().yardiInterfaceNetworkSimulation().delay().getValue(1000));
                    } catch (InterruptedException e) {
                        throw new Error(e);
                    }
                }

                long requestTime = TimeUtils.since(requestStartTime);
                ServerSideFactory.create(YardiConfigurationFacade.class).yardiRequestCompleted(requestTime);
                ServerSideFactory.create(YardiConfigurationFacade.class).incrementYardiTimer(requestTime);
            }

        });
    }

    @Override
    public void logRecordedTracastions() {
        log.warn("Yardi transaction recorded at {}", printableListOfRecordedTracastionFiles());
    }

    public String printableListOfRecordedTracastionFiles() {
        return ConverterUtils.convertStringCollection(recordedTracastionsLogs, "\n");
    }

    protected void setTransportOptions(Stub stub, PmcYardiCredential yc) {
        Options options = stub._getServiceClient().getOptions();
        if (options == null) {
            options = new Options();
        }
        options.setTimeOutInMilliSeconds(Consts.SEC2MSEC
                * ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).yardiConnectionTimeout());

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

        log.debug("open yardi connection to {} @ {}", yc.database().getStringView(), options.getTo().getAddress());
    }

    protected String serviceWithPath(PmcYardiCredential yc, String path) {
        if (yc.serviceURLBase().getValue().endsWith("/")) {
            return yc.serviceURLBase().getValue() + path;
        } else {
            return yc.serviceURLBase().getValue() + "/" + path;
        }
    }

    @SuppressWarnings("unchecked")
    protected <R> R ensureResult(String xml, Class<R> resultType) throws YardiServiceException {
        final List<String> errorTags = Arrays.asList("ErrorMessage", "ErrorMessages");
        try {
            final XMLStreamReader xsr = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(xml));
            Unmarshaller um = JAXBContext.newInstance(resultType).createUnmarshaller();
            um.setEventHandler(new ValidationEventHandler() {
                @Override
                public boolean handleEvent(ValidationEvent event) {
                    return !errorTags.contains(xsr.getLocalName());
                }
            });
            return (R) um.unmarshal(xsr);
        } catch (JAXBException e) {
            logRecordedTracastions();
            throw new YardiResponseException(xml);
        } catch (XMLStreamException e) {
            logRecordedTracastions();
            throw new YardiResponseException(xml);
        }
    }

    protected void ensureValid(String xml) throws YardiServiceException {
        try {
            Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
            if (messages.isError()) {
                logRecordedTracastions();
                throw new YardiResponseException(xml);
            } else {
                log.debug(messages.toString());
            }
        } catch (JAXBException e) {
            logRecordedTracastions();
            throw new YardiServiceException(e);
        }
    }
}
