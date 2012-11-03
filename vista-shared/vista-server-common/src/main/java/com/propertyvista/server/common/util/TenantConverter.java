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

import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInLeaseDTO;
import com.propertyvista.dto.TenantInfoDTO;

public class TenantConverter {

    public static class TenantEditorConverter extends EntityDtoBinder<LeaseTermTenant, TenantInLeaseDTO> {

        public TenantEditorConverter() {
            super(LeaseTermTenant.class, TenantInLeaseDTO.class);
        }

        @Override
        protected void bind() {
            bind(dtoProto.leaseParticipant().customer(), dboProto.leaseParticipant().customer());
            bind(dtoProto.relationship(), dboProto.relationship());
            bind(dtoProto.role(), dboProto.role());
            bind(dtoProto.takeOwnership(), dboProto.takeOwnership());
        }
    }

    @SuppressWarnings("rawtypes")
    public static class LeaseParticipant2TenantInfo extends EntityDtoBinder<LeaseTermParticipant, TenantInfoDTO> {

        public LeaseParticipant2TenantInfo() {
            super(LeaseTermParticipant.class, TenantInfoDTO.class);
        }

        @Override
        protected void bind() {
            bind(dtoProto.person(), dboProto.leaseParticipant().customer().person());
            bind(dtoProto.emergencyContacts(), dboProto.leaseParticipant().customer().emergencyContacts());
        }
    }

    public static class Tenant2TenantInfo extends EntityDtoBinder<LeaseTermTenant, TenantInfoDTO> {

        public Tenant2TenantInfo() {
            super(LeaseTermTenant.class, TenantInfoDTO.class);
        }

        @Override
        protected void bind() {
            bind(dtoProto.person(), dboProto.leaseParticipant().customer().person());
            bind(dtoProto.emergencyContacts(), dboProto.leaseParticipant().customer().emergencyContacts());
        }
    }

    public static class Guarantor2TenantInfo extends EntityDtoBinder<LeaseTermGuarantor, TenantInfoDTO> {

        public Guarantor2TenantInfo() {
            super(LeaseTermGuarantor.class, TenantInfoDTO.class);
        }

        @Override
        protected void bind() {
            bind(dtoProto.person(), dboProto.leaseParticipant().customer().person());
        }
    }

    public static class TenantScreening2TenantInfo extends EntityDtoBinder<CustomerScreening, TenantInfoDTO> {

        public TenantScreening2TenantInfo() {
            super(CustomerScreening.class, TenantInfoDTO.class);
        }

        @Override
        protected void bind() {
            bind(dtoProto.version().currentAddress(), dboProto.version().currentAddress());
            bind(dtoProto.version().previousAddress(), dboProto.version().previousAddress());
            bind(dtoProto.documents(), dboProto.documents());
            bind(dtoProto.version().legalQuestions(), dboProto.version().legalQuestions());
        }
    }

    public static class TenantFinancialEditorConverter extends EntityDtoBinder<CustomerScreening, TenantFinancialDTO> {

        public TenantFinancialEditorConverter() {
            super(CustomerScreening.class, TenantFinancialDTO.class);
        }

        @Override
        protected void bind() {
            bind(dtoProto.incomes(), dboProto.version().incomes());
            bind(dtoProto.assets(), dboProto.version().assets());
        }
    }
}
