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

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.NamespaceManager;
import com.pyx4j.server.contexts.Visit;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.AuditRecordEventType;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.operations.domain.security.AuditRecord;
import com.propertyvista.server.common.security.VistaAntiBot;
import com.propertyvista.server.common.security.VistaContext;
import com.propertyvista.server.jobs.TaskRunner;

public class AuditFacadeImpl implements AuditFacade {

    private static final int details_length = EntityFactory.getEntityPrototype(AuditRecord.class).details().getMeta().getLength() - 3;

    @Override
    public void login(VistaApplication application) {
        record(AuditRecordEventType.Login, null, null);
    }

    @Override
    public void logout(VistaApplication application) {
        record(AuditRecordEventType.Logout, null, null);
    }

    @Override
    public void sessionExpiration(String namespace, VistaApplication application, AbstractUser user, String sessionId) {
        AuditRecord record = EntityFactory.create(AuditRecord.class);
        record.event().setValue(AuditRecordEventType.SessionExpiration);
        record.namespace().setValue(namespace);
        record.app().setValue(application);
        record.sessionId().setValue(sessionId);
        setPrincipalUser(record, user);
        record(record);
    }

    @Override
    public void loginFailed(VistaApplication application, AbstractUser user) {
        AuditRecord record = EntityFactory.create(AuditRecord.class);
        record.event().setValue(AuditRecordEventType.LoginFailed);
        record.namespace().setValue(NamespaceManager.getNamespace());
        record.app().setValue(application);
        record.remoteAddr().setValue(getRequestRemoteAddr());
        setPrincipalUser(record, user);
        record(record);
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
        AuditRecord record = EntityFactory.create(AuditRecord.class);
        record.namespace().setValue(NamespaceManager.getNamespace());
        record.remoteAddr().setValue(getRequestRemoteAddr());
        record.sessionId().setValue(Context.getSessionId());
        record.event().setValue(eventType);
        record.details().setValue((format == null) ? null : truncDetails(SimpleMessageFormat.format(format, args)));
        if (entity != null) {
            record.entityId().setValue(entity.getPrimaryKey());
            record.entityClass().setValue(entity.getEntityMeta().getEntityClass().getSimpleName());
        }
        setPrincipalUser(record, getPrincipal());
        record.app().setValue(VistaApplication.getVistaApplication(SecurityController.getBehaviors()));
        record(record);
    }

    private static String truncDetails(String details) {
        if ((details != null) && (details.length() > details_length)) {
            return details.substring(0, details_length) + "...";
        } else {
            return details;
        }
    }

    private void setPrincipalUser(AuditRecord record, AbstractUser principal) {
        if (principal != null) {
            record.user().setValue(principal.getPrimaryKey());
            record.userType().setValue(VistaContext.getVistaUserType(principal));
        }
    }

    private void record(final AuditRecord record) {

        record.worldTime().setValue(WorldDateManager.getWorldTime());

        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                if (!record.namespace().isNull() && !record.namespace().getValue().equals(VistaNamespace.operationsNamespace)) {
                    NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
                    EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
                    criteria.add(PropertyCriterion.eq(criteria.proto().namespace(), record.namespace()));
                    record.pmc().set(Persistence.service().retrieve(criteria));
                }

                new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.Web).execute(new Executable<Void, RuntimeException>() {
                    @Override
                    public Void execute() throws RuntimeException {
                        Persistence.service().persist(record);
                        return null;
                    }
                });
                return null;
            }
        });
    }

    private AbstractUser getPrincipal() {
        Visit visit = Context.getVisit();
        if (visit == null) {
            return null;
        } else {
            UserVisit userVisit = visit.getUserVisit();
            if (userVisit == null) {
                return null;
            } else {
                return VistaContext.getUserFromVisit(visit);
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
