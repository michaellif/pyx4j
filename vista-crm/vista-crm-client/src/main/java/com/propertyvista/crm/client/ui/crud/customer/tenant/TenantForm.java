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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.customer.tenant;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.dialogs.AbstractEntitySelectorDialog;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntitySelectorLabel;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.components.folders.PapCoveredItemDtoFolder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.activity.crud.customer.tenant.TenantEditorActivity;
import com.propertyvista.crm.client.ui.crud.customer.common.LeaseParticipantForm;
import com.propertyvista.crm.client.ui.crud.lease.TenantInsuranceCertificateFolder;
import com.propertyvista.crm.client.ui.crud.lease.application.components.EmergencyContactFolder;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.security.common.AbstractPmcUser;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.dto.PreauthorizedPaymentDTO;
import com.propertyvista.dto.TenantDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class TenantForm extends LeaseParticipantForm<TenantDTO> {

    private static final I18n i18n = I18n.get(TenantForm.class);

    private Label noRequirementsLabel;

    private final Tab paymentMethodsTab, autoPaymentsTab, insuranceTab;

    public TenantForm(IForm<TenantDTO> view) {
        super(TenantDTO.class, view);

        selectTab(addTab(createDetailsTab(i18n.tr("Details"))));
        addTab(createContactsTab(i18n.tr("Emergency Contacts")));
        paymentMethodsTab = addTab(createPaymentMethodsTab(i18n.tr("Payment Methods")));
        autoPaymentsTab = addTab(createPreauthorizedPaymentsTab(i18n.tr("Auto Payments")));
        insuranceTab = addTab(createTenantInsuranceTab(i18n.tr("Insurance")));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        boolean financialyEligible = (getValue().role().getValue() != Role.Dependent);
        Lease.Status leaseStatus = getValue().lease().status().getValue();

        setTabVisible(paymentMethodsTab, financialyEligible && (leaseStatus.isDraft() || leaseStatus.isCurrent()));
        setTabVisible(autoPaymentsTab, financialyEligible && !getValue().lease().status().getValue().isNoAutoPay());
        setTabVisible(insuranceTab, financialyEligible && (leaseStatus.isDraft() || leaseStatus.isCurrent()));

        get(proto().preauthorizedPayments()).setEditable(!getValue().isMoveOutWithinNextBillingCycle().getValue(false));

        updateTenantInsuranceTabControls();
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().customer().emergencyContacts()).addComponentValidator(new AbstractComponentValidator<List<EmergencyContact>>() {
            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() == null || getValue() == null) {
                    return null;
                }

                return !EntityGraph.hasBusinessDuplicates(getValue().customer().emergencyContacts()) ? null : new FieldValidationError(getComponent(), i18n
                        .tr("Duplicate Emergency Contacts specified"));
            }
        });

        get(proto().customer().person().sex()).setMandatory(!VistaFeatures.instance().yardiIntegration());
    }

    private TwoColumnFlexFormPanel createContactsTab(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);

        main.setWidget(0, 0, 2, inject(proto().customer().emergencyContacts(), new EmergencyContactFolder(isEditable())));

        return main;
    }

    protected TwoColumnFlexFormPanel createPreauthorizedPaymentsTab(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);
        int row = -1;

        main.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().nextScheduledPaymentDate(), new CDateLabel()), true).labelWidth("20em").build());

        main.setH3(++row, 0, 2, proto().preauthorizedPayments().getMeta().getCaption());
        main.setWidget(++row, 0, 2, inject(proto().preauthorizedPayments(), new PreauthorizedPaymentFolder()));

        return main;
    }

    private TwoColumnFlexFormPanel createTenantInsuranceTab(String title) {
        TwoColumnFlexFormPanel tabPanel = new TwoColumnFlexFormPanel(title);
        int row = -1;

        tabPanel.setH1(++row, 0, 2, i18n.tr("Requirements"));
        tabPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().minimumRequiredLiability()), true).build());
        get(proto().minimumRequiredLiability()).setEditable(false);

        noRequirementsLabel = new Label(i18n.tr("None"));
        noRequirementsLabel.setVisible(false);
        tabPanel.setWidget(++row, 0, 2, noRequirementsLabel);

        tabPanel.setH1(++row, 0, 2, i18n.tr("Insurance Certificates"));
        tabPanel.setWidget(++row, 0, 2, inject(proto().insuranceCertificates(), new TenantInsuranceCertificateFolder(null)));

        return tabPanel;
    }

    private void updateTenantInsuranceTabControls() {
        (get(proto().minimumRequiredLiability())).setVisible(!getValue().minimumRequiredLiability().isNull());
        noRequirementsLabel.setVisible(getValue().minimumRequiredLiability().isNull());

        if (!isEditable() & VistaFeatures.instance().yardiIntegration()) {
            get(proto().leaseTermV()).setVisible(!getValue().isPotentialTenant().isBooleanTrue());
        }
    }

    @Override
    protected void onPaymentMethodRemove(LeasePaymentMethod lpm) {
        PreauthorizedPaymentFolder ppf = ((PreauthorizedPaymentFolder) (CComponent<?>) get(proto().preauthorizedPayments()));
        IList<PreauthorizedPaymentDTO> items = ppf.getValue();
        Iterator<PreauthorizedPaymentDTO> it = items.iterator();
        while (it.hasNext()) {
            if (it.next().paymentMethod().equals(lpm)) {
                it.remove();
            }
        }
        ppf.populate(items);
    }

    private class PreauthorizedPaymentFolder extends VistaBoxFolder<PreauthorizedPaymentDTO> {

        public PreauthorizedPaymentFolder() {
            super(PreauthorizedPaymentDTO.class, true);
            setOrderable(false);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof PreauthorizedPaymentDTO) {
                return new PreauthorizedPaymentEditor();
            }
            return super.create(member);
        }

        @Override
        protected void createNewEntity(final AsyncCallback<PreauthorizedPaymentDTO> callback) {
            ((TenantEditorActivity) getParentView().getPresenter()).createPreauthorizedPayment(new DefaultAsyncCallback<PreauthorizedPaymentDTO>() {
                @Override
                public void onSuccess(PreauthorizedPaymentDTO result) {
                    callback.onSuccess(result);
                }
            });
        }

        @Override
        protected void removeItem(final CEntityFolderItem<PreauthorizedPaymentDTO> item) {
            MessageDialog.confirm(i18n.tr("Please confirm"), i18n.tr("Do you really want to delete the Pre-Authorized Payment?"), new Command() {
                @Override
                public void execute() {
                    PreauthorizedPaymentFolder.super.removeItem(item);
                }
            });
        }

        private class PreauthorizedPaymentEditor extends CEntityForm<PreauthorizedPaymentDTO> {

            public PreauthorizedPaymentEditor() {
                super(PreauthorizedPaymentDTO.class);
            }

            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().id(), new CNumberLabel()), 10).build());

                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().creationDate()), 15).build());
                content.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().createdBy(), new CEntityLabel<AbstractPmcUser>()), 22).build());

                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().updated()), 15).build());

                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().paymentMethod(), new CEntitySelectorLabel<LeasePaymentMethod>() {
                    @Override
                    protected AbstractEntitySelectorDialog<LeasePaymentMethod> getSelectorDialog() {
                        return new EntitySelectorListDialog<LeasePaymentMethod>(i18n.tr("Select Payment Method"), false, TenantForm.this.getValue()
                                .paymentMethods()) {
                            @Override
                            public boolean onClickOk() {
                                get(proto().paymentMethod()).setValue(getSelectedItems().iterator().next());
                                return true;
                            }
                        };
                    }
                })).componentWidth("35em").build());

                content.setBR(++row, 0, 2);

                content.setWidget(++row, 0, 2, inject(proto().coveredItemsDTO(), new PapCoveredItemDtoFolder()));

                return content;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                get(proto().id()).setVisible(!getValue().id().isNull());
                get(proto().creationDate()).setVisible(!getValue().creationDate().isNull());
                get(proto().createdBy()).setVisible(!getValue().createdBy().isNull());
                get(proto().updated()).setVisible(!getValue().updated().isNull());
            }
        }
    }
}