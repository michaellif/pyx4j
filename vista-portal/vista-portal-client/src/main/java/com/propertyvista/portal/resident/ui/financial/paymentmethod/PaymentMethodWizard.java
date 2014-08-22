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

import java.util.Collections;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.rpc.AppPlaceInfo;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.PaymentMethodDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;
import com.propertyvista.portal.shared.ui.IWizardView;
import com.propertyvista.portal.shared.ui.PortalFormPanel;
import com.propertyvista.portal.shared.ui.util.editors.PortalPaymentMethodEditor;

public class PaymentMethodWizard extends CPortalEntityWizard<PaymentMethodDTO> {

    private static final I18n i18n = I18n.get(PaymentMethodWizard.class);

    private final WizardStep comfirmationStep;

    private final SimplePanel confirmationDetailsHolder = new SimplePanel();

    private final Anchor termsOfUseAnchor = new Anchor(i18n.tr("Terms Of Use"));

    private final PortalPaymentMethodEditor<LeasePaymentMethod> paymentMethodEditor = new PortalPaymentMethodEditor<LeasePaymentMethod>(
            LeasePaymentMethod.class) {

        @Override
        public Set<PaymentType> getDefaultPaymentTypes() {
            if (PaymentMethodWizard.this.getValue() != null) {
                return PaymentMethodWizard.this.getValue().allowedPaymentsSetup().allowedPaymentTypes();
            }
            return Collections.emptySet();
        }

        @Override
        protected Set<CreditCardType> getAllowedCardTypes() {
            if (PaymentMethodWizard.this.getValue() != null) {
                return PaymentMethodWizard.this.getValue().allowedPaymentsSetup().allowedCardTypes();
            }
            return Collections.emptySet();
        }

        @Override
        protected Set<CreditCardType> getConvienceFeeApplicableCardTypes() {
            if (PaymentMethodWizard.this.getValue() != null) {
                return PaymentMethodWizard.this.getValue().allowedPaymentsSetup().convenienceFeeApplicableCardTypes();
            }
            return Collections.emptySet();
        };

        @Override
        public void onBillingAddressSameAsCurrentOne(boolean set, final CComponent<?, InternationalAddress, ?> comp) {
            if (set) {
                assert (getView().getPresenter() != null);
                ((PaymentMethodWizardView.Presenter) getView().getPresenter()).getCurrentAddress(new DefaultAsyncCallback<InternationalAddress>() {
                    @Override
                    public void onSuccess(InternationalAddress result) {
                        comp.setValue(result, false);
                    }
                });
            } else {
                comp.setValue(EntityFactory.create(InternationalAddress.class), false);
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

        addStep(createPaymentMethodStep(), i18n.tr("Payment Method"));
        comfirmationStep = addStep(createConfirmationStep(), i18n.tr("Confirmation"));
    }

    public void setPresenter(PaymentMethodWizardView.Presenter presenter) {
        this.presenter = presenter;

        if (this.presenter != null) {
            this.termsOfUseAnchor.setHref(AppPlaceInfo.absoluteUrl(GWT.getModuleBaseURL(), true, this.presenter.getTermsOfUsePlace()));
        }
    }

    private IsWidget createPaymentMethodStep() {
        PortalFormPanel formPanel = new PortalFormPanel(this);

        formPanel.append(Location.Left, proto().paymentMethod(), paymentMethodEditor);

        return formPanel;
    }

    private IsWidget createConfirmationStep() {
        PortalFormPanel formPanel = new PortalFormPanel(this);
        formPanel.append(Location.Left, confirmationDetailsHolder);

        formPanel.br();
        formPanel.hr();

        formPanel.append(Location.Left, createLegalTermsPanel());

        return formPanel;
    }

    @Override
    protected void onStepSelected(WizardStep selectedStep) {
        super.onStepSelected(selectedStep);

        if (selectedStep.equals(comfirmationStep)) {
            confirmationDetailsHolder.clear();
            confirmationDetailsHolder.setWidget(createConfirmationDetailsPanel());
        }
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
