/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-12
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.tenant.OnlineApplicationFacade;
import com.propertyvista.crm.rpc.services.lease.LeaseApplicationEditorCrudService;
import com.propertyvista.crm.server.services.lease.common.LeaseEditorCrudServiceBaseImpl;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.server.common.util.TenantConverter;
import com.propertyvista.server.common.util.TenantRetriever;

public class LeaseApplicationEditorCrudServiceImpl extends LeaseEditorCrudServiceBaseImpl<LeaseApplicationDTO> implements LeaseApplicationEditorCrudService {

    private final static I18n i18n = I18n.get(LeaseApplicationEditorCrudServiceImpl.class);

    public LeaseApplicationEditorCrudServiceImpl() {
        super(LeaseApplicationDTO.class);
    }

    @Override
    protected void enhanceRetrieved(Lease in, LeaseApplicationDTO dto) {
        super.enhanceRetrieved(in, dto);
        enhanceRetrievedCommon(in, dto);

        for (Tenant tenant : dto.version().tenants()) {
            TenantRetriever tr = new TenantRetriever(tenant.getPrimaryKey(), true);
            dto.tenantInfo().add(createTenantInfoDTO(tr));
            dto.tenantFinancials().add(createTenantFinancialDTO(tr));
        }

        dto.masterApplicationStatus().set(
                ServerSideFactory.create(OnlineApplicationFacade.class).calculateOnlineApplicationStatus(dto.leaseApplication().onlineApplication()));
    }

    @Override
    protected void enhanceListRetrieved(Lease in, LeaseApplicationDTO dto) {
        super.enhanceListRetrieved(in, dto);
        enhanceRetrievedCommon(in, dto);
    }

    private void enhanceRetrievedCommon(Lease in, LeaseApplicationDTO dto) {
        dto.numberOfOccupants().setValue(dto.version().tenants().size());
        dto.numberOfGuarantors().setValue(dto.version().guarantors().size());
        dto.numberOfApplicants().setValue(0);

        for (Tenant tenant : dto.version().tenants()) {
            Persistence.service().retrieve(tenant);
            Persistence.service().retrieve(tenant.screening(), AttachLevel.ToStringMembers);

            if (tenant.role().getValue() == LeaseParticipant.Role.Applicant) {
                dto.mainApplicant().set(tenant.customer());
                dto.numberOfApplicants().setValue(dto.numberOfApplicants().getValue() + 1);
            } else if (tenant.role().getValue() == LeaseParticipant.Role.CoApplicant) {
                dto.numberOfApplicants().setValue(dto.numberOfApplicants().getValue() + 1);
            }
        }
    }

    // internal helpers:
    private TenantInfoDTO createTenantInfoDTO(TenantRetriever tr) {
        TenantInfoDTO tiDTO = new TenantConverter.Tenant2TenantInfo().createDTO(tr.getTenant());
        new TenantConverter.TenantScreening2TenantInfo().copyDBOtoDTO(tr.getScreening(), tiDTO);
        return tiDTO;
    }

    private TenantFinancialDTO createTenantFinancialDTO(TenantRetriever tr) {
        TenantFinancialDTO tfDTO = new TenantConverter.TenantFinancialEditorConverter().createDTO(tr.getScreening());
        tfDTO.person().set(tr.getPerson());
        return tfDTO;
    }
}
