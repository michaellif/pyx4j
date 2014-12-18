/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-15
 * @author vlads
 */
package com.propertyvista.portal.server.portal.prospect;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.prospect.MasterOnlineApplication;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.rpc.portal.prospect.ProspectUserVisit;
import com.propertyvista.portal.server.portal.shared.PortalVistaContext;

public class ProspectPortalContext extends PortalVistaContext {

    public static void setOnlineApplication(ProspectUserVisit visit, OnlineApplication application) {
        Lease leaseId = null;
        if (application != null) {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.eq(criteria.proto().leaseApplication().onlineApplication().applications(), application);
            leaseId = Persistence.service().retrieve(criteria, AttachLevel.IdOnly);
        }
        LeaseParticipant<?> leaseParticipantId = null;
        if (application != null) {
            @SuppressWarnings("rawtypes")
            EntityQueryCriteria<LeaseParticipant> criteria = EntityQueryCriteria.create(LeaseParticipant.class);
            criteria.eq(criteria.proto().lease().leaseApplication().onlineApplication().applications(), application);
            criteria.eq(criteria.proto().customer().user(), visit.getCurrentUser());
            leaseParticipantId = Persistence.service().retrieve(criteria, AttachLevel.IdOnly);
        }
        visit.setOnlineApplication(application, leaseId, leaseParticipantId);
    }

    public static OnlineApplication getOnlineApplicationIdStub() {
        return ServerContext.visit(ProspectUserVisit.class).getOnlineApplicationId();
    }

    public static OnlineApplication getOnlineApplication() {
        return Persistence.service().retrieve(OnlineApplication.class, getOnlineApplicationIdStub().getPrimaryKey());
    }

    public static MasterOnlineApplication getMasterOnlineApplication() {
        EntityQueryCriteria<MasterOnlineApplication> criteria = EntityQueryCriteria.create(MasterOnlineApplication.class);
        criteria.eq(criteria.proto().applications(), getOnlineApplicationIdStub());
        return Persistence.service().retrieve(criteria);
    }

    public static LeaseTermTenant getLeaseTermTenant() {
        EntityQueryCriteria<LeaseTermTenant> criteria = EntityQueryCriteria.create(LeaseTermTenant.class);
        criteria.eq(criteria.proto().leaseParticipant().customer(), getCustomer());
        criteria.eq(criteria.proto().leaseParticipant().lease().leaseApplication().onlineApplication().applications(), getOnlineApplicationIdStub());
        criteria.eq(criteria.proto().leaseTermV().holder(), criteria.proto().leaseTermV().holder().lease().currentTerm());
        criteria.isDraft(criteria.proto().leaseTermV());
        return Persistence.service().retrieve(criteria);
    }

    public static Guarantor getGuarantor() {
        LeaseParticipant<?> lp = getLeaseParticipant();
        if (lp == null) {
            return null;
        }
        return (Guarantor) lp.cast();
    }

    public static LeaseTermGuarantor getLeaseTermGuarantor() {
        EntityQueryCriteria<LeaseTermGuarantor> criteria = EntityQueryCriteria.create(LeaseTermGuarantor.class);
        criteria.eq(criteria.proto().leaseParticipant().customer(), getCustomer());
        criteria.eq(criteria.proto().leaseParticipant().lease().leaseApplication().onlineApplication().applications(), getOnlineApplicationIdStub());
        criteria.eq(criteria.proto().leaseTermV().holder(), criteria.proto().leaseTermV().holder().lease().currentTerm());
        criteria.isDraft(criteria.proto().leaseTermV());
        return Persistence.service().retrieve(criteria);
    }
}
