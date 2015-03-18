/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 */
package com.propertyvista.crm.client.ui.crud.lease.application;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.CFolder;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Toolbar;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.crm.client.ui.crud.lease.application.components.ApplicationStatusFolder;
import com.propertyvista.crm.client.ui.crud.lease.application.components.FinancialViewForm;
import com.propertyvista.crm.client.ui.crud.lease.application.components.LeaseApplicationDocumentFolder;
import com.propertyvista.crm.client.ui.crud.lease.application.components.TenantInfoViewForm;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseFormBase;
import com.propertyvista.domain.contact.LegalAddress;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.policy.policies.ProspectPortalPolicy.FeePayment;
import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.domain.tenant.lease.LeaseApplication.ApprovalChecklistItem;
import com.propertyvista.domain.tenant.lease.LeaseApplication.ApprovalChecklistItem.StatusSelectionItem;
import com.propertyvista.domain.tenant.prospect.LeaseApplicationDocument;
import com.propertyvista.domain.tenant.prospect.MasterOnlineApplication;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseApplicationForm extends LeaseFormBase<LeaseApplicationDTO> {

    private FormPanel onlineStatusPanel;

    private FormPanel onlineIndividualAppPanel;

    private FormPanel onlineAppFeePanel;

    private final Tab chargesTab, approvalTab;

    private final ApprovalChecklistItemFolder approvalChecklistFolder = new ApprovalChecklistItemFolder();

    public LeaseApplicationForm(IPrimeFormView<LeaseApplicationDTO, ?> view) {
        super(LeaseApplicationDTO.class, view);

        selectTab(addTab(createDetailsTab(), i18n.tr("Details")));
        addTab(createInfoTab(), i18n.tr("Information"));
        chargesTab = addTab(createChargesTab(), i18n.tr("Potential Charges"));
        addTab(((LeaseApplicationViewerView) getParentView()).getPaymentListerView().asWidget(), i18n.tr("Payments"),
                DataModelPermission.permissionRead(PaymentRecordDTO.class));
        addTab(createFinancialTab(), i18n.tr("Financial"), DataModelPermission.permissionRead(TenantFinancialDTO.class));
        addTab(createApplicationDocumentsTab(), i18n.tr("Documents"), DataModelPermission.permissionRead(LeaseApplicationDocument.class));
        addTab(createSummaryTab(), i18n.tr("Summary"));
        approvalTab = addTab(createApprovalChecklistTab(), i18n.tr("Approval Checklist"));
    }

    @Override
    public void onReset() {
        super.onReset();

        get(proto().currentTerm()).setNote(null);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        // dynamic tabs visibility management:
        chargesTab.setTabVisible(getValue().status().getValue().isDraft() && !getValue().billingPreview().isNull());
        approvalTab.setTabVisible(!getValue().leaseApplication().approvalChecklist().isEmpty());
        approvalChecklistFolder.setModifyable(approvalTab.isTabVisible() && getValue().status().getValue().isDraft());

        get(proto().leaseApplication().applicationId()).setVisible(true);
        get(proto().leaseApplication().yardiApplicationId()).setVisible(VistaFeatures.instance().yardiIntegration());

        get(proto().applicationId()).setVisible(true);
        get(proto().yardiApplicationId()).setVisible(VistaFeatures.instance().yardiIntegration());

        if (onlineStatusPanel != null) {
            boolean isAppCancelled = MasterOnlineApplication.Status.Cancelled.equals(getValue().leaseApplication().onlineApplication().status().getValue());

            onlineStatusPanel.setVisible(!getValue().leaseApplication().onlineApplication().isNull());
            get(proto().masterApplicationStatus().progress()).setVisible(isAppCancelled);
            onlineIndividualAppPanel.setVisible(!isAppCancelled);
            onlineAppFeePanel.setVisible(!FeePayment.none.equals(getValue().leaseApplication().onlineApplication().feePayment().getValue()) && !isAppCancelled);
        }

        // show processing result:
        get(proto().leaseApplication().submission()).setVisible(!getValue().leaseApplication().submission().isEmpty());
        get(proto().leaseApplication().validation()).setVisible(!getValue().leaseApplication().validation().isEmpty());
        get(proto().leaseApplication().approval()).setVisible(!getValue().leaseApplication().approval().isEmpty());

        get(proto().currentTerm()).setNote(getValue().currentTermNote().getValue(), NoteStyle.Warn);
    }

    private IsWidget createInfoTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, inject(proto().tenantInfo(), createTenantView()));

        return formPanel;
    }

    private IsWidget createFinancialTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, inject(proto().tenantFinancials(), createFinancialView()));

        return formPanel;
    }

    private CFolder<TenantInfoDTO> createTenantView() {
        VistaBoxFolder<TenantInfoDTO> folder = new VistaBoxFolder<TenantInfoDTO>(TenantInfoDTO.class, false) {
            @Override
            protected CForm<TenantInfoDTO> createItemForm(IObject<?> member) {
                return new TenantInfoViewForm();
            }

            @Override
            public VistaBoxFolderItemDecorator<TenantInfoDTO> createItemDecorator() {
                VistaBoxFolderItemDecorator<TenantInfoDTO> decor = super.createItemDecorator();
                decor.setExpended(false);
                return decor;
            }
        };
        folder.setNoDataLabel(i18n.tr("No tenants data has been entered yet. Navigate Views->Tenants/Guarantors to view/edit"));
        return folder;
    }

    private CFolder<TenantFinancialDTO> createFinancialView() {
        VistaBoxFolder<TenantFinancialDTO> folder = new VistaBoxFolder<TenantFinancialDTO>(TenantFinancialDTO.class, false) {
            @Override
            protected CForm<TenantFinancialDTO> createItemForm(IObject<?> member) {
                return new FinancialViewForm();
            }

            @Override
            public VistaBoxFolderItemDecorator<TenantFinancialDTO> createItemDecorator() {
                VistaBoxFolderItemDecorator<TenantFinancialDTO> decor = super.createItemDecorator();
                decor.setExpended(false);
                return decor;
            }
        };
        folder.setNoDataLabel(i18n.tr("No financial data has been entered yet. Navigate Views->Tenants/Guarantors to view/edit"));
        return folder;
    }

    private IsWidget createSummaryTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().unit().info().legalAddress(), new CEntityLabel<LegalAddress>()).decorate();

        formPanel.append(Location.Left, proto().applicationId()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().yardiApplicationId()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().leaseApplication().status(), new CEnumLabel()).decorate().componentWidth(180);

        formPanel.append(Location.Dual, proto().leaseApplication().submission(), new DecisionInfoForm(i18n.tr("Submission info:")));
        formPanel.append(Location.Dual, proto().leaseApplication().validation(), new DecisionInfoForm(i18n.tr("Validation info:")));
        formPanel.append(Location.Dual, proto().leaseApplication().approval(), new DecisionInfoForm(i18n.tr("Approve/Decline/Cancel info:")));

        if (VistaFeatures.instance().onlineApplication()) {
            formPanel.append(Location.Dual, onlineStatusPanel = createOnlineStatusPanel());
        }

        if (VistaFeatures.instance().countryOfOperation() == CountryOfOperation.Canada) {
            formPanel.br();

            formPanel.h1(i18n.tr("Credit Check"));
            formPanel.append(Location.Left, proto().leaseApproval().percenrtageApproved()).decorate().componentWidth(80);
            formPanel.append(Location.Left, proto().leaseApproval().totalAmountApproved()).decorate().componentWidth(120);
            formPanel.append(Location.Left, proto().leaseApproval().rentAmount()).decorate().componentWidth(120);

            formPanel.br();

            formPanel.append(Location.Left, proto().leaseApproval().recommendedDecision()).decorate();
        }

        formPanel.br();

        formPanel.append(Location.Dual, proto().leaseApproval().participants(), new LeaseParticipanApprovalFolder(
                ((LeaseApplicationViewerView) getParentView())));

        return formPanel;
    }

    private IsWidget createApprovalChecklistTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().leaseApplication().approvalChecklist(), approvalChecklistFolder);

        return formPanel;
    }

    private class ApprovalChecklistItemFolder extends VistaBoxFolder<ApprovalChecklistItem> {

        public ApprovalChecklistItemFolder() {
            super(ApprovalChecklistItem.class, false);
        }

        @Override
        public void setModifyable(boolean modifyable) {
            for (CComponent<?, ?, ?, ?> item : getComponents()) {
                ((ApprovalChecklistItemEditor) ((CFolderItem<?>) item).getComponents().iterator().next()).setModifyable(modifyable);
            }
        }

        @Override
        protected CForm<? extends ApprovalChecklistItem> createItemForm(IObject<?> member) {
            return new ApprovalChecklistItemEditor();
        }

        private class ApprovalChecklistItemEditor extends CForm<ApprovalChecklistItem> {

            private final CComboBox<String> statusSelector = new CComboBox<>();

            private final Anchor update = new Anchor(i18n.tr("Update Status"), new Command() {
                @Override
                public void execute() {
                    setActive(true);
                }
            });

            private final Button save = new Button(i18n.tr("Save"), new Command() {
                @Override
                public void execute() {
                    setActive(false);

                    ((LeaseApplicationViewerView.Presenter) getParentView().getPresenter()).updateApprovalTaskItem(
                            new DefaultAsyncCallback<ApprovalChecklistItem>() {
                                @Override
                                public void onSuccess(ApprovalChecklistItem result) {
                                    setValue(result);
                                }
                            }, getValue());
                }
            });

            private final Anchor cancel = new Anchor(i18n.tr("Cancel"), new Command() {
                @Override
                public void execute() {
                    setActive(false);
                }
            });

            private final Toolbar buttons = new Toolbar();

            public ApprovalChecklistItemEditor() {
                super((ApprovalChecklistItem.class));

                buttons.addItem(update);
                buttons.addItem(save);
                buttons.addItem(cancel);
            }

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().task()).decorate();

                formPanel.append(Location.Left, proto().decidedBy()).decorate();
                formPanel.append(Location.Left, proto().decisionDate()).decorate();

                formPanel.append(Location.Right, proto().status(), statusSelector).decorate();
                formPanel.append(Location.Right, proto().notes()).decorate();

                if (SecurityController.check(DataModelPermission.permissionUpdate(LeaseApplicationDTO.class))) {
                    formPanel.append(Location.Left, buttons);
                }

                // tweaks:
                get(proto().status()).inheritViewable(false);
                get(proto().status()).inheritEditable(false);

                get(proto().notes()).inheritViewable(false);
                get(proto().notes()).inheritEditable(false);

                setModifyable(false);
                setActive(false);

                return formPanel;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                if (populate) {
                    Collection<String> statusesToSelect = new ArrayList<>(getValue().statusesToSelect().size());
                    for (StatusSelectionItem item : getValue().statusesToSelect()) {
                        statusesToSelect.add(item.statusSelection().getValue());
                    }
                    statusSelector.setOptions(statusesToSelect);
                }
            }

            public void setModifyable(boolean modifyable) {
                buttons.asWidget().setVisible(modifyable);
            }

            private void setActive(boolean active) {
                get(proto().status()).setViewable(!active);
                get(proto().status()).setEditable(active);

                get(proto().notes()).setViewable(!active);
                get(proto().notes()).setEditable(active);

                update.setVisible(!active);
                save.setVisible(active);
                cancel.setVisible(active);
            }
        }
    }

    private FormPanel createOnlineStatusPanel() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h2(i18n.tr("Online Status Details"));
        formPanel.append(Location.Left, proto().leaseApplication().onlineApplication().status()).decorate();
        formPanel.append(Location.Left, proto().masterApplicationStatus().progress()).decorate();

        onlineIndividualAppPanel = new FormPanel(this);
        onlineIndividualAppPanel.h4(i18n.tr("Status Details per customer"));
        onlineIndividualAppPanel.append(Location.Dual, proto().masterApplicationStatus().individualApplications(), new ApplicationStatusFolder());
        formPanel.append(Location.Dual, onlineIndividualAppPanel);

        onlineAppFeePanel = new FormPanel(this);
        onlineAppFeePanel.h4(i18n.tr("Application Fee to check"));
        onlineAppFeePanel.append(Location.Left, proto().leaseApplication().onlineApplication().feeAmount()).decorate();
        onlineAppFeePanel.append(Location.Left, proto().leaseApplication().onlineApplication().feePayment()).decorate();
        formPanel.append(Location.Dual, onlineAppFeePanel);

        return formPanel;
    }

    private IsWidget createApplicationDocumentsTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().applicationDocuments(), new LeaseApplicationDocumentFolder());
        return formPanel;
    }

    public class DecisionInfoForm extends CForm<LeaseApplication.DecisionInfo> {
        String caption;

        public DecisionInfoForm(String caption) {
            super(LeaseApplication.DecisionInfo.class);
            this.caption = caption;
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.h4(caption);
            formPanel.append(Location.Left, proto().decidedBy()).decorate();
            formPanel.append(Location.Left, proto().decisionDate()).decorate();
            formPanel.append(Location.Left, proto().decisionReason()).decorate();

            return formPanel;
        }
    }
}