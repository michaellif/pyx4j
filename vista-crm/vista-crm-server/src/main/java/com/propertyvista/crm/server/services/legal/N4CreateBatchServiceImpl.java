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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.legal.N4ManagementFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4BatchRequestDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4CandidateSearchCriteriaDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4GenerationDefaultParamsDTO;
import com.propertyvista.crm.rpc.services.legal.N4CreateBatchService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.legal.LegalNoticeCandidate;
import com.propertyvista.domain.legal.n4.N4DeliveryMethod;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.N4Policy;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.shared.PolicyNotFoundException;
import com.propertyvista.server.common.util.AddressConverter;
import com.propertyvista.server.common.util.AddressRetriever;

public class N4CreateBatchServiceImpl implements N4CreateBatchService {

    private static final I18n i18n = I18n.get(N4CreateBatchServiceImpl.class);

    @Override
    public void getItems(AsyncCallback<Vector<LegalNoticeCandidateDTO>> callback, N4CandidateSearchCriteriaDTO settings) {
        assertN4PolicyIsSet();

        List<Building> buildingsFilter = queryBuildingsFilter(settings);

        if (!buildingsFilter.isEmpty()) {
            List<LegalNoticeCandidate> n4Candidates = ServerSideFactory.create(N4ManagementFacade.class).getN4Candidates(settings.minAmountOwed().getValue(),
                    buildingsFilter);

            Vector<LegalNoticeCandidateDTO> dtoCandidates = new Vector<LegalNoticeCandidateDTO>(n4Candidates.size());
            for (LegalNoticeCandidate candidate : n4Candidates) {
                dtoCandidates.add(makeLegalNoticeCandidateDto(candidate));
            }
            callback.onSuccess(dtoCandidates);

        } else {
            callback.onSuccess(new Vector<LegalNoticeCandidateDTO>());
        }
    }

    @Override
    public void process(AsyncCallback<String> callback, N4BatchRequestDTO batchRequest) {
        callback.onSuccess(DeferredProcessRegistry.fork(new N4GenerationDeferredProcess(batchRequest), ThreadPoolNames.IMPORTS));
    }

    @Override
    public void initSettings(AsyncCallback<N4GenerationDefaultParamsDTO> callback) {

        N4GenerationDefaultParamsDTO defaults = EntityFactory.create(N4GenerationDefaultParamsDTO.class);
        defaults.searchCriteria().n4PolicyErrors().setValue(StringUtils.join(validateN4Policy(), "\n"));
        if (CommonsStringUtils.isEmpty(defaults.searchCriteria().n4PolicyErrors().getValue())) {
            defaults.batchRequest().noticeDate().setValue(new LogicalDate());
            defaults.batchRequest().deliveryMethod().setValue(N4DeliveryMethod.Hand);
            defaults.batchRequest().agent().set(CrmAppContext.getCurrentUserEmployee());
            defaults.availableAgents().addAll(getAvailableAgents());

            N4Policy n4policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(EntityFactory.create(OrganizationPoliciesNode.class),
                    N4Policy.class);
            defaults.batchRequest().companyName().setValue(n4policy.companyName().getValue());
            defaults.batchRequest().mailingAddress().set(n4policy.mailingAddress().duplicate(AddressSimple.class));
            defaults.batchRequest().phoneNumber().setValue(n4policy.phoneNumber().getValue());
            defaults.batchRequest().faxNumber().setValue(n4policy.faxNumber().getValue());
            defaults.batchRequest().emailAddress().setValue(n4policy.emailAddress().getValue());
        }
        callback.onSuccess(defaults);
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

    private List<Employee> getAvailableAgents() {
        List<Employee> employees = Persistence.secureQuery(EntityQueryCriteria.create(Employee.class));
        for (Employee employee : employees) {
            Persistence.service().retrieve(employee.signature(), AttachLevel.IdOnly, false);
        }
        return employees;
    }

    private void assertN4PolicyIsSet() {
        List<String> validationErrors = validateN4Policy();
        if (!validationErrors.isEmpty()) {
            throw new UserRuntimeException(StringUtils.join(validationErrors, "\n"));
        }
    }

    private List<String> validateN4Policy() {
        List<String> policyValidationErrors = new ArrayList<String>();
        N4Policy policy = null;
        try {
            policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(EntityFactory.create(OrganizationPoliciesNode.class), N4Policy.class);
        } catch (PolicyNotFoundException p) {
            // do nothing for policy not found but don't ignore other runtime exceptions.
        }
        if (policy == null) {
            policyValidationErrors.add(i18n.tr("Please set up N4 policy to use this tool (at \"Administration/Policies\")"));
            return policyValidationErrors;
        } else {
            if (policy.relevantARCodes().isEmpty()) {
                policyValidationErrors.add(i18n.tr("N4 Policy has no AR Code settings. Please set up AR Codes in N4 policy!"));
            }
            return policyValidationErrors;
        }
    }

    private List<Building> queryBuildingsFilter(N4CandidateSearchCriteriaDTO settings) {
        Set<Building> buildingsFilter = new HashSet<Building>();
        if (!settings.filterByBuildings().isBooleanTrue() && !settings.filterByPortfolios().isBooleanTrue()) {
            buildingsFilter.addAll(Persistence.secureQuery(EntityQueryCriteria.create(Building.class)));
        }
        if (settings.filterByBuildings().isBooleanTrue()) {
            Portfolio virtualPortfolio = EntityFactory.create(Portfolio.class);
            virtualPortfolio.buildings().addAll(settings.buildings());
            if (!settings.filterByPortfolios().isBooleanTrue()) {
                settings.portfolios().clear();
                settings.filterByPortfolios().setValue(true);
            }
            settings.portfolios().add(virtualPortfolio);
        }
        if (settings.filterByPortfolios().isBooleanTrue()) {
            for (Portfolio portfolio : settings.portfolios()) {
                if (portfolio.isValueDetached()) {
                    portfolio = Persistence.service().retrieve(Portfolio.class, portfolio.getPrimaryKey());
                }
                if (!portfolio.buildings().isEmpty()) {
                    EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
                    criteria.in(criteria.proto().id(), portfolio.buildings());
                    buildingsFilter.addAll(Persistence.secureQuery(criteria));
                }
            }
        }
        return new ArrayList<Building>(buildingsFilter);

    }
}
