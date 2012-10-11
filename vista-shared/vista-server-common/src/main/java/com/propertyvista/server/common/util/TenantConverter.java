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

import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInLeaseDTO;
import com.propertyvista.dto.TenantInfoDTO;

public class TenantConverter {

    public static class TenantEditorConverter extends EntityDtoBinder<Tenant, TenantInLeaseDTO> {

        public TenantEditorConverter() {
            super(Tenant.class, TenantInLeaseDTO.class);
        }

        @Override
        protected void bind() {
            bind(dtoProto.leaseCustomer().customer(), dboProto.leaseCustomer().customer());
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
            bind(dtoProto.person(), dboProto.leaseCustomer().customer().person());
            bind(dtoProto.emergencyContacts(), dboProto.leaseCustomer().customer().emergencyContacts());
        }
    }

    public static class Guarantor2TenantInfo extends EntityDtoBinder<Guarantor, TenantInfoDTO> {

        public Guarantor2TenantInfo() {
            super(Guarantor.class, TenantInfoDTO.class);
        }

        @Override
        protected void bind() {
            bind(dtoProto.person(), dboProto.leaseCustomer().customer().person());
        }
    }

    public static class TenantScreening2TenantInfo extends EntityDtoBinder<PersonScreening, TenantInfoDTO> {

        public TenantScreening2TenantInfo() {
            super(PersonScreening.class, TenantInfoDTO.class);
        }

        @Override
        protected void bind() {
            bind(dtoProto.version().currentAddress(), dboProto.version().currentAddress());
            bind(dtoProto.version().previousAddress(), dboProto.version().previousAddress());
            bind(dtoProto.documents(), dboProto.documents());
            bind(dtoProto.version().legalQuestions(), dboProto.version().legalQuestions());
        }
    }

    public static class TenantFinancialEditorConverter extends EntityDtoBinder<PersonScreening, TenantFinancialDTO> {

        public TenantFinancialEditorConverter() {
            super(PersonScreening.class, TenantFinancialDTO.class);
        }

        @Override
        protected void bind() {
            bind(dtoProto.incomes(), dboProto.version().incomes());
            bind(dtoProto.assets(), dboProto.version().assets());
        }
    }
}
