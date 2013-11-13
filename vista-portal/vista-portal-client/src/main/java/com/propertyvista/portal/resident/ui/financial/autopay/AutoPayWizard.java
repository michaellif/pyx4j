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
package com.propertyvista.portal.resident.ui.financial.autopay;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.CSimpleEntityComboBox;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.rpc.AppPlaceInfo;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.RadioGroup;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.PaymentDataDTO;
import com.propertyvista.dto.PaymentDataDTO.PaymentSelect;
import com.propertyvista.dto.PreauthorizedPaymentCoveredItemDTO;
import com.propertyvista.portal.resident.ui.financial.paymentmethod.editor.PaymentMethodEditor;
import com.propertyvista.portal.rpc.portal.web.dto.financial.AutoPayDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class AutoPayWizard extends CPortalEntityWizard<AutoPayDTO> {

    static final I18n i18n = I18n.get(AutoPayWizard.class);

    private final WizardStep detailsStep, paymentMethodSelectionStep, comfirmationStep;

    private final CComboBox<LeasePaymentMethod> profiledPaymentMethodsCombo = new CSimpleEntityComboBox<LeasePaymentMethod>();

    private final SimplePanel confirmationDetailsHolder = new SimplePanel();

    private final SimplePanel confirmationTotalHolder = new SimplePanel();

    private final SimplePanel detailsTotalHolder = new SimplePanel();

    private final Anchor termsOfUseAnchor = new Anchor(i18n.tr("Terms Of Use"));

    private final Anchor preAuthorizedAgreementAnchor = new Anchor(i18n.tr("Pre-Authorized Agreement"));

    private final PaymentMethodEditor<LeasePaymentMethod> paymentMethodEditor = new PaymentMethodEditor<LeasePaymentMethod>(LeasePaymentMethod.class) {

        @Override
        protected Set<CreditCardType> getAllowedCardTypes() {
            return AutoPayWizard.this.getValue().allowedCardTypes();
        }

        @Override
        public void onBillingAddressSameAsCurrentOne(boolean set, final CComponent<AddressSimple> comp) {
            if (set) {
                assert (getView().getPresenter() != null);
                ((AutoPayWizardView.Presenter) getView().getPresenter()).getCurrentAddress(new DefaultAsyncCallback<AddressSimple>() {
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

    private AutoPayWizardView.Presenter presenter;

    public AutoPayWizard(AutoPayWizardView view) {
        super(AutoPayDTO.class, view, i18n.tr("Automatic Payment Setup"), i18n.tr("Submit"), ThemeColor.contrast4);

        detailsStep = addStep(createDetailsStep());
        paymentMethodSelectionStep = addStep(createSelectPaymentMethodStep());
        comfirmationStep = addStep(createConfirmationStep());
    }

    public void setPresenter(AutoPayWizardView.Presenter presenter) {
        this.presenter = presenter;

        if (this.presenter != null) {
            this.termsOfUseAnchor.setHref(AppPlaceInfo.absoluteUrl(GWT.getModuleBaseURL(), true, this.presenter.getTermsOfUsePlace()));
        }
    }

    private BasicFlexFormPanel createDetailsStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Details"));
        int row = -1;

        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().tenant(), new CEntityLabel<Tenant>()), 200).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().address(), new CEntityLabel<AddressSimple>()), 200).build());
        panel.setWidget(++row, 0, inject(proto().coveredItemsDTO(), new PapCoveredItemDtoFolder() {
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
        }));
        panel.setWidget(++row, 0, detailsTotalHolder);

        return panel;
    }

    private BasicFlexFormPanel createSelectPaymentMethodStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Payment Method Selection"));
        int row = -1;

        panel.setWidget(
                ++row,
                0,
                new FormWidgetDecoratorBuilder(inject(proto().selectPaymentMethod(), new CRadioGroupEnum<PaymentSelect>(PaymentSelect.class,
                        RadioGroup.Layout.HORISONTAL)), 200).build());

        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().profiledPaymentMethod(), profiledPaymentMethodsCombo), 200).build());

        panel.setWidget(++row, 0, inject(proto().paymentMethod(), paymentMethodEditor));

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

                        profiledPaymentMethodsCombo.setVisible(false);

                        break;

                    case Profiled:
                        paymentMethodEditor.setViewable(true);

                        profiledPaymentMethodsCombo.reset();
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
                    paymentMethodEditor.setValue(event.getValue());
                }
            }
        });

        return panel;
    }

    private BasicFlexFormPanel createConfirmationStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Confirmation"));
        int row = -1;

        panel.setWidget(++row, 0, confirmationDetailsHolder);
        panel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        panel.setBR(++row, 0, 1);

        panel.setWidget(++row, 0, inject(proto().coveredItems(), new PapCoveredItemFolder()));
        get(proto().coveredItems()).setViewable(true);
        get(proto().coveredItems()).inheritViewable(false);

        panel.setWidget(++row, 0, confirmationTotalHolder);

        panel.setBR(++row, 0, 1);

        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().nextPaymentDate(), new CDateLabel()), 100).labelWidth("250px").build());

        panel.setHR(++row, 0, 1);

        panel.setWidget(++row, 0, createLegalTermsPanel());
        panel.getFlexCellFormatter().setAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);

        return panel;
    }

    @Override
    protected void onStepChange(SelectionEvent<WizardStep> event) {
        super.onStepChange(event);
        if (event.getSelectedItem().equals(detailsStep)) {
            switchTotal(detailsTotalHolder);
        } else if (event.getSelectedItem().equals(comfirmationStep)) {

            confirmationDetailsHolder.clear();
            ((AutoPayWizardView.Presenter) getView().getPresenter()).preview(new DefaultAsyncCallback<AutopayAgreement>() {
                @Override
                public void onSuccess(AutopayAgreement result) {
                    get(proto().coveredItems()).populate(result.coveredItems());
                    confirmationDetailsHolder.setWidget(createConfirmationDetailsPanel());
                }
            }, getValue());

            switchTotal(confirmationTotalHolder);
        }
    }

    private void switchTotal(SimplePanel holder) {
        BigDecimal total = BigDecimal.ZERO;
        if (isBound(proto().total())) {
            total = get(proto().total()).getValue();
            unbind(proto().total());
        }
        holder.setWidget(new FormWidgetDecoratorBuilder(inject(proto().total()), 100).build());
        get(proto().total()).setValue(total);
        get(proto().total()).setViewable(true);
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
                get(proto().selectPaymentMethod()).setVisible(hasProfiledMethods);
                get(proto().selectPaymentMethod()).setValue(hasProfiledMethods ? PaymentDataDTO.PaymentSelect.Profiled : PaymentDataDTO.PaymentSelect.New,
                        true, populate);
            }
        });
    }

    @Override
    public void addValidations() {
        super.addValidations();

        profiledPaymentMethodsCombo.addValueValidator(new EditableValueValidator<LeasePaymentMethod>() {
            @Override
            public ValidationError isValid(CComponent<LeasePaymentMethod> component, LeasePaymentMethod value) {
                if (value != null) {
                    return (paymentMethodEditor.defaultPaymentTypes().contains(value.type().getValue()) ? null : new ValidationError(component, i18n
                            .tr("Not allowed payment type!")));
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
            w.setStyleName(VistaTheme.StyleName.warningMessage.name());
            w.getElement().getStyle().setTextAlign(TextAlign.CENTER);

            get(proto().total()).setVisible(false);
        }

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

        preAuthorizedAgreementAnchor.getElement().getStyle().setDisplay(Display.INLINE);
        preAuthorizedAgreementAnchor.getElement().getStyle().setPadding(0, Unit.PX);
        preAuthorizedAgreementAnchor.getElement().getStyle().setWhiteSpace(WhiteSpace.NORMAL);
        preAuthorizedAgreementAnchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                switch (get(proto().paymentMethod()).getValue().type().getValue()) {
                case Echeck:
                    preAuthorizedAgreementAnchor.setHref(AppPlaceInfo.absoluteUrl(GWT.getModuleBaseURL(), true, presenter.getPadPolicyPlace()));
                    presenter.showPadPolicy();
                    break;
                case CreditCard:
                    preAuthorizedAgreementAnchor.setHref(AppPlaceInfo.absoluteUrl(GWT.getModuleBaseURL(), true, presenter.getCcPolicyPlace()));
                    presenter.showCcPolicy();
                    break;
                default:
                    assert false : "Illegal payment method type!";
                    break;
                }
                DOM.eventPreventDefault((com.google.gwt.user.client.Event) event.getNativeEvent());
            }
        });

        Widget w;
        FlowPanel panel = new FlowPanel();

        panel.add(new HTML(i18n.tr("Be informed that you are acknowledging our")));
        panel.add(termsOfUseAnchor);

        panel.add(w = new HTML("&nbsp" + i18n.tr("and") + "&nbsp"));
        w.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        panel.add(preAuthorizedAgreementAnchor);

        return panel;
    }
}
