/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 6, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.stubs;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.biz.system.yardi.YardiPropertyNoAccessException;
import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.YardiInterface;
import com.propertyvista.yardi.beans.Messages;
import com.propertyvista.yardi.services.YardiHandledErrorMessages;

public class YardiAbstractStubProxy {

    private final static Logger log = LoggerFactory.getLogger(YardiResidentTransactionsStubProxy.class);

    public static final String GENERIC_YARDI_ERROR = "Unexpected Yardi response";

    private DataErrorHandler dataErrorHandler;

    private MessageErrorHandler messageErrorHandler;

    final MessageErrorHandler noPropertyAccessHandler = new MessageErrorHandler() {
        @Override
        public boolean handle(Messages messages) throws YardiServiceException {
            if (messages.hasErrorMessage(YardiHandledErrorMessages.errorMessage_NoAccess)) {
                throw new YardiPropertyNoAccessException(messages.getErrorMessage().getValue());
            }
            return false;
        }
    };

    void setDataErrorHandler(DataErrorHandler dataErrorHandler) {
        this.dataErrorHandler = dataErrorHandler;
    }

    void setMessageErrorHandler(MessageErrorHandler messageErrorHandler) {
        this.messageErrorHandler = messageErrorHandler;
    }

    void validateResponseXml(String xml) throws YardiServiceException {
        if (Messages.isMessageResponse(xml)) {
            try {
                Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
                if (messages.isError()) {
                    YardiLicense.handleVendorLicenseError(messages);
                    if (messageErrorHandler == null || !messageErrorHandler.handle(messages)) {
                        throw new YardiServiceException(messages.toString());
                    }
                } else {
                    log.info(messages.toString());
                }
            } catch (JAXBException xe) {
                throw new YardiServiceException(GENERIC_YARDI_ERROR, xe);
            }
        } else if (dataErrorHandler != null) {
            dataErrorHandler.handle(xml);
        }
    }

    <T extends YardiInterface> T getStubInstance(Class<T> ifClass, PmcYardiCredential yc) {
        // TODO - yc may have stub version info that should be passed to the factory
        String version = null;
        T stub = YardiStubFactory.create(ifClass, version);
        return stub;
    }

    public interface MessageErrorHandler {
        boolean handle(Messages messages) throws YardiServiceException;
    }

    public interface DataErrorHandler {
        void handle(String xml) throws YardiServiceException;
    }
}
