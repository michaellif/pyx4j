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
 */
package com.propertyvista.portal.resident.ui.financial.autopay;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.CSimpleEntityComboBox;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.ui.wizard.WizardDecorator;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.RadioGroup;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.PaymentDataDTO;
import com.propertyvista.dto.PaymentDataDTO.PaymentSelect;
import com.propertyvista.dto.PreauthorizedPaymentCoveredItemDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap.ResidentPortalTerms;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.AutoPayDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;
import com.propertyvista.portal.shared.ui.TermsAnchor;
import com.propertyvista.portal.shared.ui.util.CCurrencyMoneyLabel;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;
import com.propertyvista.portal.shared.ui.util.editors.PortalPaymentMethodEditor;

public class AutoPayWizard extends CPortalEntityWizard<AutoPayDTO> {

    static final I18n i18n = I18n.get(AutoPayWizard.class);

    private final WizardStep detailsStep, comfirmationStep;

    private final CComboBox<LeasePaymentMethod> profiledPaymentMethodsCombo = new CSimpleEntityComboBox<LeasePaymentMethod>();

    private final SimplePanel confirmationDetailsHolder = new SimplePanel();

    private final SimplePanel confirmationTotalHolder = new SimplePanel();

    private final SimplePanel detailsTotalHolder = new SimplePanel();

    private final PortalPaymentMethodEditor<LeasePaymentMethod> paymentMethodEditor = new PortalPaymentMethodEditor<LeasePaymentMethod>(
            LeasePaymentMethod.class) {

        @Override
        public Set<PaymentType> getDefaultPaymentTypes() {
            if (AutoPayWizard.this.getValue() != null) {
                return AutoPayWizard.this.getValue().allowedPaymentsSetup().allowedPaymentTypes();
            }
            return Collections.emptySet();
        }

        @Override
        protected Set<CreditCardType> getAllowedCardTypes() {
            if (AutoPayWizard.this.getValue() != null) {
                return AutoPayWizard.this.getValue().allowedPaymentsSetup().allowedCardTypes();
            }
            return Collections.emptySet();
        }

        @Override
        protected Set<CreditCardType> getConvienceFeeApplicableCardTypes() {
            if (AutoPayWizard.this.getValue() != null) {
                return AutoPayWizard.this.getValue().allowedPaymentsSetup().convenienceFeeApplicableCardTypes();
            }
            return Collections.emptySet();
        };

        @Override
        public void onBillingAddressSameAsCurrentOne(boolean set, final CComponent<?, InternationalAddress, ?, ?> comp) {
            if (set) {
                assert (getView().getPresenter() != null);
                ((AutoPayWizardView.Presenter) getView().getPresenter()).getCurrentAddress(new DefaultAsyncCallback<InternationalAddress>() {
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

    private final CComponent<?, ?, ?, ?> totalWidget;

    public AutoPayWizard(AutoPayWizardView view) {
        super(AutoPayDTO.class, view, i18n.tr("Automatic Payment Setup"), i18n.tr("Submit"), ThemeColor.contrast4);

        detailsStep = addStep(createDetailsStep(), i18n.tr("Details"));
        addStep(createSelectPaymentMethodStep(), i18n.tr("Payment Method Selection"));
        comfirmationStep = addStep(createConfirmationStep(), i18n.tr("Confirmation"));

        totalWidget = inject(proto().total(), new CCurrencyMoneyLabel(i18n.tr("CAD $")),
                new FieldDecoratorBuilder().useLabelSemicolon(true).labelAlignment(Alignment.right).build());
    }

    private IsWidget createDetailsStep() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().tenant(), new CEntityLabel<Tenant>()).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().address(), new CEntityLabel<InternationalAddress>()).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().coveredItemsDTO(), new PapCoveredItemDtoFolder() {
            @Override
            public void onAmontValueChange() {
                BigDecimal total = BigDecimal.ZERO;
                for (PreauthorizedPaymentCoveredItemDTO item : getValue()) {
                    if (!item.amount().isNull()) {
                        total = (total.add(item.amount().getValue()));
                    }
                }
                AutoPayWizard.this.get(AutoPayWizard.this.proto().total()).setValue(total);
            }
        });
        formPanel.append(Location.Left, detailsTotalHolder);

        return formPanel;
    }

    private IsWidget createSelectPaymentMethodStep() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().selectPaymentMethod(), new CRadioGroupEnum<PaymentSelect>(PaymentSelect.class, RadioGroup.Layout.HORIZONTAL))
                .decorate().componentWidth(200);

        formPanel.append(Location.Left, proto().profiledPaymentMethod(), profiledPaymentMethodsCombo).decorate();

        formPanel.append(Location.Left, proto().paymentMethod(), paymentMethodEditor);

        // tweaks:

        get(proto().selectPaymentMethod()).addValueChangeHandler(new ValueChangeHandler<PaymentSelect>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentSelect> event) {
                paymentMethodEditor.clear();
                paymentMethodEditor.setDefaultPaymentTypes();

                if (event.getValue() != null) {
                    switch (event.getValue()) {
                    case New:
                        paymentMethodEditor.setEditable(true);

                        if (getValue().allowedPaymentsSetup().allowedPaymentTypes().isEmpty()) {
                            paymentMethodEditor.initNew(null);
                            MessageDialog.warn(i18n.tr("Warning"), i18n.tr("There are no payment methods allowed!"));
                        } else {
                            // set preferred value:
                            if (getValue().allowedPaymentsSetup().allowedPaymentTypes().contains(PaymentType.Echeck)) {
                                paymentMethodEditor.initNew(PaymentType.Echeck);
                            } else {
                                paymentMethodEditor.initNew(null);
                            }
                        }

                        paymentMethodEditor.getValue().isProfiledMethod().setValue(Boolean.FALSE);

                        profiledPaymentMethodsCombo.setVisible(false);

                        break;

                    case Profiled:
                        paymentMethodEditor.setEditable(false);

                        profiledPaymentMethodsCombo.clear();
                        profiledPaymentMethodsCombo.setVisible(true);
                        if (!profiledPaymentMethodsCombo.getOptions().isEmpty()) {
                            profiledPaymentMethodsCombo.setValue(profiledPaymentMethodsCombo.getOptions().get(0));
                        }

                        break;
                    }
                }
            }
        });

        profiledPaymentMethodsCombo.addValueChangeHandler(new ValueChangeHandler<LeasePaymentMethod>() {
            @Override
            public void onValueChange(ValueChangeEvent<LeasePaymentMethod> event) {
                if (event.getValue() != null) {
                    paymentMethodEditor.populate(event.getValue().<LeasePaymentMethod> duplicate());
                }
            }
        });

        return formPanel;
    }

    private IsWidget createConfirmationStep() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, confirmationDetailsHolder);
        formPanel.br();
        formPanel.append(Location.Left, proto().coveredItems(), new PapCoveredItemFolder());
        formPanel.append(Location.Left, confirmationTotalHolder);
        formPanel.br();
        formPanel.append(Location.Left, proto().nextPaymentDate(), new CDateLabel()).decorate().componentWidth(100);
        formPanel.hr();
        formPanel.append(Location.Left, createLegalTermsPanel());

        get(proto().coveredItems()).setViewable(true);
        get(proto().coveredItems()).inheritViewable(false);

        return formPanel;
    }

    @Override
    public void onReset() {
        super.onReset();

        get(proto().coveredItems()).setVisible(true);
        get(proto().total()).setVisible(true);
        get(proto().nextPaymentDate()).setVisible(true);
        if (getDecorator() instanceof WizardDecorator) {
            ((WizardDecorator<?>) getDecorator()).getBtnNext().setEnabled(true);
        }

        switchTotal(detailsTotalHolder);
    }

    @Override
    protected void onStepSelected(WizardStep selectedStep) {
        super.onStepSelected(selectedStep);
        if (selectedStep.equals(detailsStep)) {
            switchTotal(detailsTotalHolder);
        } else if (selectedStep.equals(comfirmationStep)) {
            switchTotal(confirmationTotalHolder);

            confirmationDetailsHolder.clear();
            ((AutoPayWizardView.Presenter) getView().getPresenter()).preview(new DefaultAsyncCallback<AutopayAgreement>() {
                @Override
                public void onSuccess(AutopayAgreement result) {
                    get(proto().coveredItems()).populate(result.coveredItems());
                    confirmationDetailsHolder.setWidget(createConfirmationDetailsPanel());
                }
            }, getValue());
        }
    }

    private void switchTotal(SimplePanel holder) {
        holder.setWidget(totalWidget);
        get(proto().total()).setViewable(true);
    }

    @Override
    protected void onValueSet(final boolean populate) {
        super.onValueSet(populate);

        loadProfiledPaymentMethods(new DefaultAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                boolean hasProfiledMethods = !profiledPaymentMethodsCombo.getOptions().isEmpty();

                get(proto().selectPaymentMethod()).clear();
                get(proto().selectPaymentMethod()).setEnabled(hasProfiledMethods);
                get(proto().selectPaymentMethod()).setVisible(hasProfiledMethods);
                get(proto().selectPaymentMethod()).setValue(hasProfiledMethods ? PaymentDataDTO.PaymentSelect.Profiled : PaymentDataDTO.PaymentSelect.New,
                        true, populate);
            }
        });
    }

    @Override
    public void addValidations() {
        super.addValidations();

        profiledPaymentMethodsCombo.addComponentValidator(new AbstractComponentValidator<LeasePaymentMethod>() {
            @Override
            public BasicValidationError isValid() {
                if (getCComponent().getValue() != null) {
                    return (paymentMethodEditor.getDefaultPaymentTypes().contains(getCComponent().getValue().type().getValue()) ? null
                            : new BasicValidationError(getCComponent(), i18n.tr("Not allowed payment type!")));
                }
                return null;
            }
        });
    }

    private void loadProfiledPaymentMethods(final AsyncCallback<Void> callback) {
        profiledPaymentMethodsCombo.setOptions(null);
        ((AutoPayWizardView.Presenter) getView().getPresenter()).getProfiledPaymentMethods(new DefaultAsyncCallback<List<LeasePaymentMethod>>() {
            @Override
            public void onSuccess(List<LeasePaymentMethod> result) {
                profiledPaymentMethodsCombo.setOptions(result);
                if (callback != null) {
                    callback.onSuccess(null);
                }
            }
        });
    }

    private Widget createConfirmationDetailsPanel() {
        VerticalPanel panel = new VerticalPanel();
        Widget w;

        panel.add(new HTML(getValue().tenant().customer().person().getStringView()));
        panel.add(new HTML(getValue().address().getStringView()));

        panel.add(new HTML("<br/>"));

        HorizontalPanel pm = new HorizontalPanel();
        pm.add(w = new HTML(i18n.tr("Payment Method:")));
        w.setWidth("10em");
        pm.add(w = new HTML(get(proto().paymentMethod()).getValue().getStringView()));
        w.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        panel.add(pm);

        if (getValue().coveredItems().isEmpty()) {
            panel.add(new HTML("<br/>"));
            panel.add(new HTML("<br/>"));

            panel.add(w = new HTML(i18n.tr("There are no payments set!")));
            w.setStyleName(VistaTheme.StyleName.WarningMessage.name());
            w.getElement().getStyle().setTextAlign(TextAlign.CENTER);

            get(proto().coveredItems()).setVisible(false);
            get(proto().total()).setVisible(false);
            get(proto().nextPaymentDate()).setVisible(false);
            if (getDecorator() instanceof WizardDecorator) {
                ((WizardDecorator<?>) getDecorator()).getBtnNext().setEnabled(false);
            }
        }

        return panel;
    }

    private Widget createLegalTermsPanel() {
        SafeHtmlBuilder legalTermsBuilder = new SafeHtmlBuilder();
        final String termsOfUseAnchorId = HTMLPanel.createUniqueId();
        final String billingPolicyAnchorId = HTMLPanel.createUniqueId();
        final String preAuthorizedAgreementId = HTMLPanel.createUniqueId();

        legalTermsBuilder.appendHtmlConstant(i18n.tr("Be informed that you are acknowledging our {0}, {1} and {2}.", "<span id=\"" + termsOfUseAnchorId
                + "\"></span>", "<span id=\"" + billingPolicyAnchorId + "\"></span>", "<span id=\"" + preAuthorizedAgreementId + "\"></span>"));

        final HTMLPanel legalTermsLinkPanel = new HTMLPanel(legalTermsBuilder.toSafeHtml());

        Anchor termsOfUseAnchor = new TermsAnchor(i18n.tr("Terms Of Use"), PortalSiteMap.PortalTerms.VistaTermsAndConditions.class);
        legalTermsLinkPanel.addAndReplaceElement(termsOfUseAnchor, termsOfUseAnchorId);

        Anchor billingPolicyAnchor = new TermsAnchor(i18n.tr("Billing And Refund Policy"), PortalSiteMap.PortalTerms.BillingTerms.class);
        legalTermsLinkPanel.addAndReplaceElement(billingPolicyAnchor, billingPolicyAnchorId);

        final TermsAnchor preAuthorizedAgreementAnchor = new TermsAnchor(i18n.tr("Pre-Authorized Agreement"), null);
        preAuthorizedAgreementAnchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                switch (get(proto().paymentMethod()).getValue().type().getValue()) {
                case Echeck:
                    preAuthorizedAgreementAnchor.openTerm(ResidentPortalTerms.PreauthorizedPaymentECheckTerms.class, event);
                    break;
                case CreditCard:
                    preAuthorizedAgreementAnchor.openTerm(ResidentPortalTerms.PreauthorizedPaymentCardTerms.class, event);
                    break;
                default:
                    assert false : "Illegal payment method type!";
                    break;
                }
                DOM.eventPreventDefault((com.google.gwt.user.client.Event) event.getNativeEvent());
            }
        });
        legalTermsLinkPanel.addAndReplaceElement(preAuthorizedAgreementAnchor, preAuthorizedAgreementId);

        return legalTermsLinkPanel;
    }
}
