/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-03
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.legal;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.legal.N4ManagementFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4GenerationQueryDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4GenerationQueryDTO.DeliveryMethod;
import com.propertyvista.crm.rpc.dto.legal.n4.N4GenerationSettingsDTO;
import com.propertyvista.crm.rpc.services.legal.N4GenerationToolService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.legal.LegalNoticeCandidate;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.N4Policy;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.server.common.util.AddressConverter;
import com.propertyvista.server.common.util.AddressRetriever;

public class N4GenerationToolServiceImpl implements N4GenerationToolService {

    private static final I18n i18n = I18n.get(N4GenerationToolServiceImpl.class);

    @Override
    public void getItems(AsyncCallback<Vector<LegalNoticeCandidateDTO>> callback, N4GenerationSettingsDTO settings) {
        assertN4PolicyIsSet();

        List<LegalNoticeCandidate> n4Candidates = ServerSideFactory.create(N4ManagementFacade.class).getN4Candidates(settings.minAmountOwed().getValue(),
                settings.buildings());

        Vector<LegalNoticeCandidateDTO> dtoCandidates = new Vector<LegalNoticeCandidateDTO>(n4Candidates.size());
        for (LegalNoticeCandidate candidate : n4Candidates) {
            dtoCandidates.add(makeLegalNoticeCandidateDto(candidate));
        }

        callback.onSuccess(dtoCandidates);
    }

    @Override
    public void process(AsyncCallback<String> callback, N4GenerationQueryDTO query) {
        callback.onSuccess(DeferredProcessRegistry.fork(new N4GenerationDeferredProcess(query.targetDelinquentLeases(), query.agent(), query.noticeDate()
                .getValue()), ThreadPoolNames.IMPORTS));
    }

    @Override
    public void initSettings(AsyncCallback<N4GenerationSettingsDTO> callback) {
        N4GenerationSettingsDTO settings = EntityFactory.create(N4GenerationSettingsDTO.class);
        settings.query().noticeDate().setValue(new LogicalDate());
        settings.query().deliveryMethod().setValue(DeliveryMethod.Hand);
        settings.query().agent().set(CrmAppContext.getCurrentUserEmployee());

        callback.onSuccess(settings);

    }

    private LegalNoticeCandidateDTO makeLegalNoticeCandidateDto(LegalNoticeCandidate candidate) {
        LegalNoticeCandidateDTO dto = candidate.duplicate(LegalNoticeCandidateDTO.class);
        Lease lease = Persistence.service().retrieve(Lease.class, dto.leaseId().getPrimaryKey());
        Persistence.service().retrieve(lease.unit());
        Persistence.service().retrieve(lease.unit().building());
        dto.building().setValue(lease.unit().building().propertyCode().getValue());

        dto.address().setValue(
                new AddressConverter.StructuredToSimpleAddressConverter().createTO(AddressRetriever.getUnitLegalAddress(lease.unit())).getStringView());
        dto.unit().setValue(lease.unit().info().number().getValue());
        dto.leaseIdString().setValue(lease.leaseId().getValue());
        dto.moveIn().setValue(lease.expectedMoveIn().getValue());
        dto.moveOut().setValue(lease.expectedMoveOut().getValue());
        dto.n4Issued().setValue(N4Utils.pastN4sCount(lease));
        return dto;
    }

    private void assertN4PolicyIsSet() {
        N4Policy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(EntityFactory.create(OrganizationPoliciesNode.class),
                N4Policy.class);

        if (policy.relevantArCodes().isEmpty()) {
            throw new UserRuntimeException("N4 Policy has no AR Code settings. Please set up AR Codes in N4 policy!");
        }
    }

}
