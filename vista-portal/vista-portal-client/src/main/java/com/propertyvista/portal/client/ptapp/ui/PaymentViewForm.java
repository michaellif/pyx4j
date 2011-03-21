/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import java.util.Date;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.propertyvista.common.client.ui.ViewLineSeparator;
import com.propertyvista.common.client.ui.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.VistaWidgetDecorator.DecorationData;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.client.ptapp.resources.SiteResources;
import com.propertyvista.portal.client.ptapp.ui.components.AddressUtils;
import com.propertyvista.portal.client.ptapp.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.portal.client.ptapp.ui.decorations.DecorationUtils;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.portal.client.ptapp.ui.validators.CreditCardNumberValidator;
import com.propertyvista.portal.domain.payment.CreditCardInfo;
import com.propertyvista.portal.domain.payment.EcheckInfo;
import com.propertyvista.portal.domain.payment.PaymentType;
import com.propertyvista.portal.domain.pt.PaymentInfo;
import com.propertyvista.portal.domain.ref.Province;

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.CRadioGroup;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.validators.EditableValueValidator;

public class PaymentViewForm extends CEntityForm<PaymentInfo> {

    private static I18n i18n = I18nFactory.getI18n(PaymentViewForm.class);

    public PaymentViewForm() {
        super(PaymentInfo.class, new VistaEditorsComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(new ViewHeaderDecorator(proto().applicationCharges()));
        main.add(inject(proto().applicationCharges().charges(), new ChargeLineFolder()));

        ViewLineSeparator sp = new ViewLineSeparator(0, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
        sp.getElement().getStyle().setMarginLeft(1, Unit.EM);
        main.add(sp);

        FlowPanel applicationFeePanel = new FlowPanel();
        applicationFeePanel.getElement().getStyle().setPaddingLeft(1, Unit.EM);
        applicationFeePanel.add(DecorationUtils.inline(inject(proto().applicationFee().label()), "300px"));
        applicationFeePanel.add(DecorationUtils.inline(inject(proto().applicationFee().charge()), "100px", "right"));
        main.add(applicationFeePanel);

        HorizontalPanel info = new HorizontalPanel();
        info.getElement().getStyle().setMarginTop(1, Unit.EM);
        info.add(new Image(SiteImages.INSTANCE.userMessageInfo()));
        info.add(new HTML(SiteResources.INSTANCE.paymentApprovalNotes().getText()));
        info.getElement().getStyle().setMarginBottom(1, Unit.EM);
        main.add(info);

        main.add(new ViewHeaderDecorator(proto().type()));
        @SuppressWarnings("unchecked")
        CRadioGroup<PaymentType> paymentType = (CRadioGroup<PaymentType>) inject(proto().type(), new CRadioGroupEnum<PaymentType>(PaymentType.class,
                CRadioGroup.Layout.VERTICAL));
        paymentType.addValueChangeHandler(new ValueChangeHandler<PaymentType>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentType> event) {
                setInstrumentsVisibility(event.getValue());
            }
        });
        paymentType.asWidget().getElement().getStyle().setFloat(Float.LEFT);
        main.add(paymentType);

        ComplexPanel instrumentsPanel = new FlowPanel();
        instrumentsPanel.asWidget().getElement().getStyle().setFloat(Float.RIGHT);
        instrumentsPanel.getElement().getStyle().setPaddingRight(50, Unit.PX);
        instrumentsPanel.getElement().getStyle().setBorderWidth(1, Unit.PX);
        instrumentsPanel.add(inject(proto().echeck(), createEcheckInfoEditor()));
        instrumentsPanel.add(inject(proto().creditCard(), createCreditCardInfoEditor()));
        main.add(instrumentsPanel);

        main.add(new ViewHeaderDecorator(proto().billingAddress()));
        CCheckBox sameAsCurrent = (CCheckBox) inject(proto().sameAsCurrent());
        main.add(sameAsCurrent, 12);
        sameAsCurrent.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                setAsCurrentAddress(event.getValue());
            }
        });

        AddressUtils.injectIAddress(main, proto().billingAddress(), this);

        main.add(inject(proto().billingAddress().phone()), 12);

        main.add(new ViewHeaderDecorator(i18n.tr("Pre-Authorized Payment")));
        HorizontalPanel preauthorisedNotes = new HorizontalPanel();
        preauthorisedNotes.add(new HTML(SiteResources.INSTANCE.paymentPreauthorisedNotes().getText()));
        main.add(preauthorisedNotes);

        main.add(inject(proto().preauthorised()), 12);

        main.add(new HTML(SiteResources.INSTANCE.paymentTermsNotes().getText()));

        main.setWidth("700px");

        return main;
    }

    private void setInstrumentsVisibility(PaymentType value) {
        boolean card = (value != PaymentType.Echeck);

        get(proto().echeck()).setVisible(!card);
        get(proto().creditCard()).setVisible(card);
    }

    private void setAsCurrentAddress(Boolean value) {
        boolean editable = true;
        if (value == Boolean.TRUE) {
            //TODO use a better forms and copy of data
            get(proto().billingAddress().street1()).setValue(getValue().currentAddress().street1().getValue());
            get(proto().billingAddress().street2()).setValue(getValue().currentAddress().street2().getValue());
            get(proto().billingAddress().city()).setValue(getValue().currentAddress().city().getValue());
            get(proto().billingAddress().postalCode()).setValue(getValue().currentAddress().postalCode().getValue());
            get(proto().billingAddress().phone()).setValue(getValue().currentPhone().getValue());

            @SuppressWarnings("unchecked")
            CEditableComponent<Province, ?> prov = (CEditableComponent<Province, ?>) getRaw(proto().billingAddress().province());
            prov.setValue(getValue().currentAddress().province());
            editable = false;
        }

        get(proto().billingAddress().street1()).setEditable(editable);
        get(proto().billingAddress().street2()).setEditable(editable);
        get(proto().billingAddress().city()).setEditable(editable);
        get(proto().billingAddress().postalCode()).setEditable(editable);
        get(proto().billingAddress().province()).setEditable(editable);
        get(proto().billingAddress().phone()).setEditable(editable);
    }

    @Override
    public void populate(PaymentInfo value) {
        super.populate(value);
        setInstrumentsVisibility(value.type().getValue());
        setAsCurrentAddress(value.sameAsCurrent().getValue());
    }

    private CEntityEditableComponent<EcheckInfo> createEcheckInfoEditor() {
        return new CEntityEditableComponent<EcheckInfo>(EcheckInfo.class) {
            @Override
            public IsWidget createContent() {
                FlowPanel panel = new FlowPanel();

                DecorationData decorData = new DecorationData();
                decorData.componentWidth = 12;
                panel.add(new VistaWidgetDecorator(inject(proto().nameOnAccount()), decorData));
                panel.add(new VistaWidgetDecorator(inject(proto().accountType()), decorData));
                panel.add(new VistaWidgetDecorator(inject(proto().bankName()), decorData));

                CheckPanel checkPanel = new CheckPanel(this);
                checkPanel.add(proto().routingNo(), 85);
                checkPanel.add(proto().accountNo(), 85);
                checkPanel.add(proto().checkNo(), 58);

                panel.add(checkPanel);

                return panel;
            }
        };

    }

    private CEntityEditableComponent<CreditCardInfo> createCreditCardInfoEditor() {
        return new CEntityEditableComponent<CreditCardInfo>(CreditCardInfo.class) {
            @Override
            public IsWidget createContent() {
                FlowPanel panel = new FlowPanel();
                DecorationData decorData = new DecorationData();
                decorData.componentWidth = 12;
                panel.add(new VistaWidgetDecorator(inject(proto().cardNumber()), decorData));
                panel.add(new VistaWidgetDecorator(inject(proto().expiry()), decorData));
                panel.add(new VistaWidgetDecorator(inject(proto().exactName()), decorData));
                panel.add(new VistaWidgetDecorator(inject(proto().bankPhone()), decorData));
                return panel;
            }

            @Override
            public CEditableComponent<?, ?> create(IObject<?> member) {
                CEditableComponent<?, ?> comp = super.create(member);
                if (comp instanceof CMonthYearPicker) {
                    Date now = new Date();
                    @SuppressWarnings("deprecation")
                    int y = 1900 + now.getYear();
                    ((CMonthYearPicker) comp).setYearOptionsRange(y, y + 10);

                    ((CMonthYearPicker) comp).addValueValidator(new EditableValueValidator<Date>() {

                        @Override
                        public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                            if (value == null) {
                                return true;
                            } else {
                                Date now = new Date();
                                Date thisMonth = new Date(now.getYear(), now.getMonth(), 1);
                                return value.compareTo(thisMonth) >= 0;
                            }
                        }

                        @Override
                        public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                            return i18n.tr("Card expiry should be a future date");
                        }
                    });
                }
                return comp;
            }

            @Override
            public void addValidations() {
                super.addValidations();
                get(proto().cardNumber()).addValueValidator(new CreditCardNumberValidator());
            }
        };
    }

    class CheckPanel extends FlexTable {

        CEntityEditableComponent<EcheckInfo> entityComponent;

        private int index = 0;

        public CheckPanel(CEntityEditableComponent<EcheckInfo> entityComponent) {
            this.entityComponent = entityComponent;
            setWidget(1, 0, new Image(SiteImages.INSTANCE.chequeGuide()));
            getFlexCellFormatter().setColSpan(1, 0, 3);
        }

        public void add(IObject<?> object, int width) {
            setWidget(0, index, entityComponent.inject(object).asWidget());
            getFlexCellFormatter().setWidth(0, index, width + "px");
            //setWidget(2, index, new HTML(object.getMeta().getCaption()));
            index++;
        }

    }

}
