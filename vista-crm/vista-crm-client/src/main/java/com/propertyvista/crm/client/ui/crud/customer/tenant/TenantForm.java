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

import com.google.gwt.user.client.ui.Label;

import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.folders.EmergencyContactFolder;
import com.propertyvista.crm.client.ui.crud.customer.common.LeaseParticipantForm;
import com.propertyvista.crm.client.ui.crud.lease.TenantInsuranceCertificateFolder;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.dto.TenantDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class TenantForm extends LeaseParticipantForm<TenantDTO> {

    private static final I18n i18n = I18n.get(TenantForm.class);

    private Label noRequirementsLabel;

    public TenantForm(IForm<TenantDTO> view) {
        super(TenantDTO.class, view);

        selectTab(addTab(createDetailsTab(i18n.tr("Details"))));
        addTab(createContactsTab(i18n.tr("Emergency Contacts")));
        addTab(createPaymentMethodsTab(i18n.tr("Payment Methods")));
        addTab(createTenantInsuranceTab(i18n.tr("Insurance")));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        updateTenantInsuranceTabControls();
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().customer().emergencyContacts()).addValueValidator(new EditableValueValidator<List<EmergencyContact>>() {
            @Override
            public ValidationError isValid(CComponent<List<EmergencyContact>, ?> component, List<EmergencyContact> value) {
                if (value == null || getValue() == null) {
                    return null;
                }

                if (value.isEmpty()) {
                    return new ValidationError(component, i18n.tr("Empty Emergency Contacts list"));
                }

                return !EntityGraph.hasBusinessDuplicates(getValue().customer().emergencyContacts()) ? null : new ValidationError(component, i18n
                        .tr("Duplicate Emergency Contacts specified"));
            }
        });
    }

    private FormFlexPanel createContactsTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        main.setWidget(0, 0, inject(proto().customer().emergencyContacts(), new EmergencyContactFolder(isEditable())));

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

        if (VistaFeatures.instance().yardiIntegration()) {
            boolean isPotentialTenant = getValue().leaseTermV().holder().status().getValue() != LeaseTerm.Status.Current
                    | getValue().leaseTermV().holder().status().getValue() != LeaseTerm.Status.Historic;
            get(proto().leaseTermV()).setVisible(isPotentialTenant);
        }
    }
}