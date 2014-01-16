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
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4BatchRequestDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4CandidateSearchCriteriaDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4GenerationDefaultParamsDTO;
import com.propertyvista.crm.rpc.services.legal.N4CreateBatchService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.legal.n4.N4DeliveryMethod;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.N4Policy;
import com.propertyvista.portal.rpc.shared.PolicyNotFoundException;

public class N4CreateBatchServiceImpl implements N4CreateBatchService {

    private static final I18n i18n = I18n.get(N4CreateBatchServiceImpl.class);

    @Override
    public void searchForItems(AsyncCallback<String> callback, N4CandidateSearchCriteriaDTO searchCriteria) {
        assertN4PolicyIsSet();
        Context.getVisit().removeAttribute(N4CandidateSearchDeferredProcess.SEARCH_RESULTS_KEY);
        callback.onSuccess(DeferredProcessRegistry.fork(new N4CandidateSearchDeferredProcess(searchCriteria), ThreadPoolNames.IMPORTS));
    }

    @Override
    public void getFoundItems(AsyncCallback<Vector<LegalNoticeCandidateDTO>> callback) {
        Vector<LegalNoticeCandidateDTO> results = (Vector<LegalNoticeCandidateDTO>) Context.getVisit().getAttribute(
                N4CandidateSearchDeferredProcess.SEARCH_RESULTS_KEY);
        if (results != null) {
            Context.getVisit().removeAttribute(N4CandidateSearchDeferredProcess.SEARCH_RESULTS_KEY);
            callback.onSuccess(results);
        } else {
            throw new RuntimeException("Session for user (principal PK ='" + Context.getVisit().getUserVisit().getPrincipalPrimaryKey()
                    + "') doens't contain N4 candidate search results");
        }
    }

    @Override
    public void getItems(AsyncCallback<Vector<LegalNoticeCandidateDTO>> callback, N4CandidateSearchCriteriaDTO searchCriteria) {
        assertN4PolicyIsSet();

        N4CandidateSearcher searcher = new N4CandidateSearcher(searchCriteria, new ExecutionMonitor());
        searcher.searchForCandidates();
        callback.onSuccess(searcher.legalNoticeCandidates());

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
            defaults.batchRequest().mailingAddress().set(n4policy.mailingAddress().duplicate(AddressStructured.class));
            defaults.batchRequest().phoneNumber().setValue(n4policy.phoneNumber().getValue());
            defaults.batchRequest().faxNumber().setValue(n4policy.faxNumber().getValue());
            defaults.batchRequest().emailAddress().setValue(n4policy.emailAddress().getValue());
        }
        callback.onSuccess(defaults);
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

}
