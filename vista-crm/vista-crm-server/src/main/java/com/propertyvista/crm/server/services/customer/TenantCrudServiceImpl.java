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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.tenant.CustomerFacade;
import com.propertyvista.crm.rpc.services.customer.TenantCrudService;
import com.propertyvista.crm.server.services.Commons;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.dto.TenantDTO;

public class TenantCrudServiceImpl extends AbstractCrudServiceDtoImpl<Tenant, TenantDTO> implements TenantCrudService {

    public TenantCrudServiceImpl() {
        super(Tenant.class, TenantDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(Tenant entity, TenantDTO dto) {
        // load detached data:
        Persistence.service().retrieve(dto.customer().emergencyContacts());
        Persistence.service().retrieve(dto.leaseV());
        Persistence.service().retrieve(dto.leaseV().holder(), AttachLevel.ToStringMembers);
        Persistence.service().retrieve(entity.paymentMethods());
    }

    @Override
    protected void enhanceListRetrieved(Tenant entity, TenantDTO dto) {
        Persistence.service().retrieve(dto.leaseV());
        Persistence.service().retrieve(dto.leaseV().holder(), AttachLevel.ToStringMembers);
    }

    @Override
    protected void persist(Tenant entity, TenantDTO in) {
        ServerSideFactory.create(CustomerFacade.class).persistCustomer(entity.customer());

        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._Units().$()._Leases().$().versions(), entity.leaseV()));
        Building building = Persistence.service().retrieve(criteria);

        for (PaymentMethod paymentMethod : entity.paymentMethods()) {
            ServerSideFactory.create(PaymentFacade.class).persistPaymentMethod(building, paymentMethod);
        }
        super.persist(entity, in);
    }

    @Override
    public void getCurrentAddress(AsyncCallback<AddressStructured> callback, Key entityId) {
        Commons.getLeaseParticipantCurrentAddress(callback, Persistence.service().retrieve(Tenant.class, entityId));
    }
}
