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
package com.propertyvista.crm.client.ui.crud.customer.tenant;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMoneyField.MoneyFormat;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.components.folders.PapFolder;
import com.propertyvista.crm.client.activity.crud.customer.tenant.TenantEditorActivity;
import com.propertyvista.crm.client.ui.crud.customer.common.LeaseParticipantForm;
import com.propertyvista.crm.client.ui.crud.lease.application.components.EmergencyContactFolder;
import com.propertyvista.crm.client.ui.crud.lease.insurance.TenantInsuranceCertificateFolder;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.dto.PreauthorizedPaymentDTO;
import com.propertyvista.dto.TenantDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class TenantForm extends LeaseParticipantForm<TenantDTO> {

    private static final I18n i18n = I18n.get(TenantForm.class);

    private final Tab paymentMethodsTab, autoPaymentsTab;

    private EmergencyContactFolder emergencyContactFolder;

    public TenantForm(IPrimeFormView<TenantDTO, ?> view) {
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

        addScreeningTabs();
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

// TODO: currently we do not want to validate tenant data in CRM:
//        emergencyContactFolder.setRestrictions(getValue().emergencyContactsIsMandatory().isBooleanTrue(), getValue().emergencyContactsAmount().getValue());
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
                if (getCComponent().getValue() == null || getValue() == null) {
                    return null;
                }

                return !EntityGraph.hasBusinessDuplicates(getValue().customer().emergencyContacts()) ? null : new BasicValidationError(getCComponent(), i18n
                        .tr("Duplicate Emergency Contacts specified"));
            }
        });
    }

    private IsWidget createContactsTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().customer().emergencyContacts(), emergencyContactFolder = new EmergencyContactFolder(isEditable()));
        return formPanel;
    }

    protected IsWidget createPreauthorizedPaymentsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().nextScheduledPaymentDate()).decorate().labelWidth(200).componentWidth(120);
        formPanel.h3(proto().preauthorizedPayments().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().preauthorizedPayments(), new PapFolder() {
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
            protected List<LeasePaymentMethod> getPaymentMethods() {
                return TenantForm.this.getValue().paymentMethods();
            }
        });

        return formPanel;
    }

    private IsWidget createTenantInsuranceTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Requirements"));
        formPanel.append(Location.Left, proto().minimumRequiredLiability(), new CMoneyLabel()).decorate().labelWidth(200).componentWidth(150);
        ((CMoneyLabel) get(proto().minimumRequiredLiability())).setFormatter(new MoneyFormat() {
            @Override
            public String format(BigDecimal value) {
                return (value == null ? "None" : super.format(value));
            }
        });

        formPanel.h1(i18n.tr("Insurance Certificates"));
        formPanel.append(Location.Dual, proto().insuranceCertificates(), new TenantInsuranceCertificateFolder(false));

        return formPanel;
    }

    private void updateTenantInsuranceTabControls() {
        if (!isEditable() & VistaFeatures.instance().yardiIntegration()) {
            get(proto().leaseTermV()).setVisible(!getValue().isPotentialTenant().getValue(false));
        }
    }

    @Override
    protected void onPaymentMethodRemove(LeasePaymentMethod lpm) {
        PapFolder ppf = ((PapFolder) (CComponent<?, ?, ?, ?>) get(proto().preauthorizedPayments()));
        IList<PreauthorizedPaymentDTO> items = ppf.getValue();
        Iterator<PreauthorizedPaymentDTO> it = items.iterator();
        while (it.hasNext()) {
            if (it.next().paymentMethod().equals(lpm)) {
                it.remove();
            }
        }
        ppf.populate(items);
    }

}