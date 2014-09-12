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

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CMoneyField.MoneyFormat;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
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
import com.propertyvista.crm.client.activity.crud.customer.tenant.TenantEditorActivity;
import com.propertyvista.crm.client.ui.crud.customer.common.LeaseParticipantForm;
import com.propertyvista.crm.client.ui.crud.lease.application.components.EmergencyContactFolder;
import com.propertyvista.crm.client.ui.crud.lease.insurance.TenantInsuranceCertificateFolder;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.security.common.AbstractPmcUser;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.dto.PreauthorizedPaymentDTO;
import com.propertyvista.dto.TenantDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class TenantForm extends LeaseParticipantForm<TenantDTO> {

    private static final I18n i18n = I18n.get(TenantForm.class);

    private final Tab paymentMethodsTab, autoPaymentsTab;

    public TenantForm(IForm<TenantDTO> view) {
        super(TenantDTO.class, view);

        selectTab(addTab(createDetailsTab(), i18n.tr("Details")));
        addTab(createContactsTab(), i18n.tr("Emergency Contacts"));
        paymentMethodsTab = addTab(createPaymentMethodsTab(), i18n.tr("Payment Methods"), DataModelPermission.permissionRead(LeasePaymentMethod.class));
        if (isEditable()) {
            paymentMethodsTab.setPermitEnabledPermission(DataModelPermission.permissionUpdate(LeasePaymentMethod.class));
        }
        autoPaymentsTab = addTab(createPreauthorizedPaymentsTab(), i18n.tr("Auto Payments"), DataModelPermission.permissionRead(PreauthorizedPaymentDTO.class));
        if (isEditable()) {
            autoPaymentsTab.setPermitEnabledPermission(DataModelPermission.permissionUpdate(PreauthorizedPaymentDTO.class));
        }
        addTab(createTenantInsuranceTab(), i18n.tr("Insurance"));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

// VISTA-3517 - reopen
//        boolean financialyEligible = (getValue().role().getValue() != Role.Dependent);
//        Lease.Status leaseStatus = getValue().lease().status().getValue();
//
//        setTabVisible(paymentMethodsTab, financialyEligible && (leaseStatus.isDraft() || leaseStatus.isCurrent()));
//        setTabVisible(autoPaymentsTab, financialyEligible && !getValue().lease().status().getValue().isNoAutoPay());
//        setTabVisible(insuranceTab, financialyEligible && (leaseStatus.isDraft() || leaseStatus.isCurrent()));

        get(proto().paymentMethods()).setEditable(getValue().electronicPaymentsAllowed().getValue(false));

        setTabVisible(autoPaymentsTab, getValue().lease().status().getValue().isCurrent());
        get(proto().preauthorizedPayments()).setEditable(
                getValue().electronicPaymentsAllowed().getValue(false) && !getValue().isMoveOutWithinNextBillingCycle().getValue(false));

        if (!getValue().electronicPaymentsAllowed().getValue(false)) {
            get(proto().paymentMethods()).setNote(i18n.tr("Merchant Account is not set up to receive Electronic Payments"), NoteStyle.Warn);
            get(proto().preauthorizedPayments()).setNote(i18n.tr("Merchant Account is not set up to receive Electronic Payments"), NoteStyle.Warn);
        }

        get(proto().nextScheduledPaymentDate()).setNote(getValue().nextAutopayApplicabilityMessage().getValue(), NoteStyle.Warn);

        updateTenantInsuranceTabControls();

    }

    @Override
    public void onReset() {
        super.onReset();
        // disable any payment-related editing if no electronic payments allowed:
        get(proto().paymentMethods()).setNote(null);
        get(proto().preauthorizedPayments()).setNote(null);
        get(proto().nextScheduledPaymentDate()).setNote(null);
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().customer().emergencyContacts()).addComponentValidator(new AbstractComponentValidator<List<EmergencyContact>>() {
            @Override
            public BasicValidationError isValid() {
                if (getComponent().getValue() == null || getValue() == null) {
                    return null;
                }

                return !EntityGraph.hasBusinessDuplicates(getValue().customer().emergencyContacts()) ? null : new BasicValidationError(getComponent(), i18n
                        .tr("Duplicate Emergency Contacts specified"));
            }
        });
    }

    private IsWidget createContactsTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().customer().emergencyContacts(), new EmergencyContactFolder(isEditable()));
        return formPanel;
    }

    protected IsWidget createPreauthorizedPaymentsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().nextScheduledPaymentDate(), new CDateLabel()).decorate().labelWidth(200).componentWidth(120);
        formPanel.h3(proto().preauthorizedPayments().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().preauthorizedPayments(), new PreauthorizedPaymentFolder());

        return formPanel;
    }

    private IsWidget createTenantInsuranceTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Requirements"));
        formPanel.append(Location.Left, proto().minimumRequiredLiability(), new CMoneyLabel()).decorate().componentWidth(150);
        ((CMoneyLabel) get(proto().minimumRequiredLiability())).setFormatter(new MoneyFormat() {
            @Override
            public String format(BigDecimal value) {
                return (value == null ? "None" : super.format(value));
            }
        });

        formPanel.h1(i18n.tr("Insurance Certificates"));
        formPanel.append(Location.Dual, proto().insuranceCertificates(), new TenantInsuranceCertificateFolder(null));

        return formPanel;
    }

    private void updateTenantInsuranceTabControls() {
        if (!isEditable() & VistaFeatures.instance().yardiIntegration()) {
            get(proto().leaseTermV()).setVisible(!getValue().isPotentialTenant().getValue(false));
        }
    }

    @Override
    protected void onPaymentMethodRemove(LeasePaymentMethod lpm) {
        PreauthorizedPaymentFolder ppf = ((PreauthorizedPaymentFolder) (CComponent<?, ?, ?>) get(proto().preauthorizedPayments()));
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
            setNoDataLabel(i18n.tr("No Auto Payments are set up"));
        }

        @Override
        protected CForm<PreauthorizedPaymentDTO> createItemForm(IObject<?> member) {
            return new PreauthorizedPaymentEditor();
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
        protected void removeItem(final CFolderItem<PreauthorizedPaymentDTO> item) {
            MessageDialog.confirm(i18n.tr("Please confirm"), i18n.tr("Do you really want to delete the Pre-Authorized Payment?"), new Command() {
                @Override
                public void execute() {
                    PreauthorizedPaymentFolder.super.removeItem(item);
                }
            });
        }

        private class PreauthorizedPaymentEditor extends CForm<PreauthorizedPaymentDTO> {

            public PreauthorizedPaymentEditor() {
                super(PreauthorizedPaymentDTO.class);
            }

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().id(), new CNumberLabel()).decorate().componentWidth(120);
                formPanel.append(Location.Left, proto().paymentMethod(), new CEntitySelectorLabel<LeasePaymentMethod>() {
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
                }).decorate();

                formPanel.append(Location.Right, proto().creationDate()).decorate().componentWidth(180);
                formPanel.append(Location.Right, proto().createdBy(), new CEntityLabel<AbstractPmcUser>()).decorate().componentWidth(200);
                formPanel.append(Location.Right, proto().updated()).decorate().componentWidth(180);

                formPanel.br();

                formPanel.append(Location.Dual, proto().coveredItemsDTO(), new PapCoveredItemDtoFolder());

                return formPanel;
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