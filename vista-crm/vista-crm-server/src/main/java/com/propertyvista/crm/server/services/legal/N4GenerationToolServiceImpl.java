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

import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.legal.N4ManagementFacade;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4GenerationSettingsDTO;
import com.propertyvista.crm.rpc.services.legal.N4GenerationToolService;
import com.propertyvista.domain.legal.LegalNoticeCandidate;
import com.propertyvista.domain.tenant.lease.Lease;

public class N4GenerationToolServiceImpl implements N4GenerationToolService {

    @Override
    public void getItems(AsyncCallback<Vector<LegalNoticeCandidateDTO>> callback, N4GenerationSettingsDTO settings) {
        List<LegalNoticeCandidate> n4Candidates = ServerSideFactory.create(N4ManagementFacade.class).getN4Candidates(BigDecimal.ZERO, null);

        Vector<LegalNoticeCandidateDTO> dtoCandidates = new Vector<LegalNoticeCandidateDTO>(n4Candidates.size());
        for (LegalNoticeCandidate candidate : n4Candidates) {
            LegalNoticeCandidateDTO dto = candidate.duplicate(LegalNoticeCandidateDTO.class);
            Lease lease = Persistence.service().retrieve(Lease.class, dto.leaseId().getPrimaryKey());
            Persistence.service().retrieve(lease.unit());
            Persistence.service().retrieve(lease.unit().building());
            dto.building().setValue(lease.unit().building().propertyCode().getValue());
            dtoCandidates.add(dto);
        }
        callback.onSuccess(dtoCandidates);
    }

    @Override
    public void process(AsyncCallback<String> callback, Vector<Lease> accepted) {
        callback.onSuccess("meh");
    }

}
