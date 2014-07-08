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
package com.propertyvista.interfaces.importer.converter;

import com.pyx4j.entity.shared.utils.SimpleEntityBinder;

import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.interfaces.importer.model.TenantIO;

public class TenantConverter extends SimpleEntityBinder<LeaseTermTenant, TenantIO> {

    public TenantConverter() {
        super(LeaseTermTenant.class, TenantIO.class, false);
    }

    @Override
    protected void bind() {
        bind(toProto.email(), boProto.leaseParticipant().customer().person().email());
        bind(toProto.participantId(), boProto.leaseParticipant().participantId());
        bind(toProto.firstName(), boProto.leaseParticipant().customer().person().name().firstName());
        bind(toProto.middleName(), boProto.leaseParticipant().customer().person().name().middleName());
        bind(toProto.lastName(), boProto.leaseParticipant().customer().person().name().lastName());
        bind(toProto.maidenName(), boProto.leaseParticipant().customer().person().name().maidenName());
        bind(toProto.nameSuffix(), boProto.leaseParticipant().customer().person().name().nameSuffix());
    }

}
