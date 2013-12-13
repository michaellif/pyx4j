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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.view.client.ListDataProvider;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.gwt.client.deferred.DeferredProcessDialog;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog.Type;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.tools.common.datagrid.ValidationErrors;
import com.propertyvista.crm.client.ui.tools.financial.moneyin.MoneyInCreateBatchView;
import com.propertyvista.crm.client.ui.tools.financial.moneyin.forms.MoneyInCandidateSearchCriteriaModel;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.financial.moneyin.MoneyInCandidateDTO;
import com.propertyvista.crm.rpc.dto.financial.moneyin.MoneyInCandidateSearchCriteriaDTO;
import com.propertyvista.crm.rpc.dto.financial.moneyin.MoneyInLeaseParticipantDTO;
import com.propertyvista.crm.rpc.dto.financial.moneyin.MoneyInPaymentDTO;
import com.propertyvista.crm.rpc.dto.tools.TooManyResultsException;
import com.propertyvista.crm.rpc.services.financial.MoneyInToolService;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

public class MoneyInCreateBatchActivity extends AbstractActivity implements MoneyInCreateBatchView.Presenter {

    private static final I18n i18n = I18n.get(MoneyInCreateBatchActivity.class);

    private final MoneyInCreateBatchView view;

    private final ListDataProvider<MoneyInCandidateDTO> searchResultsProvider;

    private final ListDataProvider<MoneyInCandidateDTO> selectedForProcessingProvider;

    private final Map<Key, HashMap<Path, ValidationErrors>> validationErrorsMap;

    private final MoneyInToolService service;

    private final MoneyInCandidateDTO proto;

    private final Map<String, Comparator<MoneyInCandidateDTO>> sortComparatorsMap;

    public MoneyInCreateBatchActivity() {
        proto = EntityFactory.getEntityPrototype(MoneyInCandidateDTO.class);
        validationErrorsMap = new HashMap<Key, HashMap<Path, ValidationErrors>>();
        view = CrmSite.getViewFactory().getView(MoneyInCreateBatchView.class);
        searchResultsProvider = new ListDataProvider<MoneyInCandidateDTO>(new LinkedList<MoneyInCandidateDTO>(), this);
        selectedForProcessingProvider = new ListDataProvider<MoneyInCandidateDTO>(new LinkedList<MoneyInCandidateDTO>(), this);
        service = GWT.<MoneyInToolService> create(MoneyInToolService.class);

        sortComparatorsMap = createSortComparatorsMap();
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
        searchResultsProvider.getList().clear();
        service.findCandidates(new DefaultAsyncCallback<Vector<MoneyInCandidateDTO>>() {
            @Override
            public void onSuccess(Vector<MoneyInCandidateDTO> result) {
                merge(result, selectedForProcessingProvider.getList());
                searchResultsProvider.setList(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof TooManyResultsException) {
                    view.displayMessage(i18n.tr("Your search has found too many results. Please refine the criteria and try again."), Type.Warning);
                } else {
                    super.onFailure(caught);
                }
            }

        }, toDto(view.getSearchCriteria()));
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
            candidate.payment().payerLeaseTermTenantIdStub().set(null);
        } else {
            candidate.payment().payerLeaseTermTenantIdStub().set(payer.leaseTermTenantIdStub().duplicate());
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
        if (selectedForProcessingProvider.getList().isEmpty()) {
            view.displayMessage(i18n.tr("Please select some payments first."), Type.Info);
            return;
        }
        if (view.getRecieptDate() == null) {
            view.displayMessage(i18n.tr("Please Enter the \"Receipt Date\"."), Type.Info);
            return;
        }
        if (!validationErrorsMap.isEmpty()) {
            view.displayMessage(
                    i18n.tr("Cannot create batch due to validation errors. Please enter all the required data for payments selected for processing."),
                    Type.Info);
            return;
        }
        service.createPaymentBatch(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String deferredCorellationId) {
                startProcessingProgress(deferredCorellationId);
            }
        }, view.getRecieptDate(), createPayments(selectedForProcessingProvider.getList()));
    }

    @Override
    public void sortFoundCandidates(String memberPath, boolean isSortAscending) {
        sort(memberPath, searchResultsProvider, isSortAscending);
    }

    @Override
    public void sortSelectedCandidates(String memberPath, boolean isSortAscending) {
        sort(memberPath, selectedForProcessingProvider, isSortAscending);
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

    // TODO validation needs refactoring
    private void validate(MoneyInCandidateDTO candidate) {
        if (candidate.processPayment().isBooleanTrue()) {
            List<String> paymentAmountErrors = new LinkedList<String>();
            if (candidate.payment().payedAmount().isNull()) {
                paymentAmountErrors.add(i18n.tr("Amount is required."));
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
                checkNumberErrors.add(i18n.tr("Check number is required."));
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
                    if (objectValidationErrors.isEmpty()) {
                        this.validationErrorsMap.remove(getKey(candidate));
                    }
                }
            }
        } else {
            this.validationErrorsMap.remove(getKey(candidate));
        }
    }

    private void merge(List<MoneyInCandidateDTO> incomingCandidates, List<MoneyInCandidateDTO> selectedCandidates) {
        for (MoneyInCandidateDTO selected : selectedCandidates) {
            MoneyInCandidateDTO matchingIncoming = null;

            for (MoneyInCandidateDTO incoming : incomingCandidates) {
                if (selected.leaseIdStub().equals(incoming.leaseIdStub())) {
                    matchingIncoming = incoming;
                    break;
                }
            }

            if (matchingIncoming != null) {
                matchingIncoming.set(selected);
            }
        }
    }

    private MoneyInCandidateSearchCriteriaDTO toDto(MoneyInCandidateSearchCriteriaModel model) {
        MoneyInCandidateSearchCriteriaDTO dto = EntityFactory.create(MoneyInCandidateSearchCriteriaDTO.class);
        dto.portfolios().addAll(toIdStubs(Portfolio.class, model.portfolios()));
        dto.buildings().addAll(toIdStubs(Building.class, model.buildings()));
        dto.unit().setValue(model.unit().getValue("").toLowerCase());
        dto.lease().setValue(model.lease().getValue("").toLowerCase());
        dto.tenant().setValue(model.tenant().getValue("").toLowerCase());
        return dto;
    }

    private <E extends IEntity> Collection<? extends E> toIdStubs(Class<E> identityStubClass, IList<?> entities) {
        List<E> idStubs = new LinkedList<E>();
        for (IEntity entity : entities) {
            idStubs.add((EntityFactory.createIdentityStub(identityStubClass, entity.getPrimaryKey())));
        }
        return idStubs;
    }

    private Map<String, Comparator<MoneyInCandidateDTO>> createSortComparatorsMap() {//@formatter:off
        HashMap<String, Comparator<MoneyInCandidateDTO>> map = new HashMap<String, Comparator<MoneyInCandidateDTO>>();        
        putMemberValueComparator(map, proto.building());
        putMemberValueComparator(map, proto.unit());
        putMemberValueComparator(map, proto.leaseId());
        // TODO add comparator for tenants?        
        map.put(proto.payment().payerLeaseTermTenantIdStub().getPath().toString(), new Comparator<MoneyInCandidateDTO>() {
            @Override
            public int compare(MoneyInCandidateDTO o1, MoneyInCandidateDTO o2) {
                LeaseTermTenant tenant1 = o1.payment().payerLeaseTermTenantIdStub();
                LeaseTermTenant tenant2 = o2.payment().payerLeaseTermTenantIdStub();                
                if (tenant1.isNull() || tenant2.isNull()) {
                    return tenant1.isNull() && tenant2.isNull() ? 0 : (tenant1.isNull() ? -1 : 1);
                } else {
                    String name1 = null;
                    for (MoneyInLeaseParticipantDTO c : o1.payerCandidates()) {
                        if (c.leaseTermTenantIdStub().getPrimaryKey().equals(tenant1.getPrimaryKey())) {
                            name1 = c.name().getValue();
                            break;
                        }
                    }
                    String name2 = null;
                    for (MoneyInLeaseParticipantDTO c : o2.payerCandidates()) {
                        if (c.leaseTermTenantIdStub().getPrimaryKey().equals(tenant2.getPrimaryKey())) {
                            name2 = c.name().getValue();
                            break;
                        }
                    }
                    return name1.compareTo(name2);
                }
            }           
        });
        putMemberValueComparator(map, proto.totalOutstanding());
        putMemberValueComparator(map, proto.payment().payedAmount());
        putMemberValueComparator(map, proto.payment().checkNumber());
        putMemberValueComparator(map, proto.processPayment());
        return map;
    }//@formatter:off
    
    private void putMemberValueComparator(Map<String, Comparator<MoneyInCandidateDTO>> map, IObject<?> member) {
        final Path path = member.getPath();
        map.put(path.toString(), new Comparator<MoneyInCandidateDTO>() {
            @Override
            public int compare(MoneyInCandidateDTO o1, MoneyInCandidateDTO o2) {
                Comparable c1 = (Comparable)o1.getMember(path).getValue();
                Comparable c2 = (Comparable)o2.getMember(path).getValue();
                if (c1 == null || c2 == null) {
                    return c1 == c2 ? 0 : (c1 == null ? -1 : 1);
                } else {
                    return c1.compareTo(c2);
                } 
            }
        });
    }
    
    private void sort(String memberPath, ListDataProvider<MoneyInCandidateDTO> provider, boolean isSortAscending) {
        Comparator<MoneyInCandidateDTO> cmp = sortComparatorsMap.get(memberPath);
        if (cmp != null) {
            if (isSortAscending) {
                final Comparator<MoneyInCandidateDTO> reverseCmp = cmp;
                cmp = new Comparator<MoneyInCandidateDTO>() {
                    @Override
                    public int compare(MoneyInCandidateDTO o1, MoneyInCandidateDTO o2) {
                        return -reverseCmp.compare(o1, o2);
                    }
                };
            }
            Collections.sort(provider.getList(), cmp);
        }
    }
    
    private Vector<MoneyInPaymentDTO> createPayments(List<MoneyInCandidateDTO> candidates) {
        Vector<MoneyInPaymentDTO> payments = new Vector<MoneyInPaymentDTO>(candidates.size());
        for (MoneyInCandidateDTO candidate : candidates) {
            payments.add(candidate.payment().duplicate(MoneyInPaymentDTO.class));
        }
        return payments;
    }
    
    private void startProcessingProgress(String deferredCorrelationId) {
        DeferredProcessDialog d = new DeferredProcessDialog("", i18n.tr("Processing..."), false) {
            @Override
            public void onDeferredSuccess(DeferredProcessProgressResponse result) {
                super.onDeferredSuccess(result);
                MoneyInCreateBatchActivity.this.onProccessingSuccess(result);
                search();
            }

            @Override
            protected void onDeferredCompleate() {
                super.onDeferredCompleate();
                this.hide();
            }
        };
        d.show();
        d.startProgress(deferredCorrelationId);
    }
    
    private void onProccessingSuccess(DeferredProcessProgressResponse result) {
        Command displayBatches = new Command() {
            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(new CrmSiteMap.Finance.MoneyIn.Batches());
            }            
        };
        Command refreshData = new Command() {
            @Override
            public void execute() {
                selectedForProcessingProvider.getList().clear();
                MoneyInCreateBatchActivity.this.search();
            }            
        };
        view.confirm(i18n.tr("Batch has been created successfully. Do you wish to see the created batch?"), displayBatches, refreshData);        
    }

}
