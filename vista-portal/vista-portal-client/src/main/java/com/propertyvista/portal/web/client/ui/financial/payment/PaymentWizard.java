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
package com.propertyvista.portal.web.client.ui.financial.payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.CSimpleEntityComboBox;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.gwt.commons.BrowserType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.RadioGroup;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.resources.VistaResources;
import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodForm;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.person.Person;
import com.propertyvista.dto.PaymentDataDTO.PaymentSelect;
import com.propertyvista.portal.domain.dto.financial.PaymentDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.CPortalEntityWizard;
import com.propertyvista.portal.web.client.ui.IWizardView;
import com.propertyvista.portal.web.client.ui.LegalTermsDialog;
import com.propertyvista.portal.web.client.ui.LegalTermsDialog.TermsType;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class PaymentWizard extends CPortalEntityWizard<PaymentDTO> {

    private static final I18n i18n = I18n.get(PaymentWizard.class);

    private final WizardStep paymentMethodSelectionStep, comfirmationStep;

    private final CComboBox<LeasePaymentMethod> profiledPaymentMethodsCombo = new CSimpleEntityComboBox<LeasePaymentMethod>();

    private final SimplePanel confirmationDetailsHolder = new SimplePanel();

    private final PaymentMethodForm<LeasePaymentMethod> paymentMethodEditor = new PaymentMethodForm<LeasePaymentMethod>(LeasePaymentMethod.class) {

        @Override
        protected Set<CreditCardType> getAllowedCardTypes() {
            return PaymentWizard.this.getValue().allowedCardTypes();
        }

        @Override
        public void onBillingAddressSameAsCurrentOne(boolean set, final CComponent<AddressSimple> comp) {
            if (set) {
                assert (getView().getPresenter() != null);
                ((PaymentWizardView.Persenter) getView().getPresenter()).getCurrentAddress(new DefaultAsyncCallback<AddressSimple>() {
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

    public PaymentWizard(IWizardView<PaymentDTO> view) {
        super(PaymentDTO.class, view, i18n.tr("Payment Setup"), i18n.tr("Submit"), ThemeColor.contrast4);

        addStep(createDetailsStep());
        paymentMethodSelectionStep = addStep(createSelectPaymentMethodStep());
        comfirmationStep = addStep(createConfirmationStep());
    }

    private BasicFlexFormPanel createDetailsStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        int row = -1;

        panel.setH1(++row, 0, 1, PortalImages.INSTANCE.billingIcon(), i18n.tr("Payment Details"));

        panel.setWidget(++row, 0,
                new FormDecoratorBuilder(inject(proto().leaseTermParticipant().leaseParticipant().customer().person(), new CEntityLabel<Person>()), 200)
                        .customLabel(i18n.tr("Tenant")).build());

        panel.setBR(++row, 0, 1);
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().amount()), 100).build());

        return panel;
    }

    private BasicFlexFormPanel createSelectPaymentMethodStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Payment Method Selection"));
        int row = -1;

        panel.setWidget(
                ++row,
                0,
                new FormDecoratorBuilder(inject(proto().selectPaymentMethod(), new CRadioGroupEnum<PaymentSelect>(PaymentSelect.class,
                        RadioGroup.Layout.HORISONTAL)), 200).build());

        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().profiledPaymentMethod(), profiledPaymentMethodsCombo), 200).build());

        panel.setWidget(++row, 0, inject(proto().paymentMethod(), paymentMethodEditor));

        panel.setHR(++row, 0, 1);

        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().addThisPaymentMethodToProfile()), 50).labelWidth(20).build());

        // tweaks:

        get(proto().selectPaymentMethod()).addValueChangeHandler(new ValueChangeHandler<PaymentSelect>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentSelect> event) {
                paymentMethodEditor.reset();
                paymentMethodEditor.setElectronicPaymentsEnabled(getValue().electronicPaymentsAllowed().getValue(Boolean.FALSE));

                if (event.getValue() != null) {
                    switch (event.getValue()) {
                    case New:
                        paymentMethodEditor.setViewable(false);

                        if (getValue().allowedPaymentTypes().isEmpty()) {
                            paymentMethodEditor.initNew(null);
                            MessageDialog.warn(i18n.tr("Warning"), i18n.tr("There are no payment methods allowed!"));
                        } else {
                            // set preferred value:
                            if (getValue().allowedPaymentTypes().contains(PaymentType.Echeck)) {
                                paymentMethodEditor.initNew(PaymentType.Echeck);
                            } else {
                                paymentMethodEditor.initNew(null);
                            }
                        }

                        paymentMethodEditor.getValue().isProfiledMethod().setValue(Boolean.FALSE);

                        setProfiledPaymentMethodsVisible(false);

                        break;

                    case Profiled:
                        paymentMethodEditor.setViewable(true);

                        profiledPaymentMethodsCombo.reset();
                        setProfiledPaymentMethodsVisible(true);
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
                    paymentMethodEditor.setValue(event.getValue());
                }
            }
        });

        paymentMethodEditor.addTypeSelectionValueChangeHandler(new ValueChangeHandler<PaymentType>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentType> event) {
                setupAddThisPaymentMethodToProfile(event.getValue());
            }
        });

        return panel;
    }

    private BasicFlexFormPanel createConfirmationStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Confirmation"));
        int row = -1;

        panel.setWidget(++row, 0, confirmationDetailsHolder);
        panel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        panel.setHR(++row, 0, 1);

        panel.setWidget(++row, 0, BasicFlexFormPanel());
        panel.getFlexCellFormatter().setAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);

        return panel;
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().amount()).addValueValidator(new EditableValueValidator<BigDecimal>() {
            @Override
            public ValidationError isValid(CComponent<BigDecimal> component, BigDecimal value) {
                if (value != null) {
                    return (value.compareTo(BigDecimal.ZERO) > 0 ? null
                            : new ValidationError(component, i18n.tr("Payment amount should be greater then zero!")));
                }
                return null;
            }
        });
    }

    @Override
    protected void onStepChange(SelectionEvent<WizardStep> event) {
        super.onStepChange(event);

//        getDecorator().getBtnNext().setEnabled(true);

        if (event.getSelectedItem().equals(comfirmationStep)) {
            confirmationDetailsHolder.clear();
            confirmationDetailsHolder.setWidget(createConfirmationDetailsPanel());

            if (get(proto().paymentMethod()).getValue().type().getValue() == PaymentType.DirectBanking) {
//                getDecorator().getBtnNext().setEnabled(false);
            }
        }
    }

    @Override
    protected void onValueSet(final boolean populate) {
        super.onValueSet(populate);

        paymentMethodEditor.setElectronicPaymentsEnabled(getValue().electronicPaymentsAllowed().getValue(Boolean.FALSE));

        loadProfiledPaymentMethods(new DefaultAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                boolean hasProfiledMethods = !profiledPaymentMethodsCombo.getOptions().isEmpty();

                get(proto().selectPaymentMethod()).reset();
                get(proto().selectPaymentMethod()).setEnabled(hasProfiledMethods);
                get(proto().selectPaymentMethod()).setValue(hasProfiledMethods ? PaymentSelect.Profiled : PaymentSelect.New, true, populate);

                paymentMethodSelectionStep.setStepVisible(hasProfiledMethods);
            }
        });
    }

    private void loadProfiledPaymentMethods(final AsyncCallback<Void> callback) {
        profiledPaymentMethodsCombo.setOptions(null);
        ((PaymentWizardView.Persenter) getView().getPresenter()).getProfiledPaymentMethods(new DefaultAsyncCallback<List<LeasePaymentMethod>>() {
            @Override
            public void onSuccess(List<LeasePaymentMethod> result) {
                profiledPaymentMethodsCombo.setOptions(result);
                if (callback != null) {
                    callback.onSuccess(null);
                }
            }
        });
    }

    private void setProfiledPaymentMethodsVisible(boolean visible) {
        profiledPaymentMethodsCombo.setVisible(visible);

        get(proto().addThisPaymentMethodToProfile()).setVisible(!visible && !getValue().paymentMethod().type().isNull());
        if (get(proto().addThisPaymentMethodToProfile()).isVisible()) {
            setupAddThisPaymentMethodToProfile(getValue().paymentMethod().type().getValue());
        }
    }

    private void setupAddThisPaymentMethodToProfile(PaymentType paymentType) {
        if (paymentType != null) {
            switch (paymentType) {
            case CreditCard:
                get(proto().addThisPaymentMethodToProfile()).setValue(true);
                get(proto().addThisPaymentMethodToProfile()).setEnabled(true);
                get(proto().addThisPaymentMethodToProfile()).setVisible(true);
                break;

            case Echeck:
                get(proto().addThisPaymentMethodToProfile()).setValue(true);
                get(proto().addThisPaymentMethodToProfile()).setEnabled(false);
                get(proto().addThisPaymentMethodToProfile()).setVisible(true);
                break;

            default:
                get(proto().addThisPaymentMethodToProfile()).setValue(false);
                get(proto().addThisPaymentMethodToProfile()).setEnabled(false);
                get(proto().addThisPaymentMethodToProfile()).setVisible(false);
                break;
            }
        }
    }

    private Widget createConfirmationDetailsPanel() {
        VerticalPanel panel = new VerticalPanel();

        panel.add(new HTML(getValue().leaseTermParticipant().leaseParticipant().customer().person().getStringView()));
        panel.add(new HTML(getValue().address().getStringView()));

        panel.add(new HTML("<br/>"));

        panel.add(createDecorator(i18n.tr("Payment Method:"), get(proto().paymentMethod()).getValue().getStringView()));
        panel.add(createDecorator(i18n.tr("Amount to pay:"), ((CTextFieldBase<?, ?>) get(proto().amount())).getFormattedValue()));

        if (get(proto().paymentMethod()).getValue().type().getValue() == PaymentType.DirectBanking) {
            panel.add(createDirectBankingPanel());
        }

        return panel;
    }

    private Widget createDirectBankingPanel() {
        VerticalPanel panel = new VerticalPanel();

        panel.add(createDecorator(i18n.tr("Account #:"), getValue().billingAccount().accountNumber().getStringView()));
        panel.add(createDecorator(i18n.tr("Payee:"), "Rent Payments - Payment Pad"));

        panel.add(new HTML("<br/>"));

        panel.add(new HTML(VistaResources.INSTANCE.directBankingDescription().getText()));

        panel.add(new HTML("<br/>"));

        FlexTable links = new FlexTable();
        links.setCellSpacing(10);
//        links.setBorderWidth(1);

        links.setWidget(0, 0, createLink(VistaImages.INSTANCE.linkTD(), "http://www.td.com/about-tdbfg/our-business"));
        links.setWidget(0, 1, createLink(VistaImages.INSTANCE.linkBMO(), "http://www.bmo.com/home/personal"));

        links.setWidget(1, 0, createLink(VistaImages.INSTANCE.linkCIBC(), "https://www.cibc.com/ca/personal.html"));
        links.setWidget(1, 1, createLink(VistaImages.INSTANCE.linkLaurentian(), "https://www.laurentianbank.ca/en/personal_banking_services/index.html"));

//        links.setWidget(2, 0, createLink(VistaImages.INSTANCE.linkManulife(), "http://www.manulifebank.ca/wps/portal/bankca/Bank.caHome/Personal/"));

        links.setWidget(2, 0, createLink(VistaImages.INSTANCE.linkNBC(), "http://www.nbc.ca/bnc/cda/index/0,4229,divId-2_langId-1_navCode-1000,00.html"));
        links.setWidget(2, 1, createLink(VistaImages.INSTANCE.linkPCF(), "http://www.pcfinancial.ca/"));

        links.setWidget(3, 0, createLink(VistaImages.INSTANCE.linkRBC(), "http://www.rbcroyalbank.com/personal.html"));
        links.setWidget(3, 1, createLink(VistaImages.INSTANCE.linkScotia(), "http://www.scotiabank.com/ca/en/0,1091,2,00.html"));

        panel.add(links);

        panel.add(new HTML("<br/>"));

        panel.add(new Anchor(i18n.tr("How to pay..."), new Command() {
            @Override
            public void execute() {
                new LegalTermsDialog(TermsType.DirectBankingInstruction).show();
            }
        }));

        return panel;
    }

    private Widget createDecorator(String label, String value) {
        HorizontalPanel payee = new HorizontalPanel();
        Widget w;

        payee.add(w = new HTML(label));
        w.setWidth("12em");
        payee.add(w = new HTML(value));
        w.getElement().getStyle().setFontWeight(FontWeight.BOLD);

        return payee;
    }

    private Image createLink(ImageResource image, final String url) {
        Image link = new Image(image);
        link.getElement().getStyle().setCursor(Cursor.POINTER);
        link.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                openLink(url);
            }
        });
        return link;
    }

    private void openLink(String url) {
        Window.open(url, "_blank", BrowserType.isIE() ? "status=1,toolbar=1,location=1,resizable=1,scrollbars=1" : null);
    }

    private Widget BasicFlexFormPanel() {
        FlowPanel panel = new FlowPanel();
        Widget w;

        panel.add(new HTML(i18n.tr("Be informed that you are acknowledging our")));
        panel.add(w = new Anchor(i18n.tr("Terms Of Use"), new Command() {
            @Override
            public void execute() {
                new LegalTermsDialog(TermsType.TermsOfUse).show();
            }
        }));

        panel.add(w = new HTML(",&nbsp"));
        w.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        panel.add(w = new Anchor(i18n.tr("Privacy Policy"), new Command() {
            @Override
            public void execute() {
                new LegalTermsDialog(TermsType.PrivacyPolicy).show();
            }
        }));

        panel.add(w = new HTML("&nbsp" + i18n.tr("and") + "&nbsp"));
        w.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        panel.add(w = new Anchor(i18n.tr("Billing And Refund Policy"), new Command() {
            @Override
            public void execute() {
                new LegalTermsDialog(TermsType.BillingAndRefundPolicy).show();
            }
        }));

        return panel;
    }
}
