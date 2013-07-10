/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-19
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system;

import java.util.concurrent.Callable;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.NamespaceManager;
import com.pyx4j.server.contexts.Visit;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.security.AuditRecordEventType;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.operations.domain.security.AuditRecord;
import com.propertyvista.server.common.security.VistaAntiBot;
import com.propertyvista.server.jobs.TaskRunner;

public class AuditFacadeImpl implements AuditFacade {

    @Override
    public void login() {
        record(AuditRecordEventType.Login, null, null);
        Persistence.service().commit();
    }

    @Override
    public void loginFailed(final AbstractUser user) {
        final String namespace = NamespaceManager.getNamespace();
        final String ip = getRequestRemoteAddr();
        TaskRunner.runAutonomousTransation(VistaNamespace.operationsNamespace, new Callable<Void>() {
            @Override
            public Void call() {
                AuditRecord record = EntityFactory.create(AuditRecord.class);
                record.namespace().setValue(namespace);
                record.remoteAddr().setValue(ip);
                record.event().setValue(AuditRecordEventType.LoginFailed);
                record.user().setValue(user.getPrimaryKey());
                record.worldTime().setValue(WorldDateManager.getWorldTime());
                Persistence.service().persist(record);
                Persistence.service().commit();
                return null;
            }
        });
    }

    @Override
    public void credentialsUpdated(AbstractUser user) {
        record(AuditRecordEventType.CredentialUpdate, user, null);
    }

    @Override
    public void created(IEntity entity) {
        record(AuditRecordEventType.Create, entity, null);
    }

    @Override
    public void updated(IEntity entity, String changes) {
        record(AuditRecordEventType.Update, entity, "{0}", changes);
    }

    @Override
    public void read(IEntity entity) {
        record(AuditRecordEventType.Read, entity, null);
    }

    @Override
    public void info(String format, Object... args) {
        record(AuditRecordEventType.Info, null, format, args);
    }

    @Override
    public void record(final AuditRecordEventType eventType, final IEntity entity, String format, Object... args) {
        final String namespace = NamespaceManager.getNamespace();
        final String ip = getRequestRemoteAddr();
        final String details = (format == null) ? null : SimpleMessageFormat.format(format, args);
        TaskRunner.runAutonomousTransation(VistaNamespace.operationsNamespace, new Callable<Void>() {
            @Override
            public Void call() {
                AuditRecord record = EntityFactory.create(AuditRecord.class);
                record.namespace().setValue(namespace);
                record.remoteAddr().setValue(ip);
                record.event().setValue(eventType);
                record.details().setValue(details);
                if (entity != null) {
                    record.entityId().setValue(entity.getPrimaryKey());
                    record.entityClass().setValue(entity.getEntityMeta().getEntityClass().getSimpleName());
                }
                record.user().setValue(getPrincipalPrimaryKey());
                record.worldTime().setValue(WorldDateManager.getWorldTime());
                Persistence.service().persist(record);
                Persistence.service().commit();
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
