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
package com.propertyvista.portal.ptapp.client.ui.steps.payment;

import java.util.Date;

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

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Selector;
import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.CRadioGroup;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.editors.CAddressStructured;
import com.propertyvista.common.client.ui.components.folders.ChargeLineFolder;
import com.propertyvista.common.client.ui.decorations.DecorationData;
import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.validators.CreditCardNumberValidator;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.portal.domain.ptapp.PaymentInformation;
import com.propertyvista.portal.ptapp.client.resources.PortalImages;
import com.propertyvista.portal.ptapp.client.resources.PortalResources;

public class PaymentViewForm extends CEntityEditor<PaymentInformation> {

    private static I18n i18n = I18n.get(PaymentViewForm.class);

    private FlowPanel paymentTypeImagesPanel;

    private FlowPanel paymentFeesPanel;

    public static String PAYMENT_BUTTONS_STYLE_PREFIX = "PaymentRadioButtonGroup";

    public static enum StyleSuffix implements IStyleName {
        PaymentImages, PaymentFee, PaymentForm
    }

    public static enum StyleDependent implements IStyleDependent {
        item, selected
    }

    public PaymentViewForm() {
        super(PaymentInformation.class, new VistaEditorsComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;

        main.setH1(++row, 0, 1, i18n.tr(proto().applicationCharges().getMeta().getCaption()));

        main.setWidget(++row, 0, inject(proto().applicationCharges().charges(), new ChargeLineFolder(isEditable())));

        FlowPanel applicationFeePanel = new FlowPanel();
        applicationFeePanel.getElement().getStyle().setPaddingLeft(1, Unit.EM);
        applicationFeePanel.add(DecorationUtils.inline(inject(proto().applicationFee().label()), "300px"));
        applicationFeePanel.add(DecorationUtils.inline(inject(proto().applicationFee().amount()), "100px", "right"));
        main.setWidget(++row, 0, applicationFeePanel);

        HorizontalPanel info = new HorizontalPanel();
        info.getElement().getStyle().setMarginTop(1, Unit.EM);
        info.add(new Image(PortalImages.INSTANCE.userMessageInfo()));
        info.add(new HTML(PortalResources.INSTANCE.paymentApprovalNotes().getText()));
        info.getElement().getStyle().setMarginBottom(1, Unit.EM);
        main.setWidget(++row, 0, info);

        main.setH1(++row, 0, 1, i18n.tr(proto().type().getMeta().getCaption()));

        CRadioGroupEnum<PaymentType> radioGroup = new CRadioGroupEnum<PaymentType>(PaymentType.class, CRadioGroup.Layout.VERTICAL);
        radioGroup.setStylePrefix(PAYMENT_BUTTONS_STYLE_PREFIX);

        paymentTypeImagesPanel = new FlowPanel();
        paymentTypeImagesPanel.setStyleName(Selector.getStyleName(PAYMENT_BUTTONS_STYLE_PREFIX, PaymentViewForm.StyleSuffix.PaymentImages));
        Image paymentTypeImage;
        FlowPanel holder;
        for (int i = 0; i < PaymentType.values().length; i++) {
            switch (i) {
            case 0:
                paymentTypeImage = new Image(PortalImages.INSTANCE.paymentACH().getSafeUri());
                break;
            case 1:
                paymentTypeImage = new Image(PortalImages.INSTANCE.paymentVISA().getSafeUri());
                break;
            case 2:
                paymentTypeImage = new Image(PortalImages.INSTANCE.paymentAMEX().getSafeUri());
                break;
            case 3:
                paymentTypeImage = new Image(PortalImages.INSTANCE.paymentMC().getSafeUri());
                break;
            case 4:
                paymentTypeImage = new Image(PortalImages.INSTANCE.paymentDiscover().getSafeUri());
                break;
            default:
                paymentTypeImage = new Image(PortalImages.INSTANCE.paymentInterac().getSafeUri());
                break;
            }
            paymentTypeImage.setHeight("20px");
            holder = new FlowPanel();
            holder.add(paymentTypeImage);
            paymentTypeImagesPanel.add(holder);
        }
        paymentTypeImagesPanel.asWidget().getElement().getStyle().setFloat(Float.LEFT);
        main.setWidget(++row, 0, paymentTypeImagesPanel);

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

        main.setWidget(++row, 0, paymentType);
        main.setWidget(++row, 0, paymentFeesPanel);
        main.setWidget(++row, 0, instrumentsPanel);

        setPaymentTableVisibility(0);

        main.setH1(++row, 0, 1, i18n.tr(proto().billingAddress().getMeta().getCaption()));

        CCheckBox sameAsCurrent = (CCheckBox) inject(proto().sameAsCurrent());
        main.setWidget(++row, 0, sameAsCurrent);
        sameAsCurrent.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                setAsCurrentAddress(event.getValue());
            }
        });

        main.setWidget(++row, 0, inject(proto().billingAddress(), new CAddressStructured()));

        main.setWidget(++row, 0, inject(proto().phone()));

        main.setH1(++row, 0, 1, i18n.tr("Pre-Authorized Payment"));

        HorizontalPanel preauthorisedNotes = new HorizontalPanel();
        preauthorisedNotes.add(new HTML(PortalResources.INSTANCE.paymentPreauthorisedNotes_VISA().getText()));
        main.setWidget(++row, 0, preauthorisedNotes);

        main.setWidget(++row, 0, inject(proto().preauthorised()));

        main.setWidget(++row, 0, new HTML(PortalResources.INSTANCE.paymentTermsNotes().getText()));

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
        CAddressStructured addressForm = (CAddressStructured) get(proto().billingAddress());
        if (value == Boolean.TRUE) {
            //TODO use a better forms and copy of data
            addressForm.get(addressForm.proto().suiteNumber()).setValue(getValue().currentAddress().suiteNumber().getValue());
            addressForm.get(addressForm.proto().streetNumber()).setValue(getValue().currentAddress().streetNumber().getValue());
            addressForm.get(addressForm.proto().streetNumberSuffix()).setValue(getValue().currentAddress().streetNumberSuffix().getValue());
            addressForm.get(addressForm.proto().streetName()).setValue(getValue().currentAddress().streetName().getValue());
            addressForm.get(addressForm.proto().streetType()).setValue(getValue().currentAddress().streetType().getValue());
            addressForm.get(addressForm.proto().streetDirection()).setValue(getValue().currentAddress().streetDirection().getValue());
            addressForm.get(addressForm.proto().city()).setValue(getValue().currentAddress().city().getValue());
            addressForm.get(addressForm.proto().county()).setValue(getValue().currentAddress().county().getValue());
            addressForm.get(addressForm.proto().postalCode()).setValue(getValue().currentAddress().postalCode().getValue());
            get(proto().phone()).setValue(getValue().currentPhone());

            CComponent<Country, ?> country = addressForm.get(addressForm.proto().country());
            country.setValue(getValue().currentAddress().country());

            CComponent<Province, ?> prov = addressForm.get(addressForm.proto().province());
            prov.setValue(getValue().currentAddress().province());

            editable = false;
        }

        addressForm.get(addressForm.proto().suiteNumber()).setEditable(editable);
        addressForm.get(addressForm.proto().streetNumber()).setEditable(editable);
        addressForm.get(addressForm.proto().streetNumberSuffix()).setEditable(editable);
        addressForm.get(addressForm.proto().streetName()).setEditable(editable);
        addressForm.get(addressForm.proto().streetType()).setEditable(editable);
        addressForm.get(addressForm.proto().streetDirection()).setEditable(editable);
        addressForm.get(addressForm.proto().city()).setEditable(editable);
        addressForm.get(addressForm.proto().county()).setEditable(editable);
        addressForm.get(addressForm.proto().province()).setEditable(editable);
        addressForm.get(addressForm.proto().country()).setEditable(editable);
        addressForm.get(addressForm.proto().postalCode()).setEditable(editable);
        get(proto().phone()).setEditable(editable);
    }

    @Override
    public void populate(PaymentInformation value) {
        super.populate(value);
        setInstrumentsVisibility(value.type().getValue());
        setAsCurrentAddress(value.sameAsCurrent().getValue());
    }

    private CEntityEditor<EcheckInfo> createEcheckInfoEditor() {
        return new CEntityEditor<EcheckInfo>(EcheckInfo.class) {
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

    private CEntityEditor<CreditCardInfo> createCreditCardInfoEditor() {
        return new CEntityEditor<CreditCardInfo>(CreditCardInfo.class) {
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
            public CComponent<?, ?> create(IObject<?> member) {
                CComponent<?, ?> comp = super.create(member);
                if (comp instanceof CMonthYearPicker) {
                    Date now = new Date();
                    @SuppressWarnings("deprecation")
                    int y = 1900 + now.getYear();
                    ((CMonthYearPicker) comp).setYearRange(new Range(y, y + 10));

                    ((CMonthYearPicker) comp).addValueValidator(new EditableValueValidator<Date>() {

                        @Override
                        public boolean isValid(CComponent<Date, ?> component, Date value) {
                            if (value == null) {
                                return true;
                            } else {
                                Date now = new Date();
                                Date thisMonth = new Date(now.getYear(), now.getMonth(), 1);
                                return value.compareTo(thisMonth) >= 0;
                            }
                        }

                        @Override
                        public String getValidationMessage(CComponent<Date, ?> component, Date value) {
                            return i18n.tr("Expiration date should be a future date");
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

        CEntityEditor<EcheckInfo> entityComponent;

        private int index = 0;

        public CheckPanel(CEntityEditor<EcheckInfo> entityComponent) {
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
