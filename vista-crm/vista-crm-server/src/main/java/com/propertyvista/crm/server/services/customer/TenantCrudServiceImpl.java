/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.crm.rpc.services.customer.TenantCrudService;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.LeaseCustomer;
import com.propertyvista.domain.tenant.lease.LeaseCustomerTenant;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.dto.TenantDTO;

public class TenantCrudServiceImpl extends LeaseCustomerCrudServiceBaseImpl<LeaseCustomerTenant, TenantDTO> implements TenantCrudService {

    public TenantCrudServiceImpl() {
        super(LeaseCustomerTenant.class, TenantDTO.class);
    }

    @Override
    protected void enhanceRetrieved(LeaseCustomerTenant entity, TenantDTO dto, RetrieveTraget retrieveTraget) {
        super.enhanceRetrieved(entity, dto, retrieveTraget);

        dto.role().setValue(retrieveTenant(dto.leaseTermV(), entity).role().getValue());

        Persistence.service().retrieve(dto.customer().emergencyContacts());

        // mark pre-authorized one:
        for (PaymentMethod paymentMethod : dto.paymentMethods()) {
            if (paymentMethod.equals(entity.preauthorizedPayment())) {
                paymentMethod.isPreauthorized().setValue(Boolean.TRUE);
                break;
            }
        }
    }

    @Override
    protected void enhanceListRetrieved(LeaseCustomerTenant entity, TenantDTO dto) {
        super.enhanceListRetrieved(entity, dto);

        dto.role().setValue(retrieveTenant(dto.leaseTermV(), entity).role().getValue());
    }

    @Override
    protected void persist(LeaseCustomerTenant entity, TenantDTO dto) {
        super.persist(entity, dto);

        Tenant tenant = retrieveTenant(dto.leaseTermV(), entity);

        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._Units().$()._Leases().$().currentTerm().versions(), tenant.leaseTermV()));
        Building building = Persistence.service().retrieve(criteria);

        // save new/edited ones (and memorize pre-authorized method):
        for (PaymentMethod paymentMethod : dto.paymentMethods()) {
            paymentMethod.customer().set(entity.customer());
            paymentMethod.isOneTimePayment().setValue(false);
            ServerSideFactory.create(PaymentFacade.class).persistPaymentMethod(building, paymentMethod);

            if (paymentMethod.isPreauthorized().isBooleanTrue()) {
                if (!entity.preauthorizedPayment().equals(paymentMethod)) {
                    entity.preauthorizedPayment().set(paymentMethod);
                    Persistence.service().merge(entity);
                }
            }
        }
    }

    private Tenant retrieveTenant(LeaseTerm.LeaseTermV termV, LeaseCustomer leaseCustomer) {
        EntityQueryCriteria<Tenant> criteria = EntityQueryCriteria.create(Tenant.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseCustomer(), leaseCustomer));
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseTermV(), termV));
        return Persistence.service().retrieve(criteria);
    }
}
