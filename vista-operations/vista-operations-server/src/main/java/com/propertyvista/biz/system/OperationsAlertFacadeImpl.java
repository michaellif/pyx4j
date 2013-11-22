/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-26
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.NamespaceManager;
import com.pyx4j.server.contexts.Visit;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.operations.domain.vista2pmc.OperationsAlert;
import com.propertyvista.server.common.security.VistaAntiBot;
import com.propertyvista.server.jobs.TaskRunner;

public class OperationsAlertFacadeImpl implements OperationsAlertFacade {

    private final static Logger log = LoggerFactory.getLogger("operations-alert");

    @Override
    public void record(final IEntity entity, String format, Object... args) {
        final String namespace = NamespaceManager.getNamespace();
        final String ip = getRequestRemoteAddr();
        final String details = SimpleMessageFormat.format(format, args);

        if ((args != null) && (args.length > 0) && (args[args.length - 1] instanceof Throwable)) {
            Throwable e = (Throwable) args[args.length - 1];
            log.warn("{}", details, e);
        } else {
            log.warn("{}", details);
        }

        TaskRunner.runAutonomousTransation(VistaNamespace.operationsNamespace, new Callable<Void>() {
            @Override
            public Void call() {
                OperationsAlert record = EntityFactory.create(OperationsAlert.class);
                record.handled().setValue(Boolean.FALSE);
                record.namespace().setValue(namespace);
                record.remoteAddr().setValue(ip);
                record.details().setValue(details);
                if (entity != null) {
                    record.entityId().setValue(entity.getPrimaryKey());
                    record.entityClass().setValue(entity.getEntityMeta().getEntityClass().getSimpleName());
                }
                record.user().setValue(getPrincipalPrimaryKey());
                Persistence.service().persist(record);
                Persistence.service().commit();

                if (!ServerSideFactory.create(VistaSystemFacade.class).isCommunicationsDisabled()) {
                    MailMessage m = new MailMessage();
                    m.setTo("support_team@propertyvista.com,support-payments@propertyvista.com");
                    m.setSender(ServerSideConfiguration.instance().getApplicationEmailSender());
                    m.setSubject("Vista Operations Alert");
                    m.setTextBody(details);
                    Mail.send(m);
                }
                return null;
            }
        });
    }

    private Key getPrincipalPrimaryKey() {
        Visit visit = Context.getVisit();
        if (visit == null) {
            return null;
        } else {
            UserVisit userVisit = visit.getUserVisit();
            if (userVisit == null) {
                return null;
            } else {
                return userVisit.getPrincipalPrimaryKey();
            }
        }
    }

    private String getRequestRemoteAddr() {
        if (Context.getRequest() == null) {
            return null;
        } else {
            Object ip = Context.getRequest().getAttribute(VistaAntiBot.REQUEST_IP_REQUEST_ATR);
            if (ip != null) {
                return ip.toString();
            } else {
                return Context.getRequestRemoteAddr();
            }
        }
    }

}
