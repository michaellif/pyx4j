/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-03
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer.screening;

import java.io.Serializable;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.tenant.ScreeningFacade;
import com.propertyvista.crm.rpc.services.customer.screening.LeaseParticipantScreeningVersionService;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.CustomerScreening.CustomerScreeningV;
import com.propertyvista.dto.LeaseParticipantScreeningTO.LeaseParticipantScreeningTOV;
import com.propertyvista.server.versioning.AbstractVistaVersionDataListServiceImpl;

public class LeaseParticipantScreeningVersionServiceImpl extends AbstractVistaVersionDataListServiceImpl<CustomerScreeningV, LeaseParticipantScreeningTOV>
        implements LeaseParticipantScreeningVersionService {

    public LeaseParticipantScreeningVersionServiceImpl() {
        super(CustomerScreeningV.class, LeaseParticipantScreeningTOV.class, CrmUser.class);
    }

    @Override
    protected void bind() {
        bind(toProto.versionNumber(), boProto.versionNumber());
        bind(toProto.fromDate(), boProto.fromDate());
        bind(toProto.toDate(), boProto.toDate());
        bind(toProto.createdByUser(), boProto.createdByUser());
    }

    @Override
    protected Path convertPropertyDTOPathToDBOPath(String path, CustomerScreeningV boProto, LeaseParticipantScreeningTOV toProto) {
        if (path.equals(toProto.holder().getPath().toString())) {
            return boProto.holder().getPath();
        } else {
            return super.convertPropertyDTOPathToDBOPath(path, boProto, toProto);
        }
    }

    @Override
    protected Serializable convertValue(EntityListCriteria<CustomerScreeningV> criteria, PropertyCriterion propertyCriterion) {
        if (toProto.holder().getPath().toString().equals(propertyCriterion.getPropertyPath())) {
            return getCustomerScreeningId((Key) propertyCriterion.getValue());
        } else {
            return super.convertValue(criteria, propertyCriterion);
        }
    }

    // Translate PK from LeaseParticipant to CustomerScreening
    private Key getCustomerScreeningId(Key keaseParticipantKey) {
        EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._tenantInLease(), keaseParticipantKey.asCurrentKey()));
        Customer customer = Persistence.service().retrieve(criteria, AttachLevel.IdOnly);
        CustomerScreening screeningId = ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningDraftOrFinal(customer, AttachLevel.IdOnly);
        return screeningId.getPrimaryKey().asCurrentKey();
    }

}
