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
package com.propertyvista.portal.ptapp.client.ui;

import java.util.Date;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Position;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.Range;

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.CRadioGroup;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;
import com.pyx4j.widgets.client.style.Selector;

import com.propertyvista.common.client.ui.AddressUtils;
import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.common.client.ui.decorations.VistaHeaderDecorator;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator.DecorationData;
import com.propertyvista.common.domain.ref.Country;
import com.propertyvista.common.domain.ref.Province;
import com.propertyvista.portal.domain.payment.CreditCardInfo;
import com.propertyvista.portal.domain.payment.EcheckInfo;
import com.propertyvista.portal.domain.payment.PaymentType;
import com.propertyvista.portal.domain.ptapp.PaymentInfo;
import com.propertyvista.portal.ptapp.client.resources.PortalImages;
import com.propertyvista.portal.ptapp.client.resources.PortalResources;
import com.propertyvista.portal.ptapp.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.portal.ptapp.client.ui.validators.CreditCardNumberValidator;

public class PaymentViewForm extends CEntityForm<PaymentInfo> {

    private static I18n i18n = I18nFactory.getI18n(PaymentViewForm.class);

    private FlowPanel paymentTypeImagesPanel;

    private FlowPanel paymentFeesPanel;

    public static String PAYMENT_BUTTONS_STYLE_PREFIX = "PaymentRadioButtonGroup";

    public static enum StyleSuffix implements IStyleSuffix {
        PaymentImages, PaymentFee, PaymentForm
    }

    public static enum StyleDependent implements IStyleDependent {
        item, selected
    }

    public PaymentViewForm() {
        super(PaymentInfo.class, new VistaEditorsComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(new VistaHeaderDecorator(proto().applicationCharges()));
        main.add(inject(proto().applicationCharges().charges(), new ChargeLineFolder()));

        VistaLineSeparator sp = new VistaLineSeparator(0, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
        sp.getElement().getStyle().setMarginLeft(1, Unit.EM);
        main.add(sp);

        FlowPanel applicationFeePanel = new FlowPanel();
        applicationFeePanel.getElement().getStyle().setPaddingLeft(1, Unit.EM);
        applicationFeePanel.add(DecorationUtils.inline(inject(proto().applicationFee().label()), "300px"));
        applicationFeePanel.add(DecorationUtils.inline(inject(proto().applicationFee().charge()), "100px", "right"));
        main.add(applicationFeePanel);

        HorizontalPanel info = new HorizontalPanel();
        info.getElement().getStyle().setMarginTop(1, Unit.EM);
        info.add(new Image(PortalImages.INSTANCE.userMessageInfo()));
        info.add(new HTML(PortalResources.INSTANCE.paymentApprovalNotes().getText()));
        info.getElement().getStyle().setMarginBottom(1, Unit.EM);
        main.add(info);

        main.add(new VistaHeaderDecorator(proto().type()));
        CRadioGroupEnum<PaymentType> radioGroup = new CRadioGroupEnum<PaymentType>(PaymentType.class, CRadioGroup.Layout.VERTICAL);
        radioGroup.setStylePrefix(PAYMENT_BUTTONS_STYLE_PREFIX);

        paymentTypeImagesPanel = new FlowPanel();
        paymentTypeImagesPanel.setStyleName(Selector.getStyleName(PAYMENT_BUTTONS_STYLE_PREFIX, PaymentViewForm.StyleSuffix.PaymentImages));
        Image paymentTypeImage;
        FlowPanel holder;
        for (int i = 0; i < PaymentType.values().length; i++) {
            switch (i) {
            case 0:
                paymentTypeImage = new Image(PortalImages.INSTANCE.paymentACH().getURL());
                break;
            case 1:
                paymentTypeImage = new Image(PortalImages.INSTANCE.paymentVISA().getURL());
                break;
            case 2:
                paymentTypeImage = new Image(PortalImages.INSTANCE.paymentAMEX().getURL());
                break;
            case 3:
                paymentTypeImage = new Image(PortalImages.INSTANCE.paymentMC().getURL());
                break;
            case 4:
                paymentTypeImage = new Image(PortalImages.INSTANCE.paymentDiscover().getURL());
                break;
            default:
                paymentTypeImage = new Image(PortalImages.INSTANCE.paymentInterac().getURL());
                break;
            }
            paymentTypeImage.setHeight("20px");
            holder = new FlowPanel();
            holder.add(paymentTypeImage);
            paymentTypeImagesPanel.add(holder);
        }
        paymentTypeImagesPanel.asWidget().getElement().getStyle().setFloat(Float.LEFT);
        main.add(paymentTypeImagesPanel);

        CRadioGroup<PaymentType> paymentType = (CRadioGroup<PaymentType>) inject(proto().type(), radioGroup);
        paymentType.addValueChangeHandler(new ValueChangeHandler<PaymentType>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentType> event) {
                int index = event.getValue().ordinal();
                setPaymentTableVisibility(index);
                setInstrumentsVisibility(event.getValue());
            }
        });
        paymentType.asWidget().getElement().getStyle().setFloat(Float.LEFT);

        paymentFeesPanel = new FlowPanel();
        paymentFeesPanel.setStyleName(Selector.getStyleName(PAYMENT_BUTTONS_STYLE_PREFIX, PaymentViewForm.StyleSuffix.PaymentFee));
        Label paymentFeesLabel;
        for (PaymentType type : PaymentType.values()) {
            paymentFeesLabel = new Label("Convenience fee: $1.99");
            paymentFeesPanel.add(paymentFeesLabel);
        }
        paymentFeesPanel.asWidget().getElement().getStyle().setFloat(Float.LEFT);

        ComplexPanel instrumentsPanel = new FlowPanel();
        instrumentsPanel.asWidget().getElement().getStyle().setFloat(Float.LEFT);
        instrumentsPanel.asWidget().getElement().addClassName(Selector.getStyleName(PAYMENT_BUTTONS_STYLE_PREFIX, StyleSuffix.PaymentForm));
        instrumentsPanel.getElement().getStyle().setHeight(184, Unit.PX);
        instrumentsPanel.getElement().getStyle().setWidth(363, Unit.PX);
        instrumentsPanel.getElement().getStyle().setPaddingRight(50, Unit.PX);
        instrumentsPanel.getElement().getStyle().setPaddingLeft(50, Unit.PX);
        instrumentsPanel.getElement().getStyle().setPaddingTop(10, Unit.PX);
        instrumentsPanel.getElement().getStyle().setPaddingBottom(10, Unit.PX);
        instrumentsPanel.getElement().getStyle().setBorderWidth(1, Unit.PX);
        instrumentsPanel.getElement().getStyle().setBorderColor("#bbb");
        instrumentsPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
        instrumentsPanel.getElement().getStyle().setBackgroundColor("white");
        instrumentsPanel.getElement().getStyle().setPosition(Position.RELATIVE);
        instrumentsPanel.getElement().getStyle().setLeft(-1, Unit.PX);

        instrumentsPanel.add(inject(proto().echeck(), createEcheckInfoEditor()));
        instrumentsPanel.add(inject(proto().creditCard(), createCreditCardInfoEditor()));

        main.add(paymentType);
        main.add(paymentFeesPanel);
        main.add(instrumentsPanel);

        setPaymentTableVisibility(0);

        main.add(new VistaHeaderDecorator(proto().billingAddress()));
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

        main.add(new VistaHeaderDecorator(i18n.tr("Pre-Authorized Payment")));
        HorizontalPanel preauthorisedNotes = new HorizontalPanel();
        preauthorisedNotes.add(new HTML(PortalResources.INSTANCE.paymentPreauthorisedNotes().getText()));
        main.add(preauthorisedNotes);

        main.add(inject(proto().preauthorised()), 12);

        main.add(new HTML(PortalResources.INSTANCE.paymentTermsNotes().getText()));

        main.setWidth("900px");

        return main;
    }

    private void setInstrumentsVisibility(PaymentType value) {
        boolean card = (value != PaymentType.Echeck);

        get(proto().echeck()).setVisible(!card);
        get(proto().creditCard()).setVisible(card);
    }

    private void setPaymentTableVisibility(int index) {
        int count = paymentFeesPanel.getWidgetCount();
        for (int i = 0; i < count; i++) {
            paymentTypeImagesPanel.getWidget(i).removeStyleName(Selector.getDependentName(StyleDependent.selected));
            paymentFeesPanel.getWidget(i).removeStyleName(Selector.getDependentName(StyleDependent.selected));
        }
        paymentTypeImagesPanel.getWidget(index).addStyleName(Selector.getDependentName(StyleDependent.selected));
        paymentFeesPanel.getWidget(index).addStyleName(Selector.getDependentName(StyleDependent.selected));
    }

    private void setAsCurrentAddress(Boolean value) {
        boolean editable = true;
        if (value == Boolean.TRUE) {
            //TODO use a better forms and copy of data
            get(proto().billingAddress().unitNumber()).setValue(getValue().currentAddress().unitNumber().getValue());
            get(proto().billingAddress().streetNumber()).setValue(getValue().currentAddress().streetNumber().getValue());
            get(proto().billingAddress().streetNumberSuffix()).setValue(getValue().currentAddress().streetNumberSuffix().getValue());
            get(proto().billingAddress().streetName()).setValue(getValue().currentAddress().streetName().getValue());
            get(proto().billingAddress().streetType()).setValue(getValue().currentAddress().streetType().getValue());
            get(proto().billingAddress().streetDirection()).setValue(getValue().currentAddress().streetDirection().getValue());
            get(proto().billingAddress().city()).setValue(getValue().currentAddress().city().getValue());
            get(proto().billingAddress().county()).setValue(getValue().currentAddress().county().getValue());
            get(proto().billingAddress().postalCode()).setValue(getValue().currentAddress().postalCode().getValue());
            get(proto().billingAddress().phone()).setValue(getValue().currentPhone().getValue());

            @SuppressWarnings("unchecked")
            CEditableComponent<Country, ?> country = (CEditableComponent<Country, ?>) getRaw(proto().billingAddress().country());
            country.setValue(getValue().currentAddress().country());

            @SuppressWarnings("unchecked")
            CEditableComponent<Province, ?> prov = (CEditableComponent<Province, ?>) getRaw(proto().billingAddress().province());
            prov.setValue(getValue().currentAddress().province());

            editable = false;
        }

        get(proto().billingAddress().unitNumber()).setEditable(editable);
        get(proto().billingAddress().streetNumber()).setEditable(editable);
        get(proto().billingAddress().streetNumberSuffix()).setEditable(editable);
        get(proto().billingAddress().streetName()).setEditable(editable);
        get(proto().billingAddress().streetType()).setEditable(editable);
        get(proto().billingAddress().streetDirection()).setEditable(editable);
        get(proto().billingAddress().city()).setEditable(editable);
        get(proto().billingAddress().county()).setEditable(editable);
        get(proto().billingAddress().province()).setEditable(editable);
        get(proto().billingAddress().country()).setEditable(editable);
        get(proto().billingAddress().postalCode()).setEditable(editable);
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
                    ((CMonthYearPicker) comp).setYearRange(new Range(y, y + 10));

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
            setWidget(1, 0, new Image(PortalImages.INSTANCE.chequeGuide()));
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
