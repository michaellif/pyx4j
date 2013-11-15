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
package com.propertyvista.portal.resident.ui.financial.paymentmethod;

import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.rpc.AppPlaceInfo;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.portal.resident.ui.financial.paymentmethod.editor.PaymentMethodEditor;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.PaymentMethodDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;
import com.propertyvista.portal.shared.ui.IWizardView;

public class PaymentMethodWizard extends CPortalEntityWizard<PaymentMethodDTO> {

    private static final I18n i18n = I18n.get(PaymentMethodWizard.class);

    private final WizardStep comfirmationStep;

    private final SimplePanel confirmationDetailsHolder = new SimplePanel();

    private final Anchor termsOfUseAnchor = new Anchor(i18n.tr("Terms Of Use"));

    private final PaymentMethodEditor<LeasePaymentMethod> paymentMethodEditor = new PaymentMethodEditor<LeasePaymentMethod>(LeasePaymentMethod.class) {

        @Override
        protected Set<CreditCardType> getAllowedCardTypes() {
            return PaymentMethodWizard.this.getValue().allowedCardTypes();
        }

        @Override
        public void onBillingAddressSameAsCurrentOne(boolean set, final CComponent<AddressSimple> comp) {
            if (set) {
                assert (getView().getPresenter() != null);
                ((PaymentMethodWizardView.Presenter) getView().getPresenter()).getCurrentAddress(new DefaultAsyncCallback<AddressSimple>() {
                    @Override
                    public void onSuccess(AddressSimple result) {
                        comp.setValue(result, false);
                    }
                });
            } else {
                comp.setValue(EntityFactory.create(AddressSimple.class), false);
            }
        }

        @Override
        protected String getNameOn() {
            return ClientContext.getUserVisit().getName();
        }
    };

    private PaymentMethodWizardView.Presenter presenter;

    public PaymentMethodWizard(IWizardView<PaymentMethodDTO> view) {
        super(PaymentMethodDTO.class, view, i18n.tr("Profile Payment Setup"), i18n.tr("Submit"), ThemeColor.contrast4);

        addStep(createPaymentMethodStep());
        comfirmationStep = addStep(createConfirmationStep());
    }

    public void setPresenter(PaymentMethodWizardView.Presenter presenter) {
        this.presenter = presenter;

        if (this.presenter != null) {
            this.termsOfUseAnchor.setHref(AppPlaceInfo.absoluteUrl(GWT.getModuleBaseURL(), true, this.presenter.getTermsOfUsePlace()));
        }
    }

    private BasicFlexFormPanel createPaymentMethodStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Payment Method"));

        panel.setWidget(0, 0, inject(proto().paymentMethod(), paymentMethodEditor));

        return panel;
    }

    private BasicFlexFormPanel createConfirmationStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Confirmation"));
        int row = -1;

        panel.setWidget(++row, 0, confirmationDetailsHolder);
        panel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

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
        w.setWidth("10em");
        pm.add(w = new HTML(get(proto().paymentMethod()).getValue().getStringView()));
        w.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        panel.add(pm);

        return panel;
    }

    private Widget createLegalTermsPanel() {
        termsOfUseAnchor.getElement().getStyle().setDisplay(Display.INLINE);
        termsOfUseAnchor.getElement().getStyle().setPadding(0, Unit.PX);
        termsOfUseAnchor.getElement().getStyle().setWhiteSpace(WhiteSpace.NORMAL);
        termsOfUseAnchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showTermsOfUse();
                DOM.eventPreventDefault((com.google.gwt.user.client.Event) event.getNativeEvent());
            }
        });

        Widget w;
        FlowPanel panel = new FlowPanel();

        panel.add(new HTML(i18n.tr("Be informed that you are acknowledging our")));
        panel.add(termsOfUseAnchor);

        return panel;
    }
}
