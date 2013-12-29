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

import com.pyx4j.entity.shared.utils.EntityBinder;

import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInLeaseDTO;
import com.propertyvista.dto.TenantInfoDTO;

public class TenantConverter {

    public static class TenantEditorConverter extends EntityBinder<LeaseTermTenant, TenantInLeaseDTO> {

        public TenantEditorConverter() {
            super(LeaseTermTenant.class, TenantInLeaseDTO.class);
        }

        @Override
        protected void bind() {
            bind(toProto.leaseParticipant().customer(), boProto.leaseParticipant().customer());
            bind(toProto.relationship(), boProto.relationship());
            bind(toProto.role(), boProto.role());
        }
    }

    @SuppressWarnings("rawtypes")
    public static class LeaseParticipant2TenantInfo extends EntityBinder<LeaseTermParticipant, TenantInfoDTO> {

        public LeaseParticipant2TenantInfo() {
            super(LeaseTermParticipant.class, TenantInfoDTO.class);
        }

        @Override
        protected void bind() {
            bind(toProto.person(), boProto.leaseParticipant().customer().person());
            bind(toProto.emergencyContacts(), boProto.leaseParticipant().customer().emergencyContacts());
            bind(toProto.role(), boProto.role());
        }
    }

    public static class Tenant2TenantInfo extends EntityBinder<LeaseTermTenant, TenantInfoDTO> {

        public Tenant2TenantInfo() {
            super(LeaseTermTenant.class, TenantInfoDTO.class);
        }

        @Override
        protected void bind() {
            bind(toProto.person(), boProto.leaseParticipant().customer().person());
            bind(toProto.emergencyContacts(), boProto.leaseParticipant().customer().emergencyContacts());
        }
    }

    public static class Guarantor2TenantInfo extends EntityBinder<LeaseTermGuarantor, TenantInfoDTO> {

        public Guarantor2TenantInfo() {
            super(LeaseTermGuarantor.class, TenantInfoDTO.class);
        }

        @Override
        protected void bind() {
            bind(toProto.person(), boProto.leaseParticipant().customer().person());
        }
    }

    public static class TenantScreening2TenantInfo extends EntityBinder<CustomerScreening, TenantInfoDTO> {

        public TenantScreening2TenantInfo() {
            super(CustomerScreening.class, TenantInfoDTO.class);
        }

        @Override
        protected void bind() {
            bind(toProto.version().currentAddress(), boProto.version().currentAddress());
            bind(toProto.version().previousAddress(), boProto.version().previousAddress());
            bind(toProto.version().documents(), boProto.version().documents());
            bind(toProto.version().legalQuestions(), boProto.version().legalQuestions());
        }
    }

    public static class TenantFinancialEditorConverter extends EntityBinder<CustomerScreening, TenantFinancialDTO> {

        public TenantFinancialEditorConverter() {
            super(CustomerScreening.class, TenantFinancialDTO.class);
        }

        @Override
        protected void bind() {
            bind(toProto.incomes(), boProto.version().incomes());
            bind(toProto.assets(), boProto.version().assets());
        }
    }
}
