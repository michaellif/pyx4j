/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.services.insurance.tenantsurepaymentmethod;

import com.google.gwt.user.client.Command;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.payment.PaymentDetails;
import com.propertyvista.portal.resident.ui.services.insurance.tenantsurepaymentmethod.TenantSurePaymentMethodWizardView.Persenter;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.InsurancePaymentMethodDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;
import com.propertyvista.portal.shared.ui.IWizardView;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

public class TenantSurePaymentMethodWizard extends CPortalEntityWizard<InsurancePaymentMethodDTO> {

    private static final I18n i18n = I18n.get(TenantSurePaymentMethodWizard.class);

    private Persenter presenter;

    private final TenantSurePaymentMethodForm paymentMethodForm = new TenantSurePaymentMethodForm(new Command() {
        @Override
        public void execute() {
            presenter.getCurrentAddress();
        }
    });

    public TenantSurePaymentMethodWizard(IWizardView<InsurancePaymentMethodDTO> view) {
        super(InsurancePaymentMethodDTO.class, view, i18n.tr("TenantSure Payment Setup"), i18n.tr("Submit"), ThemeColor.contrast3);

        addStep(createDisplayCurrentPaymentMethodStep(), i18n.tr("Current Payment Method"));
        addStep(createInputNewPaymentMethodStep(), i18n.tr("New Payment Method"));
    }

    public void setPresenter(TenantSurePaymentMethodWizardView.Persenter presenter) {
        this.presenter = presenter;
    }

    public void setBillingAddress(AddressSimple address) {
        InsurancePaymentMethod paymentMethod = paymentMethodForm.getValue();
        paymentMethod.billingAddress().set(address);
        paymentMethodForm.setValue(paymentMethod);
    }

    private BasicFlexFormPanel createDisplayCurrentPaymentMethodStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        int row = -1;
        panel.setWidget(++row, 0, inject(proto().currentPaymentMethod().creationDate(), new CDateLabel(), new FieldDecoratorBuilder(100).build()));
        panel.setWidget(++row, 0, inject(proto().currentPaymentMethod().details(), new CEntityLabel<PaymentDetails>(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0,
                inject(proto().currentPaymentMethod().billingAddress(), new CEntityLabel<AddressSimple>(), new FieldDecoratorBuilder().build()));

        return panel;
    }

    private BasicFlexFormPanel createInputNewPaymentMethodStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();

        panel.setWidget(0, 0, inject(proto().newPaymentMethod(), paymentMethodForm));

        return panel;
    }

}
