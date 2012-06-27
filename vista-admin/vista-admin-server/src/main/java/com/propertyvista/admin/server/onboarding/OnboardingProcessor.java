/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.onboarding;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.validator.EntityValidator;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.admin.domain.pmc.PmcDnsName.DnsNameTarget;
import com.propertyvista.admin.server.onboarding.rh.OnboardingRequestHandlerFactory;
import com.propertyvista.admin.server.onboarding.rhf.RequestHandler;
import com.propertyvista.onboarding.RequestIO;
import com.propertyvista.onboarding.RequestMessageIO;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.onboarding.ResponseMessageIO;
import com.propertyvista.server.common.security.VistaAntiBot;

class OnboardingProcessor {

    private final static Logger log = LoggerFactory.getLogger(OnboardingProcessor.class);

    OnboardingProcessor() {
    }

    public Throwable isValid(RequestMessageIO message) {
        for (RequestIO request : message.requests()) {
            RequestIO entity = request.cast();
            EntityMeta em = entity.getEntityMeta();
            for (String memberName : em.getMemberNames()) {
                IObject<?> member = entity.getMember(memberName);
                switch (member.getMeta().getObjectClassType()) {
                case Entity:
                    try {
                        EntityValidator.validate((IEntity) member);
                    } catch (RuntimeException e) {
                        return new Error(member.getPath() + " " + e.getMessage());
                    }
                    break;
                case EntityList:
                case EntitySet:
                    @SuppressWarnings("unchecked")
                    Iterator<IEntity> lit = ((ICollection<IEntity, ?>) member).iterator();
                    while (lit.hasNext()) {
                        IEntity ent = lit.next();
                        try {
                            EntityValidator.validate(ent);
                        } catch (RuntimeException e) {
                            return new Error(ent.getPath() + " " + e.getMessage());
                        }
                    }
                    break;
                }
            }
        }
        return null;
    }

    private ResponseIO execute(RequestIO request) {
        Context.getRequest().setAttribute(VistaAntiBot.REQUEST_IP_REQUEST_ATR, request.requestRemoteAddr().getValue());
        RequestHandler<RequestIO> requestHandler = new OnboardingRequestHandlerFactory().createRequestHandler(request);
        if (requestHandler != null) {
            ResponseIO response = requestHandler.execute(request);
            if (!request.requestId().isNull()) {
                response.requestId().set(request.requestId());
            }
            return response;
        } else {
            ResponseIO response = EntityFactory.create(ResponseIO.class);
            if (!request.requestId().isNull()) {
                response.requestId().set(request.requestId());
            }
            response.success().setValue(Boolean.FALSE);
            response.errorMessage().setValue("Not implemented");
            return response;
        }
    }

    public ResponseMessageIO execute(RequestMessageIO message) {
        ResponseMessageIO rm = EntityFactory.create(ResponseMessageIO.class);
        if (!message.messageId().isNull()) {
            rm.messageId().set(message.messageId());
        }

        VistaAntiBot.setApiRequestDnsNameTarget(DnsNameTarget.vistaCrm);

        for (RequestIO request : message.requests()) {
            try {
                rm.responses().add(execute(request));
            } catch (Throwable e) {
                Persistence.service().rollback();
                log.error("Error", e);
                ResponseIO response = EntityFactory.create(ResponseIO.class);
                response.success().setValue(Boolean.FALSE);
                if (ApplicationMode.isDevelopment()) {
                    response.errorMessage().setValue(e.getMessage());
                }
                rm.responses().add(response);
            }
        }
        rm.status().setValue(ResponseMessageIO.StatusCode.OK);
        return rm;
    }

}
