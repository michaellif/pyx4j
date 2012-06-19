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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.security.AuditRecord;
import com.propertyvista.admin.domain.security.AuditRecord.EventType;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.security.AbstractUser;
import com.propertyvista.server.jobs.TaskRunner;

public class AuditFacadeImpl implements AuditFacade {

    @Override
    public void login() {
        record(EventType.Login, null);
        Persistence.service().commit();
    }

    @Override
    public void loginFailed(final AbstractUser user) {
        final String namespace = NamespaceManager.getNamespace();
        final String ip = Context.getRequestRemoteAddr();
        TaskRunner.runAutonomousTransation(VistaNamespace.adminNamespace, new Callable<Void>() {
            @Override
            public Void call() {
                AuditRecord record = EntityFactory.create(AuditRecord.class);
                record.namespace().setValue(namespace);
                record.remoteAddr().setValue(ip);
                record.event().setValue(EventType.LoginFailed);
                record.user().setValue(user.getPrimaryKey());
                Persistence.service().persist(record);
                Persistence.service().commit();
                return null;
            }
        });
    }

    @Override
    public void created(IEntity entity) {
        record(EventType.Create, entity);
    }

    @Override
    public void updated(IEntity entity) {
        record(EventType.Update, entity);
    }

    private void record(final EventType event, final IEntity entity) {
        final String namespace = NamespaceManager.getNamespace();
        final String ip = Context.getRequestRemoteAddr();
        TaskRunner.runAutonomousTransation(VistaNamespace.adminNamespace, new Callable<Void>() {
            @Override
            public Void call() {
                AuditRecord record = EntityFactory.create(AuditRecord.class);
                record.namespace().setValue(namespace);
                record.remoteAddr().setValue(ip);
                record.event().setValue(event);
                if (entity != null) {
                    record.entityId().setValue(entity.getPrimaryKey());
                }
                record.user().setValue(Context.getVisit().getUserVisit().getPrincipalPrimaryKey());
                Persistence.service().persist(record);
                Persistence.service().commit();
                return null;
            }
        });
    }

}
