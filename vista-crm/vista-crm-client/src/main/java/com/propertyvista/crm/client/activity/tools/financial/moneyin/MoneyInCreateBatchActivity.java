/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-26
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.tools.financial.moneyin;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.view.client.ListDataProvider;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.tools.common.datagrid.ValidationErrors;
import com.propertyvista.crm.client.ui.tools.financial.moneyin.MoneyInCreateBatchView;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.moneyin.MoneyInCandidateDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.moneyin.MoneyInLeaseParticipantDTO;

public class MoneyInCreateBatchActivity extends AbstractActivity implements MoneyInCreateBatchView.Presenter {

    private static final I18n i18n = I18n.get(MoneyInCreateBatchActivity.class);

    private final MoneyInCreateBatchView view;

    private final ListDataProvider<MoneyInCandidateDTO> searchResultsProvider;

    private final ListDataProvider<MoneyInCandidateDTO> selectedForProcessingProvider;

    private final Map<Key, HashMap<Path, ValidationErrors>> validationErrorsMap;

    public MoneyInCreateBatchActivity() {
        validationErrorsMap = new HashMap<Key, HashMap<Path, ValidationErrors>>();
        view = CrmSite.getViewFactory().getView(MoneyInCreateBatchView.class);
        searchResultsProvider = new ListDataProvider<MoneyInCandidateDTO>(makeMockCandidates(), this);
        selectedForProcessingProvider = new ListDataProvider<MoneyInCandidateDTO>(new LinkedList<MoneyInCandidateDTO>(), this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        searchResultsProvider.addDataDisplay(view.searchResults());
        selectedForProcessingProvider.addDataDisplay(view.selectedForProcessing());
        panel.setWidget(view);
    }

    @Override
    public Key getKey(MoneyInCandidateDTO item) {
        return item.leaseIdStub().getPrimaryKey();
    }

    @Override
    public void search() {
        // TODO 
        System.out.println(view.getSearchCriteria());
    }

    @Override
    public void setProcessCandidate(MoneyInCandidateDTO candidate, boolean process) {
        candidate.processPayment().setValue(process);
        validate(candidate);

        if (candidate.processPayment().isBooleanTrue()) {
            selectedForProcessingProvider.getList().add(candidate);
        } else {
            selectedForProcessingProvider.getList().remove(candidate);
        }

        updateSearchResultsView(candidate);
    }

    @Override
    public void setPayer(MoneyInCandidateDTO candidate, MoneyInLeaseParticipantDTO payer) {
        if (payer == null) {
            candidate.payment().payerTenantIdStub().set(null);
        } else {
            candidate.payment().payerTenantIdStub().set(payer.tenantIdStub().duplicate());
        }
        validate(candidate);

        updateSearchResultsView(candidate);
        updateSelectedForProcessingView(candidate);
    }

    @Override
    public void setAmount(MoneyInCandidateDTO candidate, BigDecimal amountToPay) {
        candidate.payment().payedAmount().setValue(amountToPay);
        validate(candidate);

        updateSearchResultsView(candidate);
        updateSelectedForProcessingView(candidate);
    }

    @Override
    public void setCheckNumber(MoneyInCandidateDTO candidate, String checkNumber) {
        candidate.payment().checkNumber().setValue(checkNumber);
        validate(candidate);

        updateSearchResultsView(candidate);
        updateSelectedForProcessingView(candidate);
    }

    @Override
    public void createBatch() {
        // TODO Auto-generated method stub
    }

    @Override
    public ValidationErrors getValidationErrors(MoneyInCandidateDTO object, Path memberPath) {
        Map<Path, ValidationErrors> objectValidationErrors = this.validationErrorsMap.get(getKey(object));
        if (objectValidationErrors != null) {
            return objectValidationErrors.get(memberPath);
        } else {
            return null;
        }
    }

    @Override
    public void populate() {
    }

    @Override
    public void refresh() {
    }

    @Override
    public AppPlace getPlace() {
        return null;
    }

    // TODO needs refactoring
    private void validate(MoneyInCandidateDTO candidate) {
        if (candidate.processPayment().isBooleanTrue()) {
            List<String> paymentAmountErrors = new LinkedList<String>();
            if (candidate.payment().payedAmount().isNull()) {
                paymentAmountErrors.add(i18n.tr("Amount is required for processing."));
            } else {
                if (candidate.payment().payedAmount().getValue().compareTo(BigDecimal.ZERO) <= 0) {
                    paymentAmountErrors.add(i18n.tr("Amount must be greater than zero."));
                }
            }
            if (!paymentAmountErrors.isEmpty()) {
                setValidationErrors(candidate, candidate.payment().payedAmount().getPath(), new ValidationErrors(paymentAmountErrors));
            } else {
                setValidationErrors(candidate, candidate.payment().payedAmount().getPath(), null);
            }

            List<String> checkNumberErrors = new LinkedList<String>();
            if (CommonsStringUtils.isEmpty(candidate.payment().checkNumber().getValue())) {
                checkNumberErrors.add(i18n.tr("Check number is required to for processing."));
            }
            if (!checkNumberErrors.isEmpty()) {
                setValidationErrors(candidate, candidate.payment().checkNumber().getPath(), new ValidationErrors(checkNumberErrors));
            } else {
                setValidationErrors(candidate, candidate.payment().checkNumber().getPath(), null);
            }
        } else {
            setValidationErrors(candidate, null, null);
        }
    }

    private void updateSearchResultsView(MoneyInCandidateDTO candidate) {
        int i = this.searchResultsProvider.getList().indexOf(candidate);
        if (i != -1) {
            this.searchResultsProvider.getList().set(i, candidate);
        }
    }

    private void updateSelectedForProcessingView(MoneyInCandidateDTO candidate) {
        int i = this.selectedForProcessingProvider.getList().indexOf(candidate);
        if (i != -1) {
            this.selectedForProcessingProvider.getList().set(i, candidate);
        }
    }

    /**
     * @param candidate
     *            an item subject to validation errors
     * @param path
     *            pass <code>null</code> to clear the errors of the candidate
     * @param validationErrors
     *            pass <code>null</code> to clear the errors in path
     */
    private void setValidationErrors(MoneyInCandidateDTO candidate, Path path, ValidationErrors validationErrors) {
        if (path != null) {
            HashMap<Path, ValidationErrors> objectValidationErrors = this.validationErrorsMap.get(getKey(candidate));
            if (validationErrors != null) {
                if (objectValidationErrors == null) {
                    objectValidationErrors = new HashMap<Path, ValidationErrors>();
                    this.validationErrorsMap.put(getKey(candidate), objectValidationErrors);
                }
                objectValidationErrors.put(path, validationErrors);
            } else {
                if (objectValidationErrors != null) {
                    objectValidationErrors.remove(path);
                }
            }
        } else {
            this.validationErrorsMap.remove(getKey(candidate));
        }
    }

    private List<MoneyInCandidateDTO> makeMockCandidates() {
        List<MoneyInCandidateDTO> mockCandidates = new LinkedList<MoneyInCandidateDTO>();
        for (int i = 1; i < 101; ++i) {
            mockCandidates.add(makeMockCandidate(i));
        }
        return mockCandidates;
    }

    private MoneyInCandidateDTO makeMockCandidate(int n) {
        MoneyInCandidateDTO c = EntityFactory.create(MoneyInCandidateDTO.class);
        c.leaseIdStub().setPrimaryKey(new Key(n));
        c.building().setValue(n % 5 != 0 ? "B1" : "B2");
        c.unit().setValue("" + (100 + n));
        c.leaseId().setValue("t00000" + n);

        c.prepayments().setValue(new BigDecimal("0.00"));
        c.totalOutstanding().setValue(new BigDecimal("1077.00"));

        for (int t = 1; t != 3; ++t) {
            MoneyInLeaseParticipantDTO payer = c.payerCandidates().$();
            payer.tenantIdStub().setPrimaryKey(new Key(t));
            payer.name().setValue("Tenat Tenantovic #" + t);
            c.payerCandidates().add(payer);
        }

        return c;
    }

}
