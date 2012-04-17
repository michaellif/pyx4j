/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services;

import java.util.Set;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.security.shared.Behavior;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.security.VistaTenantBehavior;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.LeaseV;
import com.propertyvista.portal.rpc.portal.services.PortalAuthenticationService;
import com.propertyvista.server.common.security.VistaAuthenticationServicesImpl;
import com.propertyvista.server.domain.security.TenantUserCredential;

public class PortalAuthenticationServiceImpl extends VistaAuthenticationServicesImpl<TenantUser, TenantUserCredential> implements PortalAuthenticationService {

    private static final I18n i18n = I18n.get(PortalAuthenticationServiceImpl.class);

    public PortalAuthenticationServiceImpl() {
        super(TenantUser.class, TenantUserCredential.class);
    }

    @Override
    protected VistaBasicBehavior getApplicationBehavior() {
        return VistaBasicBehavior.TenantPortal;
    }

    @Override
    protected Behavior getPasswordChangeRequiredBehavior() {
        return VistaBasicBehavior.TenantPortalPasswordChangeRequired;
    }

    @Override
    protected void addBehaviors(TenantUserCredential userCredential, Set<Behavior> behaviors) {
        if (!userCredential.behaviors().containsAny(VistaTenantBehavior.Tenant, VistaTenantBehavior.TenantPrimary, VistaTenantBehavior.TenantSecondary)) {
            throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
        }
        behaviors.addAll(userCredential.behaviors());
    }

    @Override
    protected void sendPasswordRetrievalToken(TenantUser user) {
        {
            // See if active Lease exists
            EntityQueryCriteria<LeaseV> criteria = EntityQueryCriteria.create(LeaseV.class);
            criteria.add(PropertyCriterion.in(criteria.proto().status(), Lease.Status.current()));
            criteria.add(PropertyCriterion.eq(criteria.proto().tenants().$().customer().user(), user));
            LeaseV lease = Persistence.service().retrieve(criteria);
            if (lease == null) {
                throw new UserRuntimeException(i18n.tr(GENERIC_FAILED_MESSAGE));
            }
        }

        {
            EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().user(), user));
            Customer customer = Persistence.service().retrieve(criteria);
            if (customer == null) {
                throw new UserRuntimeException(i18n.tr(GENERIC_FAILED_MESSAGE));
            }
            ServerSideFactory.create(CommunicationFacade.class).sendProspectPasswordRetrievalToken(customer);
        }

    }
}
