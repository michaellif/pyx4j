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
package com.propertyvista.common.client.ui.components.c;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.Range;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.CRadioGroup;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.theme.NewPaymentMethodEditorTheme;
import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.editors.AddressStructuredEditor;
import com.propertyvista.common.client.ui.validators.CreditCardNumberValidator;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.InteracInfo;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;

public class NewPaymentMethodForm extends CEntityDecoratableEditor<PaymentMethod> {

    private static final I18n i18n = I18n.get(NewPaymentMethodForm.class);

    private FlowPanel paymentTypeImagesPanel;

    private CEntityEditor<AddressStructured> billingAddress;

    private final boolean twoColumns;

    public NewPaymentMethodForm() {
        this(false);
    }

    public NewPaymentMethodForm(boolean twoColumns) {
        super(PaymentMethod.class, new VistaEditorsComponentFactory());
        this.twoColumns = twoColumns;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();
        setInstrumentsVisibility(getValue().type().getValue());
    }

    @Override
    public IsWidget createContent() {

        FormFlexPanel container = new FormFlexPanel();

        container.setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditor.name());

        int row = -1;

        container.setH1(++row, 0, 3, proto().type().getMeta().getCaption());

        CRadioGroupEnum<PaymentType> radioGroup = new CRadioGroupEnum<PaymentType>(PaymentType.class, CRadioGroup.Layout.VERTICAL);

        paymentTypeImagesPanel = new FlowPanel();
        paymentTypeImagesPanel.setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorImages.name());
        Image paymentTypeImage;
        FlowPanel holder;

        for (PaymentType type : PaymentType.values()) {
            switch (type) {
            case Echeck:
                paymentTypeImage = new Image(VistaImages.INSTANCE.paymentACH().getSafeUri());
                break;
            case Visa:
                paymentTypeImage = new Image(VistaImages.INSTANCE.paymentVISA().getSafeUri());
                break;
            case MasterCard:
                paymentTypeImage = new Image(VistaImages.INSTANCE.paymentMC().getSafeUri());
                break;
            case Discover:
                paymentTypeImage = new Image(VistaImages.INSTANCE.paymentDiscover().getSafeUri());
                break;
            case Interac:
                paymentTypeImage = new Image(VistaImages.INSTANCE.paymentInterac().getSafeUri());
                break;
            default:
                paymentTypeImage = null;
                break;
            }
            if (paymentTypeImage != null) {
                paymentTypeImage.setHeight("20px");
                holder = new FlowPanel();
                holder.add(paymentTypeImage);
                paymentTypeImagesPanel.add(holder);
            }
        }

        container.setWidget(++row, 0, paymentTypeImagesPanel);

        CRadioGroup<PaymentType> paymentType = (CRadioGroup<PaymentType>) inject(proto().type(), radioGroup);

        paymentType.asWidget().setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorButtons.name());

        paymentType.addValueChangeHandler(new ValueChangeHandler<PaymentType>() {

            @Override
            public void onValueChange(ValueChangeEvent<PaymentType> event) {

                setInstrumentsVisibility(event.getValue());
            }
        });

        ComplexPanel instrumentsPanel = new FlowPanel();
        instrumentsPanel.asWidget().getElement().addClassName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorForm.name());

        instrumentsPanel.add(inject(proto().echeck(), createEcheckInfoEditor()));
        instrumentsPanel.add(inject(proto().creditCard(), createCreditCardInfoEditor()));
        instrumentsPanel.add(inject(proto().interac(), createInteracInfoEditor()));

        container.setWidget(row, 1, paymentType);

        container.setWidget(row, 2, instrumentsPanel);

        container.setH1(++row, 0, 3, proto().billingAddress().getMeta().getCaption());

        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().sameAsCurrent()), 5).build());
        container.getFlexCellFormatter().setColSpan(row, 0, 3);

        CComponent<?, ?> comp = get(proto().sameAsCurrent());
        if (comp instanceof CCheckBox) {
            ((CCheckBox) comp).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    onBillingAddressSameAsCurrentOne(event.getValue());
                    billingAddress.setEditable(!event.getValue());
                }
            });
        }

        container.setWidget(++row, 0, inject(proto().billingAddress(), billingAddress = new AddressStructuredEditor(twoColumns)));
        container.getFlexCellFormatter().setColSpan(row, 0, 3);

        container.setHR(++row, 0, 3);
        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().phone()), 15).build());
        container.getFlexCellFormatter().setColSpan(row, 0, 3);

        container.setWidth("100%");

        paymentType.setValue(PaymentType.Echeck);

        return container;

    }

    public void onBillingAddressSameAsCurrentOne(boolean set) {
        // Implements meaningful in derived classes...
    }

    private void setInstrumentsVisibility(PaymentType value) {
        get(proto().echeck()).setVisible(value == PaymentType.Echeck);
        get(proto().creditCard()).setVisible(isCardPayment(value));
        get(proto().interac()).setVisible(value == PaymentType.Interac);
        setPaymentTableVisibility(value.ordinal());
    }

    private boolean isCardPayment(PaymentType value) {
        return value == PaymentType.MasterCard | value == PaymentType.Visa | value == PaymentType.Discover;
    }

    private void setPaymentTableVisibility(int index) {
        int count = paymentTypeImagesPanel.getWidgetCount();
        for (int i = 0; i < count; i++) {
            paymentTypeImagesPanel.getWidget(i).removeStyleName(NewPaymentMethodEditorTheme.StyleDependent.selected.name());
        }
        paymentTypeImagesPanel.getWidget(index).addStyleName(NewPaymentMethodEditorTheme.StyleDependent.selected.name());
    }

    private CEntityEditor<EcheckInfo> createEcheckInfoEditor() {
        return new CEntityEditor<EcheckInfo>(EcheckInfo.class) {
            @Override
            public IsWidget createContent() {
                FlowPanel panel = new FlowPanel();

                panel.add(new InnerPanelWidgetDecorator(inject(proto().nameOnAccount())));
                panel.add(new InnerPanelWidgetDecorator(inject(proto().accountType())));
                panel.add(new InnerPanelWidgetDecorator(inject(proto().bankName())));

                CheckPanel checkPanel = new CheckPanel(this);
                checkPanel.add(proto().checkNo(), 58);
                checkPanel.add(proto().routingNo(), 85);
                checkPanel.add(proto().accountNo(), 85);

                HorizontalPanel hpanel = new HorizontalPanel();
                hpanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
                hpanel.add(checkPanel);
                hpanel.setCellWidth(checkPanel, "400");

                panel.add(hpanel);

                return panel;
            }
        };

    }

    private CEntityEditor<CreditCardInfo> createCreditCardInfoEditor() {
        return new CEntityEditor<CreditCardInfo>(CreditCardInfo.class) {
            @Override
            public IsWidget createContent() {
                FlowPanel panel = new FlowPanel();
                panel.add(new InnerPanelWidgetDecorator(inject(proto().number())));
                panel.add(new InnerPanelWidgetDecorator(inject(proto().expiryDate())));
                panel.add(new InnerPanelWidgetDecorator(inject(proto().name())));
                panel.add(new InnerPanelWidgetDecorator(inject(proto().securityCode())));
//                panel.add(new InnerPanelWidgetDecorator(inject(proto().bankPhone())));
                return panel;
            }

            @Override
            public CComponent<?, ?> create(IObject<?> member) {
                CComponent<?, ?> comp = super.create(member);
                if (comp instanceof CMonthYearPicker) {
                    Date now = new Date();
                    @SuppressWarnings("deprecation")
                    int y = 1900 + now.getYear();
                    ((CMonthYearPicker) comp).setYearRange(new Range(y, 10));

                    ((CMonthYearPicker) comp).addValueValidator(new EditableValueValidator<Date>() {

                        @Override
                        public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                            if (value == null) {
                                return null;
                            } else {
                                Date now = new Date();
                                @SuppressWarnings("deprecation")
                                Date thisMonth = new Date(now.getYear(), now.getMonth(), 1);
                                return value.compareTo(thisMonth) >= 0 ? null : new ValidationFailure(i18n.tr("Card expiry should be a future date"));
                            }
                        }

                    });
                }
                return comp;
            }

            @Override
            public void addValidations() {
                super.addValidations();
                get(proto().number()).addValueValidator(new CreditCardNumberValidator());
            }
        };
    }

    private CEntityEditor<InteracInfo> createInteracInfoEditor() {
        return new CEntityEditor<InteracInfo>(InteracInfo.class) {
            @Override
            public IsWidget createContent() {
                FlowPanel panel = new FlowPanel();
                panel.add(InteracPanelCanada());

                return panel;
            }
        };

    }

    private HorizontalPanel InteracPanelCanada() {
        HorizontalPanel panel = new HorizontalPanel();
        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        panel.getElement().getStyle().setProperty("padding", "5px");

        Image image = new Image(VistaImages.INSTANCE.logoBMO().getSafeUri());
        image.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                interacRedirect("BMO");
            }
        });
        panel.add(image);
        panel.setCellWidth(image, "100");
        image = new Image(VistaImages.INSTANCE.logoRBC().getSafeUri());
        image.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                interacRedirect("RBC");
            }
        });
        panel.add(image);
        panel.setCellWidth(image, "100");
        image = new Image(VistaImages.INSTANCE.logoTD().getSafeUri());
        image.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                interacRedirect("TD");
            }
        });
        panel.add(image);
        panel.setCellWidth(image, "100");
        image = new Image(VistaImages.INSTANCE.logoScotia().getSafeUri());
        image.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                interacRedirect("Scotia");
            }
        });
        panel.add(image);
        panel.setCellWidth(image, "100");

        return panel;
    }

    private void interacRedirect(String site) { //TODO add a method for creating proper Interac links
        String url = null;
        if (site.equals("BMO")) {
            url = "https://www12.bmo.com/cgi-bin/netbnx/NBmain?product=1";
        } else if (site.equals("RBC")) {
            url = "https://www1.royalbank.com/cgi-bin/rbaccess/rbunxcgi?F6=1&F7=IB&F21=IB&F22=IB&REQUEST=ClientSignin&LANGUAGE=ENGLISH";
        } else if (site.equals("TD")) {
            url = "https://easywebcpo.td.com/waw/idp/login.htm?execution=e1s1";
        } else if (site.equals("Scotia")) {
            url = "https://www1.scotiaonline.scotiabank.com/online/authentication/authentication.bns";
        } else {
            Window.alert("Proper link is not set up yet");
            url = "www.google.com";
        }

        Window.open(url, Media.Type.externalUrl.name(), null);

    }

    class CheckPanel extends FlexTable {

        CEntityEditor<EcheckInfo> entityComponent;

        private int index = 0;

        public CheckPanel(CEntityEditor<EcheckInfo> entityComponent) {
            this.entityComponent = entityComponent;
            setWidget(1, 0, new Image(VistaImages.INSTANCE.canadianChequeGuide()));
            getFlexCellFormatter().setColSpan(1, 0, 3);
        }

        public void add(IObject<?> object, int width) {
            setWidget(0, index, entityComponent.inject(object).asWidget());
            getFlexCellFormatter().setWidth(0, index, width + "px");
            index++;
        }

    }

    class InnerPanelWidgetDecorator extends WidgetDecorator {

        public InnerPanelWidgetDecorator(CComponent<?, ?> component) {
            super(new Builder(component).labelWidth(12).componentWidth(12));
        }

    }
}
