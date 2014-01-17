/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-16
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.legal;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.legal.N4ManagementFacade;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4CandidateSearchCriteriaDTO;
import com.propertyvista.crm.server.util.BuildingsCriteriaNormalizer;
import com.propertyvista.domain.legal.LegalNoticeCandidate;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.server.common.util.AddressConverter;
import com.propertyvista.server.common.util.AddressRetriever;

public class N4CandidateSearcher {

    private final N4CandidateSearchCriteriaDTO searchCriteria;

    private volatile Vector<LegalNoticeCandidateDTO> candidates;

    private final ExecutionMonitor progressMonitor;

    private final BuildingsCriteriaNormalizer buildingCriteriaNormalizer;

    public N4CandidateSearcher(N4CandidateSearchCriteriaDTO searchCriteria, ExecutionMonitor progressMonitor) {
        this.searchCriteria = searchCriteria;
        this.progressMonitor = progressMonitor;
        this.buildingCriteriaNormalizer = new BuildingsCriteriaNormalizer(null);
    }

    public void searchForCandidates() {
        Vector<LegalNoticeCandidateDTO> n4CandidateDtos = new Vector<LegalNoticeCandidateDTO>();

        List<Building> buildingsFilter = buildingCriteriaNormalizer.normalizeDto(searchCriteria.portfolios(), searchCriteria.buildings());
        if (buildingsFilter == null) {
            buildingsFilter = Persistence.secureQuery(EntityQueryCriteria.create(Building.class));
        }
        if (buildingsFilter != null) {
            List<LegalNoticeCandidate> n4Candidates = ServerSideFactory.create(N4ManagementFacade.class).getN4Candidates(
                    searchCriteria.minAmountOwed().getValue(), buildingsFilter, this.progressMonitor);

            n4CandidateDtos = new Vector<LegalNoticeCandidateDTO>(n4Candidates.size());

            this.progressMonitor.setExpectedTotal(this.progressMonitor.getExpectedTotal() + n4Candidates.size());

            Iterator<LegalNoticeCandidate> n4CandidatesIterator = n4Candidates.iterator();

            while (n4CandidatesIterator.hasNext() && !progressMonitor.isTerminationRequested()) {
                LegalNoticeCandidate candidate = n4CandidatesIterator.next();
                n4CandidateDtos.add(makeLegalNoticeCandidateDto(candidate));
                progressMonitor.addProcessedEvent("Load N4 candidate details");

            }
        }
        if (!progressMonitor.isTerminationRequested()) {
            this.candidates = n4CandidateDtos;
        }
    }

    public Vector<LegalNoticeCandidateDTO> legalNoticeCandidates() {
        return this.candidates;
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

}
