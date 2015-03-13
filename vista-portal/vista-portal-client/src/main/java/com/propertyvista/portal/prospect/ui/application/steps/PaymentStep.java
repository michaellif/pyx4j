/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 11, 2013
 * @author VladL
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.CSimpleEntityComboBox;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.RadioGroup;

import com.propertyvista.common.client.theme.VistaTheme.StyleName;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.dto.PaymentDataDTO.PaymentSelect;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.shared.ui.util.editors.PortalPaymentMethodEditor;
import com.propertyvista.portal.shared.ui.util.folders.DepositFolder;

public class PaymentStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(PaymentStep.class);

    private final CComboBox<LeasePaymentMethod> profiledPaymentMethodsCombo = new CSimpleEntityComboBox<LeasePaymentMethod>();

    private final PortalPaymentMethodEditor<LeasePaymentMethod> paymentMethodEditor = new PortalPaymentMethodEditor<LeasePaymentMethod>(
            LeasePaymentMethod.class) {

        @Override
        public Set<PaymentType> getDefaultPaymentTypes() {
            if (PaymentStep.this.getValue() != null) {
                return PaymentStep.this.getValue().payment().allowedPaymentsSetup().allowedPaymentTypes();
            }
            return Collections.emptySet();
        }

        @Override
        protected Set<CreditCardType> getAllowedCardTypes() {
            if (PaymentStep.this.getValue() != null) {
                return PaymentStep.this.getValue().payment().allowedPaymentsSetup().allowedCardTypes();
            }
            return Collections.emptySet();
        }

        @Override
        protected Set<CreditCardType> getConvienceFeeApplicableCardTypes() {
            if (PaymentStep.this.getValue() != null) {
                return PaymentStep.this.getValue().payment().allowedPaymentsSetup().convenienceFeeApplicableCardTypes();
            }
            return Collections.emptySet();
        };

        @Override
        protected String getNameOn() {
            return ClientContext.getUserVisit().getName();
        }
    };

    private Widget depositHeader, feesHeader;

    private FormPanel paymentMethodPanel;

    private final SimplePanel paymentMethodHolder = new SimplePanel();

    private final Label noPaymentRequiredLabel = new Label(i18n.tr("No Payment Required"));

    private final Label noPaymentAcceptLabel = new Label(i18n.tr("Can not accept payment at this time - you will be contacted by the office"));

    public PaymentStep() {
        super(OnlineApplicationWizardStepMeta.Payment);

        paymentMethodEditor.setBillingAddressAsCurrentDisplay(false);

        noPaymentRequiredLabel.setStyleName(StyleName.InfoMessage.name());
        noPaymentAcceptLabel.setStyleName(StyleName.WarningMessage.name());
    }

    @Override
    public IsWidget createStepContent() {
        FormPanel formPanel = new FormPanel(getWizard());

        if (SecurityController.check(PortalProspectBehavior.Applicant)) {
            depositHeader = formPanel.h3(i18n.tr("Deposits"));
            formPanel.append(Location.Left, proto().leaseChargesData().deposits(), new DepositFolder());
            formPanel.append(Location.Left, proto().leaseChargesData().totalDeposits()).decorate();
        }

        feesHeader = formPanel.h3(i18n.tr("Fees"));
        formPanel.append(Location.Left, proto().payment().applicationFee(), new CMoneyLabel()).decorate();

        formPanel.append(Location.Left, paymentMethodHolder);
        paymentMethodPanel = createPaymentMethodPanel();

        return formPanel;
    }

    public FormPanel createPaymentMethodPanel() {
        FormPanel formPanel = new FormPanel(getWizard());

        formPanel.h3(i18n.tr("Payment Method"));
        formPanel.append(Location.Left, proto().payment().selectPaymentMethod(),
                new CRadioGroupEnum<PaymentSelect>(PaymentSelect.class, RadioGroup.Layout.HORIZONTAL)).decorate();
        formPanel.append(Location.Left, proto().payment().profiledPaymentMethod(), profiledPaymentMethodsCombo).decorate();
        formPanel.append(Location.Left, proto().payment().paymentMethod(), paymentMethodEditor);
        formPanel.hr();
        formPanel.append(Location.Left, proto().payment().storeInProfile()).decorate();

        // tweaks:

        get(proto().payment().selectPaymentMethod()).addValueChangeHandler(new ValueChangeHandler<PaymentSelect>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentSelect> event) {
                paymentMethodEditor.clear();
                paymentMethodEditor.setDefaultPaymentTypes();

                if (event.getValue() != null) {
                    switch (event.getValue()) {
                    case New:
                        paymentMethodEditor.setEditable(true);

                        if (getValue().payment().allowedPaymentsSetup().allowedPaymentTypes().isEmpty()) {
                            paymentMethodEditor.initNew(null);
                        } else {
                            // set preferred value:
                            if (getValue().payment().allowedPaymentsSetup().allowedPaymentTypes().contains(PaymentType.Echeck)) {
                                paymentMethodEditor.initNew(PaymentType.Echeck);
                            } else {
                                paymentMethodEditor.initNew(null);
                            }
                        }

                        paymentMethodEditor.getValue().isProfiledMethod().setValue(Boolean.FALSE);

                        setProfiledPaymentMethodsVisible(false);

                        break;

                    case Profiled:
                        paymentMethodEditor.setEditable(false);

                        profiledPaymentMethodsCombo.clear();
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
                    paymentMethodEditor.populate(event.getValue().<LeasePaymentMethod> duplicate());
                }
            }
        });

        paymentMethodEditor.addTypeSelectionValueChangeHandler(new ValueChangeHandler<PaymentType>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentType> event) {
                setupAddThisPaymentMethodToProfile(event.getValue());
            }
        });

        return formPanel;
    }

    @Override
    public void onValueSet(final boolean populate) {
        super.onValueSet(populate);

        boolean isDepositsPresent = !getValue().leaseChargesData().deposits().isEmpty();

        if (SecurityController.check(PortalProspectBehavior.Applicant)) {
            depositHeader.setVisible(isDepositsPresent);
            get(proto().leaseChargesData().deposits()).setVisible(isDepositsPresent);
            get(proto().leaseChargesData().totalDeposits()).setVisible(isDepositsPresent);
        }

        boolean isFeesPresent = !getValue().payment().applicationFee().isNull();
        feesHeader.setVisible(isFeesPresent);
        get(proto().payment().applicationFee()).setVisible(isFeesPresent);

        boolean isPaymentRequired = (isFeesPresent || isDepositsPresent);

        get(proto().payment().storeInProfile()).setEnabled(isPaymentRequired);
        get(proto().payment().selectPaymentMethod()).setEnabled(isPaymentRequired);
        profiledPaymentMethodsCombo.setEnabled(isPaymentRequired);
        paymentMethodEditor.setEnabled(isPaymentRequired);

        if (isPaymentRequired) {
            if (getValue().payment().allowedPaymentsSetup().allowedPaymentTypes().isEmpty()) {
                get(proto().payment().storeInProfile()).setEnabled(false);
                get(proto().payment().selectPaymentMethod()).setEnabled(false);
                profiledPaymentMethodsCombo.setEnabled(false);
                paymentMethodEditor.setEnabled(false);

                paymentMethodPanel.setVisible(false);
                paymentMethodHolder.setWidget(noPaymentAcceptLabel);
            } else {
                paymentMethodPanel.setVisible(true);
                paymentMethodHolder.setWidget(paymentMethodPanel);
                loadProfiledPaymentMethods(new DefaultAsyncCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        boolean hasProfiledMethods = !profiledPaymentMethodsCombo.getOptions().isEmpty();

                        get(proto().payment().selectPaymentMethod()).clear();
                        get(proto().payment().selectPaymentMethod()).setEnabled(hasProfiledMethods);
                        get(proto().payment().selectPaymentMethod()).setValue(hasProfiledMethods ? PaymentSelect.Profiled : PaymentSelect.New, true, populate);
                    }
                });
            }
        } else {
            paymentMethodPanel.setVisible(false);
            paymentMethodHolder.setWidget(noPaymentRequiredLabel);
            paymentMethodEditor.clear();
        }
    }

    private void loadProfiledPaymentMethods(final AsyncCallback<Void> callback) {
        profiledPaymentMethodsCombo.setOptions(null);
        getWizard().getPresenter().getProfiledPaymentMethods(new DefaultAsyncCallback<List<LeasePaymentMethod>>() {
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

        get(proto().payment().storeInProfile()).setVisible(!visible && !getValue().payment().paymentMethod().type().isNull());
        if (get(proto().payment().storeInProfile()).isVisible()) {
            setupAddThisPaymentMethodToProfile(getValue().payment().paymentMethod().type().getValue());
        }
    }

    private void setupAddThisPaymentMethodToProfile(PaymentType paymentType) {
        if (paymentType != null) {
            switch (paymentType) {
            case CreditCard:
                get(proto().payment().storeInProfile()).setValue(true);
                get(proto().payment().storeInProfile()).setEnabled(true);
                get(proto().payment().storeInProfile()).setVisible(true);
                break;

            case Echeck:
                get(proto().payment().storeInProfile()).setValue(true);
                get(proto().payment().storeInProfile()).setEnabled(false);
                get(proto().payment().storeInProfile()).setVisible(true);
                break;

            default:
                get(proto().payment().storeInProfile()).setValue(false);
                get(proto().payment().storeInProfile()).setEnabled(false);
                get(proto().payment().storeInProfile()).setVisible(false);
                break;
            }
        }
    }

}
