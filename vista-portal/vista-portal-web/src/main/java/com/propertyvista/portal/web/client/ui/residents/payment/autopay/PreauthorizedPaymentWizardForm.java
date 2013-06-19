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
package com.propertyvista.portal.web.client.ui.residents.payment.autopay;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.CSimpleEntityComboBox;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.ui.prime.wizard.IWizard;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.RadioGroup;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodForm;
import com.propertyvista.common.client.ui.components.folders.PapCoveredItemDtoFolder;
import com.propertyvista.common.client.ui.components.folders.PapCoveredItemFolder;
import com.propertyvista.common.client.ui.wizard.VistaWizardForm;
import com.propertyvista.common.client.ui.wizard.VistaWizardStep;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.PaymentDataDTO;
import com.propertyvista.dto.PaymentDataDTO.PaymentSelect;
import com.propertyvista.dto.PreauthorizedPaymentCoveredItemDTO;
import com.propertyvista.portal.rpc.portal.dto.PreauthorizedPaymentDTO;
import com.propertyvista.portal.web.client.ui.residents.LegalTermsDialog;
import com.propertyvista.portal.web.client.ui.residents.LegalTermsDialog.TermsType;
import com.propertyvista.portal.web.client.ui.residents.payment.PortalPaymentTypesUtil;

public class PreauthorizedPaymentWizardForm extends VistaWizardForm<PreauthorizedPaymentDTO> {

    static final I18n i18n = I18n.get(PreauthorizedPaymentWizardForm.class);

    private static String cutOffDateWarning = i18n.tr("All changes will take effect after this date!");

    private final VistaWizardStep detailsStep, paymentMethodStep, comfirmationStep;

    private final CComboBox<LeasePaymentMethod> profiledPaymentMethodsCombo = new CSimpleEntityComboBox<LeasePaymentMethod>();

    private final SimplePanel confirmationDetailsHolder = new SimplePanel();

    private final SimplePanel confirmationTotalHolder = new SimplePanel();

    private final SimplePanel detailsTotalHolder = new SimplePanel();

    private final PaymentMethodForm<LeasePaymentMethod> paymentMethodEditor = new PaymentMethodForm<LeasePaymentMethod>(LeasePaymentMethod.class) {
        @Override
        public Collection<PaymentType> defaultPaymentTypes() {
            return PortalPaymentTypesUtil.getAllowedPaymentTypes();
        }

        @Override
        public void onBillingAddressSameAsCurrentOne(boolean set, final CComponent<AddressStructured, ?> comp) {
            if (set) {
                assert (getView().getPresenter() != null);
                ((PreauthorizedPaymentWizardView.Persenter) getView().getPresenter()).getCurrentAddress(new DefaultAsyncCallback<AddressStructured>() {
                    @Override
                    public void onSuccess(AddressStructured result) {
                        comp.setValue(result, false);
                    }
                });
            } else {
                comp.setValue(EntityFactory.create(AddressStructured.class), false);
            }
        }
    };

    public PreauthorizedPaymentWizardForm(IWizard<PreauthorizedPaymentDTO> view) {
        super(PreauthorizedPaymentDTO.class, view);

        detailsStep = addStep(createDetailsStep());
        addStep(createSelectPaymentMethodStep());
        paymentMethodStep = addStep(createPaymentMethodStep());
        comfirmationStep = addStep(createConfirmationStep());
    }

    private FormFlexPanel createDetailsStep() {
        FormFlexPanel panel = new FormFlexPanel(i18n.tr("Details"));
        int row = -1;

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().tenant(), new CEntityLabel<Tenant>()), 30, 10).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().address(), new CEntityLabel<AddressSimple>()), 40, 10).build());
        panel.setWidget(++row, 0, inject(proto().coveredItemsDTO(), new PapCoveredItemDtoFolder() {
            @Override
            public void onAmontValueChange() {
                BigDecimal total = BigDecimal.ZERO;
                for (PreauthorizedPaymentCoveredItemDTO item : getValue()) {
                    if (!item.amount().isNull()) {
                        total = (total.add(item.amount().getValue()));
                    }
                }
                PreauthorizedPaymentWizardForm.this.get(PreauthorizedPaymentWizardForm.this.proto().total()).setValue(total);
            }
        }));
        panel.setWidget(++row, 0, detailsTotalHolder);

        return panel;
    }

    private FormFlexPanel createSelectPaymentMethodStep() {
        FormFlexPanel panel = new FormFlexPanel(i18n.tr("Payment Method Selection"));
        int row = -1;

        panel.setWidget(
                ++row,
                0,
                new DecoratorBuilder(inject(proto().selectPaymentMethod(),
                        new CRadioGroupEnum<PaymentSelect>(PaymentSelect.class, RadioGroup.Layout.HORISONTAL))).build());

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().profiledPaymentMethod(), profiledPaymentMethodsCombo), 25).build());

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

                        paymentMethodStep.setStepVisible(true);
                        break;

                    case Profiled:
                        paymentMethodEditor.setViewable(true);

                        profiledPaymentMethodsCombo.reset();
                        profiledPaymentMethodsCombo.setVisible(true);
                        if (!profiledPaymentMethodsCombo.getOptions().isEmpty()) {
                            profiledPaymentMethodsCombo.setValue(profiledPaymentMethodsCombo.getOptions().get(0));
                        }

                        paymentMethodStep.setStepVisible(false);
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

    private FormFlexPanel createPaymentMethodStep() {
        FormFlexPanel panel = new FormFlexPanel(i18n.tr("Payment Method"));

        panel.setWidget(0, 0, inject(proto().paymentMethod(), paymentMethodEditor));

        return panel;
    }

    private FormFlexPanel createConfirmationStep() {
        FormFlexPanel panel = new FormFlexPanel(i18n.tr("Confirmation"));
        int row = -1;

        panel.setWidget(++row, 0, confirmationDetailsHolder);

        panel.setBR(++row, 0, 1);

        panel.setWidget(++row, 0, inject(proto().coveredItems(), new PapCoveredItemFolder()));
        get(proto().coveredItems()).setViewable(true);
        get(proto().coveredItems()).inheritViewable(false);

        panel.setWidget(++row, 0, confirmationTotalHolder);

        panel.setBR(++row, 0, 1);

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().nextScheduledPaymentDate(), new CDateLabel()), 7, 25).build());
        panel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        panel.setHR(++row, 0, 1);

        panel.setWidget(++row, 0, createLegalTermsPanel());
        panel.getFlexCellFormatter().setAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);

        return panel;
    }

    @Override
    protected void onStepChange(SelectionEvent<VistaWizardStep> event) {
        super.onStepChange(event);
        if (event.getSelectedItem().equals(detailsStep)) {
            switchTotal(detailsTotalHolder, 38);
        } else if (event.getSelectedItem().equals(comfirmationStep)) {

            confirmationDetailsHolder.clear();
            ((PreauthorizedPaymentWizardView.Persenter) getView().getPresenter()).preview(new DefaultAsyncCallback<PreauthorizedPayment>() {
                @Override
                public void onSuccess(PreauthorizedPayment result) {
                    ((CComponent<List<PreauthorizedPayment.PreauthorizedPaymentCoveredItem>, ?>) get(proto().coveredItems())).populate(result.coveredItems());
                    confirmationDetailsHolder.setWidget(createConfirmationDetailsPanel());
                }
            }, getValue());

            switchTotal(confirmationTotalHolder, 40);
        }
    }

    private void switchTotal(SimplePanel holder, double width) {
        BigDecimal total = BigDecimal.ZERO;
        if (isBound(proto().total())) {
            total = get(proto().total()).getValue();
            unbind(proto().total());
        }
        holder.setWidget(new DecoratorBuilder(inject(proto().total()), 10, width).build());
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

        LogicalDate today = new LogicalDate(ClientContext.getServerDate());
        if (!today.before(getValue().paymentCutOffDate().getValue()) && !today.after(getValue().nextScheduledPaymentDate().getValue())) {
            get(proto().nextScheduledPaymentDate()).setNote(cutOffDateWarning, NoteStyle.Warn);
        } else {
            get(proto().nextScheduledPaymentDate()).setNote(null);
        }
    }

    private void loadProfiledPaymentMethods(final AsyncCallback<Void> callback) {
        profiledPaymentMethodsCombo.setOptions(null);
        ((PreauthorizedPaymentWizardView.Persenter) getView().getPresenter()).getProfiledPaymentMethods(new DefaultAsyncCallback<List<LeasePaymentMethod>>() {
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

        panel.add(new HTML(getValue().tenant().getStringView()));
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

        panel.setWidth("100%");
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
        panel.add(w = new Anchor(i18n.tr("Pre-Authorized Agreement"), new Command() {
            @Override
            public void execute() {
                switch (get(proto().paymentMethod()).getValue().type().getValue()) {
                case Echeck:
                    new LegalTermsDialog(TermsType.PreauthorisedPAD).show();
                    break;
                case CreditCard:
                    new LegalTermsDialog(TermsType.PreauthorisedCC).show();
                    break;
                default:
                    assert false : "Illegal payment method type!";
                    break;
                }
            }
        }));

        return panel;
    }
}
