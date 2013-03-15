/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-15
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;
import com.propertyvista.crm.rpc.services.customer.PreauthorizedPaymentsVisorService;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.tenant.lease.Tenant;

public class PreauthorizedPaymentsVisorServiceImpl implements PreauthorizedPaymentsVisorService {

    @Override
    public void retrieve(AsyncCallback<PreauthorizedPaymentsDTO> callback, Tenant tenantId) {
        PreauthorizedPaymentsDTO dto = EntityFactory.create(PreauthorizedPaymentsDTO.class);

        dto.tenant().set(Persistence.service().retrieve(Tenant.class, tenantId.getPrimaryKey()));
        Persistence.service().retrieveMember(dto.tenant().preauthorizedPayments());

        EntityListCriteria<LeasePaymentMethod> criteria = new EntityListCriteria<LeasePaymentMethod>(LeasePaymentMethod.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().customer(), dto.tenant().customer()));
        criteria.add(PropertyCriterion.eq(criteria.proto().isOneTimePayment(), Boolean.FALSE));
        criteria.add(PropertyCriterion.eq(criteria.proto().isDeleted(), Boolean.FALSE));

        dto.availablePaymentMethods().addAll(Persistence.service().query(criteria));

        callback.onSuccess(dto);
    }

    @Override
    public void save(AsyncCallback<VoidSerializable> callback, PreauthorizedPaymentsDTO pads) {
        Persistence.service().merge(pads.tenant());
        Persistence.service().commit();

        callback.onSuccess(null);
    }
}
