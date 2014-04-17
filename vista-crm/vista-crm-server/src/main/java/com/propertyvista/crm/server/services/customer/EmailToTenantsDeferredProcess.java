/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

@SuppressWarnings("serial")
public class EmailToTenantsDeferredProcess extends AbstractDeferredProcess {

    private final I18n i18n = I18n.get(EmailToTenantsDeferredProcess.class);

    private final EmailTemplateType emailType;

    private int sentCount = 0;

    @SuppressWarnings("rawtypes")
    private final EntityQueryCriteria<LeaseTermParticipant> criteria;

    public EmailToTenantsDeferredProcess(EmailTemplateType emailType, @SuppressWarnings("rawtypes") EntityQueryCriteria<LeaseTermParticipant> criteria) {
        this.emailType = emailType;
        this.criteria = criteria;
    }

    @Override
    public void execute() {
        criteria.eq(criteria.proto().leaseTermV().holder(), criteria.proto().leaseTermV().holder().lease().currentTerm());
        criteria.isCurrent(criteria.proto().leaseTermV());
        criteria.eq(criteria.proto().leaseTermV().holder().lease().unit().building().suspended(), false);

        progress.progressMaximum.addAndGet(Persistence.service().count(criteria));

        @SuppressWarnings("rawtypes")
        ICursorIterator<LeaseTermParticipant> participants = Persistence.secureQuery(null, criteria, AttachLevel.Attached);
        try {
            while (participants.hasNext()) {
                progress.progress.addAndGet(1);

                final LeaseTermParticipant<?> participant = participants.next();

                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {
                    @Override
                    public Void execute() {

                        switch (emailType) {
                        case TenantInvitation:
                            if (participant.isInstanceOf(LeaseTermTenant.class)) {
                                LeaseTermTenant tenant = participant.cast();
                                ServerSideFactory.create(CommunicationFacade.class).sendTenantInvitation(tenant);
                                sentCount++;
                            }
                        default:
                            break;
                        }

                        return null;
                    }
                });

                if (canceled) {
                    break;
                }

            }
        } finally {
            participants.close();
        }

        completed = true;
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse status = super.status();
        if (completed || canceled) {
            status.setMessage(i18n.tr("Email was sent to {0} tenant(s)", sentCount));
        }
        return status;
    }
}
