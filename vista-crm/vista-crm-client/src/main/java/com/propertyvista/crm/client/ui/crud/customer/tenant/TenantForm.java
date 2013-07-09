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

import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.ui.dialogs.AbstractEntitySelectorDialog;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntitySelectorLabel;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.EmergencyContactFolder;
import com.propertyvista.common.client.ui.components.folders.PapCoveredItemDtoFolder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.activity.crud.customer.tenant.TenantEditorActivity;
import com.propertyvista.crm.client.ui.crud.customer.common.LeaseParticipantForm;
import com.propertyvista.crm.client.ui.crud.lease.TenantInsuranceCertificateFolder;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.dto.PreauthorizedPaymentDTO;
import com.propertyvista.dto.TenantDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class TenantForm extends LeaseParticipantForm<TenantDTO> {

    private static final I18n i18n = I18n.get(TenantForm.class);

    private static String cutOffDateWarning = i18n.tr("All changes will take effect after this date!");

    private Label noRequirementsLabel;

    private final Tab autoPaymentsTab;

    public TenantForm(IForm<TenantDTO> view) {
        super(TenantDTO.class, view);

        selectTab(addTab(createDetailsTab(i18n.tr("Details"))));
        addTab(createContactsTab(i18n.tr("Emergency Contacts")));
        addTab(createPaymentMethodsTab(i18n.tr("Payment Methods")));
        autoPaymentsTab = addTab(createPreauthorizedPaymentsTab(i18n.tr("Auto Payments")));
        addTab(createTenantInsuranceTab(i18n.tr("Insurance")));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        LogicalDate today = new LogicalDate(ClientContext.getServerDate());
        if (!today.before(getValue().paymentCutOffDate().getValue()) && !today.after(getValue().nextScheduledPaymentDate().getValue())) {
            get(proto().nextScheduledPaymentDate()).setNote(cutOffDateWarning, NoteStyle.Warn);
        } else {
            get(proto().nextScheduledPaymentDate()).setNote(null);
        }

        setTabVisible(autoPaymentsTab, getValue().lease().status().getValue().isCurrent());

        updateTenantInsuranceTabControls();
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().customer().emergencyContacts()).addValueValidator(new EditableValueValidator<List<EmergencyContact>>() {
            @Override
            public ValidationError isValid(CComponent<List<EmergencyContact>> component, List<EmergencyContact> value) {
                if (value == null || getValue() == null) {
                    return null;
                }

                if (!VistaFeatures.instance().yardiIntegration() & value.isEmpty()) {
                    return new ValidationError(component, i18n.tr("Empty Emergency Contacts list"));
                }

                return !EntityGraph.hasBusinessDuplicates(getValue().customer().emergencyContacts()) ? null : new ValidationError(component, i18n
                        .tr("Duplicate Emergency Contacts specified"));
            }
        });

        get(proto().customer().person().sex()).setMandatory(!VistaFeatures.instance().yardiIntegration());
        get(proto().customer().person().birthDate()).setMandatory(!VistaFeatures.instance().yardiIntegration());

        get(proto().customer().emergencyContacts()).setMandatory(!VistaFeatures.instance().yardiIntegration());
    }

    private FormFlexPanel createContactsTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        main.setWidget(0, 0, inject(proto().customer().emergencyContacts(), new EmergencyContactFolder(isEditable())));

        return main;
    }

    protected FormFlexPanel createPreauthorizedPaymentsTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);
        int row = -1;

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().nextScheduledPaymentDate(), new CDateLabel()), 7, 20).build());

        main.setH3(++row, 0, 1, proto().preauthorizedPayments().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().preauthorizedPayments(), new PreauthorizedPaymentFolder()));

        return main;
    }

    private FormFlexPanel createTenantInsuranceTab(String title) {
        FormFlexPanel tabPanel = new FormFlexPanel(title);
        int row = -1;
        tabPanel.setH1(++row, 0, 1, i18n.tr("Requirements"));
        tabPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().minimumRequiredLiability()), 15).build());
        get(proto().minimumRequiredLiability()).setViewable(true);

        noRequirementsLabel = new Label(i18n.tr("None"));
        noRequirementsLabel.setVisible(false);
        tabPanel.setWidget(++row, 0, noRequirementsLabel);

        tabPanel.setH1(++row, 0, 1, i18n.tr("Insurance Certificates"));
        tabPanel.setWidget(++row, 0, inject(proto().insuranceCertificates(), new TenantInsuranceCertificateFolder(null)));
        return tabPanel;
    }

    private void updateTenantInsuranceTabControls() {
        (get(proto().minimumRequiredLiability())).setVisible(!getValue().minimumRequiredLiability().isNull());
        noRequirementsLabel.setVisible(getValue().minimumRequiredLiability().isNull());

        if (!isEditable() & VistaFeatures.instance().yardiIntegration()) {
            get(proto().leaseTermV()).setVisible(!getValue().isPotentialTenant().isBooleanTrue());
        }
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
            MessageDialog.confirm(i18n.tr("Please confirm"), i18n.tr("Do you really want to delete the Preauthorized Payment?"), new Command() {
                @Override
                public void execute() {
                    PreauthorizedPaymentFolder.super.removeItem(item);
                }
            });
        }

        private class PreauthorizedPaymentEditor extends CEntityDecoratableForm<PreauthorizedPaymentDTO> {

            private final FormFlexPanel expirationWarning = new FormFlexPanel();

            public PreauthorizedPaymentEditor() {
                super(PreauthorizedPaymentDTO.class);

                Widget expirationWarningLabel = new HTML(i18n.tr("This Preauthorized Payment is expired - needs to be replaced with new one!"));
                expirationWarningLabel.setStyleName(VistaTheme.StyleName.warningMessage.name());
                expirationWarning.setWidget(0, 0, expirationWarningLabel);
                expirationWarning.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
                expirationWarning.setHR(1, 0, 1);
                expirationWarning.setBR(2, 0, 1);
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel content = new FormFlexPanel();
                int row = -1;

                content.setWidget(++row, 0, expirationWarning);

                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().paymentMethod(), new CEntitySelectorLabel<LeasePaymentMethod>() {
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
                }), 38, 10).build());

                content.setBR(++row, 0, 1);

                content.setWidget(++row, 0, inject(proto().coveredItemsDTO(), new PapCoveredItemDtoFolder()));

                return content;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                setEditable(getValue().expiring().isNull());
                expirationWarning.setVisible(!getValue().expiring().isNull());
            }
        }
    }

}