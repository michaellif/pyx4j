/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 21, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.util;

import com.pyx4j.entity.shared.utils.EntityDtoBinder;

import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.TenantScreening;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInLeaseDTO;
import com.propertyvista.dto.TenantInfoDTO;

public class TenantConverter {

    public static class TenantEditorConverter extends EntityDtoBinder<TenantInLease, TenantInLeaseDTO> {

        public TenantEditorConverter() {
            super(TenantInLease.class, TenantInLeaseDTO.class);
        }

        @Override
        protected void bind() {
            bind(dtoProto.person(), dboProto.tenant().person());
            bind(dtoProto.relationship(), dboProto.relationship());
            bind(dtoProto.role(), dboProto.role());
            bind(dtoProto.takeOwnership(), dboProto.takeOwnership());
        }
    }

    public static class Tenant2TenantInfo extends EntityDtoBinder<Tenant, TenantInfoDTO> {

        public Tenant2TenantInfo() {
            super(Tenant.class, TenantInfoDTO.class);
        }

        @Override
        protected void bind() {
            bind(dtoProto.person(), dboProto.person());
            bind(dtoProto.emergencyContacts(), dboProto.emergencyContacts());
        }

    }

    public static class TenantScreening2TenantInfo extends EntityDtoBinder<TenantScreening, TenantInfoDTO> {

        public TenantScreening2TenantInfo() {
            super(TenantScreening.class, TenantInfoDTO.class);
        }

        @Override
        protected void bind() {
            bind(dtoProto.driversLicense(), dboProto.driversLicense());
            bind(dtoProto.driversLicenseState(), dboProto.driversLicenseState());
            bind(dtoProto.secureIdentifier(), dboProto.secureIdentifier());
            bind(dtoProto.notCanadianCitizen(), dboProto.notCanadianCitizen());

            bind(dtoProto.documents(), dboProto.documents());

            bind(dtoProto.currentAddress(), dboProto.currentAddress());
            bind(dtoProto.previousAddress(), dboProto.previousAddress());
            bind(dtoProto.legalQuestions(), dboProto.legalQuestions());

        }

    }

    public static class TenantFinancialEditorConverter extends EntityDtoBinder<TenantScreening, TenantFinancialDTO> {

        public TenantFinancialEditorConverter() {
            super(TenantScreening.class, TenantFinancialDTO.class);
        }

        @Override
        protected void bind() {
            bind(dtoProto.incomes(), dboProto.incomes());
            //bind(dtoProto.incomes2(), dboProto.incomes2());
            bind(dtoProto.assets(), dboProto.assets());
            bind(dtoProto.guarantors(), dboProto.guarantors());
        }

    }

}
