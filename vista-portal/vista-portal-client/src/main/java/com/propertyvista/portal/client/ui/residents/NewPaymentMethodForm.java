/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents;

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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.Range;

import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.CRadioGroup;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;
import com.pyx4j.widgets.client.style.Selector;

import com.propertyvista.common.client.ui.components.AddressUtils;
import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.decorations.DecorationData;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaHeaderBar;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.portal.client.resources.PortalImages;
import com.propertyvista.portal.client.ui.validators.CreditCardNumberValidator;
import com.propertyvista.portal.domain.dto.PaymentMethodGenericDTO;
import com.propertyvista.portal.domain.payment.CreditCardInfo;
import com.propertyvista.portal.domain.payment.EcheckInfo;
import com.propertyvista.portal.domain.payment.PaymentType;

public class NewPaymentMethodForm extends CEntityEditor<PaymentMethodGenericDTO> {

    private static I18n i18n = I18nFactory.getI18n(NewPaymentMethodForm.class);

    private FlowPanel paymentTypeImagesPanel;

    private FlowPanel paymentFeesPanel;

    public static String PAYMENT_BUTTONS_STYLE_PREFIX = "PaymentRadioButtonGroup";

    public static enum StyleSuffix implements IStyleSuffix {
        PaymentImages, PaymentFee, PaymentForm
    }

    public static enum StyleDependent implements IStyleDependent {
        item, selected
    }

    public NewPaymentMethodForm() {
        super(PaymentMethodGenericDTO.class, new VistaEditorsComponentFactory());
    }

    @Override
    public void populate(PaymentMethodGenericDTO value) {
        super.populate(value);
        setInstrumentsVisibility(value.type().getValue());
    }

    @Override
    public IsWidget createContent() {

        VistaDecoratorsFlowPanel container = new VistaDecoratorsFlowPanel();
        container.add(new VistaHeaderBar(proto().type(), "100%"));
        CRadioGroupEnum<PaymentType> radioGroup = new CRadioGroupEnum<PaymentType>(PaymentType.class, CRadioGroup.Layout.VERTICAL);
        radioGroup.setStylePrefix(PAYMENT_BUTTONS_STYLE_PREFIX);

        paymentTypeImagesPanel = new FlowPanel();
        paymentTypeImagesPanel.setStyleName(Selector.getStyleName(PAYMENT_BUTTONS_STYLE_PREFIX, NewPaymentMethodForm.StyleSuffix.PaymentImages));
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
        container.add(paymentTypeImagesPanel);
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
        paymentFeesPanel.setStyleName(Selector.getStyleName(PAYMENT_BUTTONS_STYLE_PREFIX, NewPaymentMethodForm.StyleSuffix.PaymentFee));
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

        container.add(paymentType);
        container.add(paymentFeesPanel);
        container.add(instrumentsPanel);

        setPaymentTableVisibility(0);

        container.add(new VistaHeaderBar(proto().billingAddress(), "100%"));
        AddressUtils.injectIAddress(container, proto().billingAddress(), this);

        container.add(inject(proto().billingAddress().phone()), 12);
        container.setWidth("100%");
        setInstrumentsVisibility(PaymentType.Echeck);
        return container;

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
                                @SuppressWarnings("deprecation")
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
            index++;
        }

    }

}
