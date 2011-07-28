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
package com.propertyvista.portal.server.ptapp.util;

import com.propertvista.generator.gdo.TenantSummaryGDO;

import com.pyx4j.entity.shared.utils.EntityDtoBinder;

import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.TenantInLeaseFragment;
import com.propertyvista.domain.tenant.TenantScreeningSecureInfoFragment;
import com.propertyvista.portal.domain.ptapp.dto.TenantEditorDTO;
import com.propertyvista.portal.domain.ptapp.dto.TenantFinancialEditorDTO;
import com.propertyvista.portal.domain.ptapp.dto.TenantInfoEditorDTO;

public class TenantConverter {

    public static class TenantEditorConverter extends EntityDtoBinder<TenantInLease, TenantEditorDTO> {

        public TenantEditorConverter() {
            super(TenantInLease.class, TenantEditorDTO.class);
        }

        @Override
        protected void bind() {
            bind(dtoProto.person(), dboProto.tenant().person());
            bind(TenantInLeaseFragment.class, dtoProto, dboProto);
        }

    }

    public static class TenantInfoEditorConverter extends EntityDtoBinder<TenantSummaryGDO, TenantInfoEditorDTO> {

        public TenantInfoEditorConverter() {
            super(TenantSummaryGDO.class, TenantInfoEditorDTO.class);
        }

        @Override
        protected void bind() {
            bind(dtoProto.person(), dboProto.tenant().person());
            bind(dtoProto.emergencyContacts(), dboProto.tenant().emergencyContacts());

            bind(TenantScreeningSecureInfoFragment.class, dtoProto, dboProto.tenantScreening());

            bind(dtoProto.documents(), dboProto.tenantScreening().documents());

            bind(dtoProto.currentAddress(), dboProto.tenantScreening().currentAddress());
            bind(dtoProto.previousAddress(), dboProto.tenantScreening().previousAddress());
            bind(dtoProto.legalQuestions(), dboProto.tenantScreening().legalQuestions());

        }

    }

    public static class TenantFinancialEditorConverter extends EntityDtoBinder<TenantSummaryGDO, TenantFinancialEditorDTO> {

        public TenantFinancialEditorConverter() {
            super(TenantSummaryGDO.class, TenantFinancialEditorDTO.class);
        }

        @Override
        protected void bind() {
            bind(dtoProto.incomes(), dboProto.tenantScreening().incomes());
            bind(dtoProto.assets(), dboProto.tenantScreening().assets());
            bind(dtoProto.guarantors(), dboProto.tenantScreening().guarantors());
        }

    }

}
