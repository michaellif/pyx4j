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
package com.propertyvista.crm.client.activity.tools.legal.l1;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.activity.AbstractVisorController;
import com.pyx4j.site.client.ui.prime.wizard.IWizard;
import com.pyx4j.site.client.ui.prime.wizard.IWizard.Presenter;
import com.pyx4j.site.client.ui.visor.IVisorEditor;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.tools.common.datagrid.MultiSelectorState;
import com.propertyvista.crm.client.ui.tools.common.datagrid.SelectionPresetModel;
import com.propertyvista.crm.client.ui.tools.legal.l1.L1DelinquentLeaseSearchView;
import com.propertyvista.crm.client.ui.tools.legal.l1.L1DelinquentLeaseSearchViewImpl;
import com.propertyvista.crm.client.ui.tools.legal.l1.datagrid.L1CandidateSelectionPresets;
import com.propertyvista.crm.client.ui.tools.legal.l1.visors.L1CommonFormDataVisorView;
import com.propertyvista.crm.client.ui.tools.legal.l1.visors.L1FormDataReviewVisorView;
import com.propertyvista.crm.rpc.dto.legal.common.LegalActionCandidateDTO;
import com.propertyvista.crm.rpc.dto.legal.l1.L1CommonFieldsDTO;
import com.propertyvista.crm.rpc.dto.legal.l1.L1FormDataReviewWizardDTO;
import com.propertyvista.domain.legal.l1.L1LandlordsContactInfo;

public class L1DelinquentLeaseSearchActivity extends AbstractActivity implements L1DelinquentLeaseSearchView.Presenter {

    private final L1DelinquentLeaseSearchView view;

    private final ListDataProvider<LegalActionCandidateDTO> dataProvider;

    private final MultiSelectionModel<LegalActionCandidateDTO> selectionModel;

    public L1DelinquentLeaseSearchActivity() {
        view = CrmSite.getViewFactory().getView(L1DelinquentLeaseSearchView.class);
        dataProvider = new ListDataProvider<LegalActionCandidateDTO>(makeMockCandidates());
        selectionModel = new MultiSelectionModel<LegalActionCandidateDTO>(L1DelinquentLeaseSearchViewImpl.LeaseIdProvider.INSTANCE);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        panel.setWidget(view);
    }

    @Override
    public AbstractDataProvider<LegalActionCandidateDTO> getDataProvider() {
        return dataProvider;
    }

    @Override
    public SelectionModel<? super LegalActionCandidateDTO> getSelectionModel() {
        return selectionModel;
    }

    @Override
    public void updateSelection(SelectionPresetModel selected) {
        if (selected.getState() != MultiSelectorState.Preset) {
            for (LegalActionCandidateDTO c : dataProvider.getList()) {
                selectionModel.setSelected(c, selected.getState() == MultiSelectorState.All);
            }
        } else {
            if (selected.getPreset() == L1CandidateSelectionPresets.Reviewed) {
                for (LegalActionCandidateDTO c : dataProvider.getList()) {
                    selectionModel.setSelected(c, c.isReviewed().isBooleanTrue());
                }
            }
        }
    }

    @Override
    public void reviewCandidate(final LegalActionCandidateDTO candidate) {
        AbstractVisorController visorController = new AbstractVisorController(view) {

            private L1FormDataReviewVisorView visor;

            private IWizard.Presenter wizardPresenter;

            {
                wizardPresenter = new Presenter() {
                    @Override
                    public void populate() {
                        visor.reset();
                        visor.populate(candidate.l1FormReview().duplicate(L1FormDataReviewWizardDTO.class));
                        getParentView().showVisor(visor);
                    }

                    @Override
                    public void finish() {
                        candidate.l1FormReview().set(visor.getValue());

                        // update 'auto fields' that are not updated by the visor

                        // should trigger view update  
                        int i = dataProvider.getList().indexOf(candidate);
                        dataProvider.getList().set(i, candidate);

                        hide();
                    }

                    @Override
                    public void cancel() {
                        hide();
                    }

                    @Override
                    public void refresh() {
                        // looks like this is not required
                    }

                };
                visor = new L1FormDataReviewVisorView(this, wizardPresenter);
            }

            @Override
            public void show() {
                wizardPresenter.populate();
            }

        };

        visorController.show();
    }

    @Override
    public void fillCommonFields() {
        IVisorEditor.Controller visorController = new IVisorEditor.Controller() {

            private L1CommonFormDataVisorView visor;

            {
                visor = new L1CommonFormDataVisorView(this);
            }

            @Override
            public void show() {
                visor.populate(EntityFactory.create(L1CommonFieldsDTO.class));
                view.showVisor(visor);
            }

            @Override
            public void hide() {
                view.hideVisor();
            }

            @Override
            public void save() {
                apply();
                hide();
            }

            @Override
            public void apply() {
                L1DelinquentLeaseSearchActivity.this.setCommonFields(visor.getValue());
            }

        };
        visorController.show();
    }

    private List<LegalActionCandidateDTO> makeMockCandidates() {
        List<LegalActionCandidateDTO> mockCandidates = new LinkedList<LegalActionCandidateDTO>();
        for (int i = 1; i < 101; ++i) {
            mockCandidates.add(makeMockCandidate(i));
        }
        return mockCandidates;
    }

    private LegalActionCandidateDTO makeMockCandidate(int n) {
        LegalActionCandidateDTO c = EntityFactory.create(LegalActionCandidateDTO.class);
        c.leaseIdStub().setPrimaryKey(new Key(n));
        c.propertyCode().setValue(n % 5 != 0 ? "B1" : "B2");
        c.leaseId().setValue("t00000" + n);
        c.unit().setValue("" + (100 + n));
        c.streetAddress().setValue("1234 The West Mall, Toronto ON A1A 1A1");

        c.l1FormReview().formData().owedSummary().applicationFillingFee().setValue(new BigDecimal("170.00"));
        c.l1FormReview().formData().owedSummary().total().setValue(new BigDecimal("170.00"));

        return c;
    }

    private void setCommonFields(L1CommonFieldsDTO commonFields) {
        for (LegalActionCandidateDTO candidate : selectionModel.getSelectedSet()) {
            candidate.l1FormReview().formData().landlordsContactInfos().clear();
            for (L1LandlordsContactInfo landlordsContactInfo : commonFields.landlordsContactInfos()) {
                candidate.l1FormReview().formData().landlordsContactInfos().add(landlordsContactInfo.duplicate(L1LandlordsContactInfo.class));
            }
            candidate.l1FormReview().formData().agentContactInfo().set(commonFields.agentContactInfo().duplicate());
            candidate.l1FormReview().formData().signatureData().set(commonFields.signatureData().duplicate());
            candidate.l1FormReview().formData().scheduleAndPayment().set(commonFields.paymentAndScheduling().duplicate());
        }
    }

    @Override
    public void populate() {
        // TODO Auto-generated method stub

    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub
    }

    @Override
    public AppPlace getPlace() {
        // TODO Auto-generated method stub
        return null;
    }

}
