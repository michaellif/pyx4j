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
package com.propertyvista.portal.client.ui.residents.paymentmethod;

import java.util.Collection;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.ui.prime.wizard.IWizard;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodForm;
import com.propertyvista.common.client.ui.wizard.VistaWizardForm;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.portal.client.ui.residents.LegalTermsDialog;
import com.propertyvista.portal.client.ui.residents.LegalTermsDialog.TermsType;
import com.propertyvista.portal.client.ui.residents.payment.PortalPaymentTypesUtil;
import com.propertyvista.portal.rpc.portal.dto.PaymentMethodDTO;

public class PaymentMethodWizardForm extends VistaWizardForm<PaymentMethodDTO> {

    private static final I18n i18n = I18n.get(PaymentMethodWizardForm.class);

    private final WizardStep comfirmationStep;

    private final SimplePanel confirmationDetailsHolder = new SimplePanel();

    private final PaymentMethodForm<LeasePaymentMethod> paymentMethodEditor = new PaymentMethodForm<LeasePaymentMethod>(LeasePaymentMethod.class) {
        @Override
        public Collection<PaymentType> defaultPaymentTypes() {
            return PortalPaymentTypesUtil.getAllowedPaymentTypes();
        }

        @Override
        public void onBillingAddressSameAsCurrentOne(boolean set, final CComponent<AddressStructured> comp) {
            if (set) {
                assert (getView().getPresenter() != null);
                ((PaymentMethodWizardView.Persenter) getView().getPresenter()).getCurrentAddress(new DefaultAsyncCallback<AddressStructured>() {
                    @Override
                    public void onSuccess(AddressStructured result) {
                        comp.setValue(result, false);
                    }
                });
            } else {
                comp.setValue(EntityFactory.create(AddressStructured.class), false);
            }
        }

        @Override
        protected String getNameOn() {
            return ClientContext.getUserVisit().getName();
        }
    };

    public PaymentMethodWizardForm(IWizard<PaymentMethodDTO> view) {
        super(PaymentMethodDTO.class, view);

        addStep(createPaymentMethodStep());
        comfirmationStep = addStep(createConfirmationStep());
    }

    private TwoColumnFlexFormPanel createPaymentMethodStep() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Payment Method"));

        panel.setWidget(0, 0, inject(proto().paymentMethod(), paymentMethodEditor));

        return panel;
    }

    private TwoColumnFlexFormPanel createConfirmationStep() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Confirmation"));
        int row = -1;

        panel.setWidget(++row, 0, confirmationDetailsHolder);

        panel.setBR(++row, 0, 1);
        panel.setHR(++row, 0, 1);

        panel.setWidget(++row, 0, createLegalTermsPanel());
        panel.getFlexCellFormatter().setAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);

        return panel;
    }

    @Override
    protected void onStepChange(SelectionEvent<WizardStep> event) {
        super.onStepChange(event);
        if (event.getSelectedItem().equals(comfirmationStep)) {
            confirmationDetailsHolder.clear();
            confirmationDetailsHolder.setWidget(createConfirmationDetailsPanel());
        }
    }

    @Override
    protected void onValueSet(final boolean populate) {
        super.onValueSet(populate);

        paymentMethodEditor.setElectronicPaymentsEnabled(getValue().electronicPaymentsAllowed().getValue(Boolean.FALSE));
    }

    private Widget createConfirmationDetailsPanel() {
        VerticalPanel panel = new VerticalPanel();
        Widget w;

        panel.add(new HTML("<br/>"));

        HorizontalPanel pm = new HorizontalPanel();
        pm.add(w = new HTML(i18n.tr("Payment Method:")));
        w.getElement().getStyle().setMarginLeft(5, Unit.EM);
        w.setWidth("10em");
        pm.add(w = new HTML(get(proto().paymentMethod()).getValue().getStringView()));
        w.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        panel.add(pm);

        return panel;
    }

    private Widget createLegalTermsPanel() {
        FlowPanel panel = new FlowPanel();
        Widget w;

        panel.add(new HTML(i18n.tr("By pressing Submit you are acknowledgeing our")));
        panel.add(w = new Anchor(i18n.tr("Terms Of Use"), new Command() {
            @Override
            public void execute() {
                new LegalTermsDialog(TermsType.TermsOfUse).show();
            }
        }));

        panel.add(w = new HTML("&nbsp" + i18n.tr("and") + "&nbsp"));
        w.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        panel.add(w = new Anchor(i18n.tr("Privacy Policy"), new Command() {
            @Override
            public void execute() {
                new LegalTermsDialog(TermsType.PrivacyPolicy).show();
            }
        }));

        return panel;
    }
}
