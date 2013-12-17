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
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.prospect;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.domain.tenant.prospect.MasterOnlineApplication;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.rpc.portal.prospect.ProspectUserVisit;
import com.propertyvista.portal.server.portal.shared.PortalVistaContext;

public class ProspectPortalContext extends PortalVistaContext {

    public static void setOnlineApplication(OnlineApplication application) {
        Context.getUserVisit(ProspectUserVisit.class).setOnlineApplication(application);
    }

    public static OnlineApplication getOnlineApplicationIdStub() {
        return Context.getUserVisit(ProspectUserVisit.class).getOnlineApplication();
    }

    public static OnlineApplication getOnlineApplication() {
        return Persistence.service().retrieve(OnlineApplication.class, getOnlineApplicationIdStub().getPrimaryKey());
    }

    public static MasterOnlineApplication getMasterOnlineApplication() {
        EntityQueryCriteria<MasterOnlineApplication> criteria = EntityQueryCriteria.create(MasterOnlineApplication.class);
        criteria.eq(criteria.proto().applications(), getOnlineApplicationIdStub());
        return Persistence.service().retrieve(criteria);
    }

    public static Lease getLease() {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.eq(criteria.proto().leaseApplication().onlineApplication().applications(), getOnlineApplicationIdStub());
        return Persistence.service().retrieve(criteria);
    }

    public static Tenant getTenant() {
        EntityQueryCriteria<Tenant> criteria = EntityQueryCriteria.create(Tenant.class);
        criteria.eq(criteria.proto().lease().leaseApplication().onlineApplication().applications(), getOnlineApplicationIdStub());
        criteria.eq(criteria.proto().customer().user(), getCustomerUserIdStub());
        return Persistence.service().retrieve(criteria);
    }

    public static LeaseTermTenant getLeaseTermTenant() {
        EntityQueryCriteria<LeaseTermTenant> criteria = EntityQueryCriteria.create(LeaseTermTenant.class);
        criteria.eq(criteria.proto().leaseParticipant().customer().user(), getCustomerUserIdStub());
        criteria.eq(criteria.proto().leaseParticipant().lease().leaseApplication().onlineApplication().applications(), getOnlineApplicationIdStub());
        criteria.eq(criteria.proto().leaseTermV().holder(), criteria.proto().leaseTermV().holder().lease().currentTerm());
        criteria.isDraft(criteria.proto().leaseTermV());
        return Persistence.service().retrieve(criteria);
    }

    public static Guarantor getGuarantor() {
        EntityQueryCriteria<Guarantor> criteria = EntityQueryCriteria.create(Guarantor.class);
        criteria.eq(criteria.proto().lease().leaseApplication().onlineApplication().applications(), getOnlineApplicationIdStub());
        criteria.eq(criteria.proto().customer().user(), getCustomerUserIdStub());
        return Persistence.service().retrieve(criteria);
    }

    public static LeaseTermGuarantor getLeaseTermGuarantor() {
        EntityQueryCriteria<LeaseTermGuarantor> criteria = EntityQueryCriteria.create(LeaseTermGuarantor.class);
        criteria.eq(criteria.proto().leaseParticipant().customer().user(), getCustomerUserIdStub());
        criteria.eq(criteria.proto().leaseParticipant().lease().leaseApplication().onlineApplication().applications(), getOnlineApplicationIdStub());
        criteria.eq(criteria.proto().leaseTermV().holder(), criteria.proto().leaseTermV().holder().lease().currentTerm());
        criteria.isDraft(criteria.proto().leaseTermV());
        return Persistence.service().retrieve(criteria);
    }
}
