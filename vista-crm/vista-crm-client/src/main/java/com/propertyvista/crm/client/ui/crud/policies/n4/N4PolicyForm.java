/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.n4;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IFormat;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CPhoneField;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.components.editors.AddressSimpleEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.YardiChargeCode;
import com.propertyvista.domain.policy.dto.N4PolicyDTO;
import com.propertyvista.domain.policy.dto.N4PolicyDTOARCodeHolderDTO;

public class N4PolicyForm extends PolicyDTOTabPanelBasedForm<N4PolicyDTO> {

    public static final I18n i18n = I18n.get(N4PolicyForm.class);

    private ARCodeFolder arCodeFolder;

    public static final IFormat<String> PHONE_NUMBER_WITHOUT_EXTENSION_FORMAT = new IFormat<String>() {

        private final IFormat<String> phoneFormat = new CPhoneField.PhoneFormat();

        private final String regex = "^[\\s\\d\\(\\)-]+$";

        private final String errorMessage = i18n.tr("Invalid phone format. Use (123) 456-7890 format");

        @Override
        public String format(String value) {
            return phoneFormat.format(value);
        }

        @Override
        public String parse(String string) throws ParseException {
            if (CommonsStringUtils.isEmpty(string)) {
                return null;
            }
            String parsed = null;
            if (!string.matches(regex)) {
                throw new ParseException(errorMessage, 0);
            }
            try {
                parsed = phoneFormat.parse(string);
            } catch (ParseException e) {
                throw new ParseException(errorMessage, 0);
            }
            return parsed;
        }
    };

    public N4PolicyForm(IForm<N4PolicyDTO> view) {
        super(N4PolicyDTO.class, view);
    }

    public void setARCodeOptions(List<ARCode> arCodeOptions) {
        arCodeFolder.setARCodeOptions(arCodeOptions);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {

        TwoColumnFlexFormPanel signaturePanel = new TwoColumnFlexFormPanel(i18n.tr("Signature"));
        FlowPanel companyNameAndPhones = new FlowPanel();
        int row = -1;
        signaturePanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().includeSignature())).build());
        signaturePanel.setH1(++row, 0, 2, i18n.tr("The following information will be used for signing N4 letters:"));

        companyNameAndPhones.add(new FormDecoratorBuilder(inject(proto().companyName())).build());
        CPhoneField phoneNumberField = inject(proto().phoneNumber(), new CPhoneField() {
            @Override
            public IFormat<String> getFormat() {
                return PHONE_NUMBER_WITHOUT_EXTENSION_FORMAT;
            }
        });
        phoneNumberField.setWatermark("(___) ___-____");
        phoneNumberField.setFormat(PHONE_NUMBER_WITHOUT_EXTENSION_FORMAT); // TODO y setFormat not working?
        companyNameAndPhones.add(new FormDecoratorBuilder(phoneNumberField).build());

        CPhoneField faxNumberField = inject(proto().faxNumber(), new CPhoneField() {
            @Override
            public IFormat<String> getFormat() {
                return PHONE_NUMBER_WITHOUT_EXTENSION_FORMAT;
            }
        });
        faxNumberField.setWatermark("(___) ___-____");
        faxNumberField.setFormat(PHONE_NUMBER_WITHOUT_EXTENSION_FORMAT); // TODO y setFormat not working?
        companyNameAndPhones.add(new FormDecoratorBuilder(faxNumberField).build());
        companyNameAndPhones.add(new FormDecoratorBuilder(inject(proto().emailAddress())).build());
        signaturePanel.setWidget(++row, 0, 1, companyNameAndPhones);
        signaturePanel.getFlexCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);

        AddressSimpleEditor addressSimpleEditor = new AddressSimpleEditor();
        row = 1;
        signaturePanel.setWidget(++row, 1, 1, inject(proto().mailingAddress(), addressSimpleEditor));

        TwoColumnFlexFormPanel arCodesPanel = new TwoColumnFlexFormPanel(i18n.tr("AR Codes"));
        row = -1;
        arCodesPanel.setH1(++row, 0, 2, i18n.tr("Use the following AR Codes for calculation of charged vs. owed rent amount:"));
        arCodesPanel.setWidget(++row, 0, 2, inject(proto().arCodes(), arCodeFolder = new ARCodeFolder()));

        TwoColumnFlexFormPanel deliveryPanel = new TwoColumnFlexFormPanel(i18n.tr("Delivery"));
        row = -1;
        deliveryPanel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().handDeliveryAdvanceDays())).build());
        deliveryPanel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().mailDeliveryAdvanceDays())).build());
        deliveryPanel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().courierDeliveryAdvanceDays())).build());

        return Arrays.asList(signaturePanel, arCodesPanel, deliveryPanel);
    }

    public static class ARCodeFolder extends VistaBoxFolder<N4PolicyDTOARCodeHolderDTO> {

        private List<ARCode> arCodeOptions;

        public ARCodeFolder() {
            super(N4PolicyDTOARCodeHolderDTO.class);
            setOrderable(false);
            setViewable(true);
        }

        public void setARCodeOptions(List<ARCode> arCodeOptions) {
            this.arCodeOptions = arCodeOptions;
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof N4PolicyDTOARCodeHolderDTO) {
                return new N4PolicyDTOARCodeHolderForm(null, false);
            }
            return super.create(member);
        }

        @Override
        public IFolderItemDecorator<N4PolicyDTOARCodeHolderDTO> createItemDecorator() {
            VistaBoxFolderItemDecorator<N4PolicyDTOARCodeHolderDTO> d = (VistaBoxFolderItemDecorator<N4PolicyDTOARCodeHolderDTO>) super.createItemDecorator();
            d.setCollapsible(false);
            return d;
        }

        @Override
        protected void addItem() {
            new N4PolicyDTOARCodeHolderFormDialog(arCodeOptions) {
                @Override
                public boolean onClickOk() {
                    if (super.onClickOk()) {
                        ARCodeFolder.this.addItem(getValue());
                        return true;
                    } else {
                        return false;
                    }
                }
            }.show();
        }

    }

    public abstract static class N4PolicyDTOARCodeHolderFormDialog extends OkCancelDialog {

        private final N4PolicyDTOARCodeHolderForm form;

        public N4PolicyDTOARCodeHolderFormDialog(List<ARCode> codeOptions) {
            super(i18n.tr("Select AR Code"));
            form = new N4PolicyDTOARCodeHolderForm(codeOptions, true);
            form.initContent();
            form.populateNew();
            setBody(form);
        }

        @Override
        public boolean onClickOk() {
            form.revalidate();
            return form.isValid();
        }

        public final N4PolicyDTOARCodeHolderDTO getValue() {
            return form.getValue();
        }
    }

    public static class N4PolicyDTOARCodeHolderForm extends CEntityForm<N4PolicyDTOARCodeHolderDTO> {

        private CComboBox<ARCode> arCodeBox;

        private final boolean inlineARCodes;

        private Label yardiChargeCodesLabel;

        public N4PolicyDTOARCodeHolderForm(List<ARCode> codeOptions, final boolean inlineARCodes) {
            super(N4PolicyDTOARCodeHolderDTO.class);
            this.inlineARCodes = inlineARCodes;
            this.arCodeBox = new CComboBox<ARCode>() {
                @Override
                public String getItemName(ARCode o) {
                    if (o == null) {
                        return super.getItemName(o);
                    }
                    return o.getStringView()
                            + (o.yardiChargeCodes().isEmpty() | !inlineARCodes ? "" : " "
                                    + i18n.tr("(Yardi charge codes: {0})", yardiChargeCodesLabel(o.yardiChargeCodes())));
                }
            };
            this.arCodeBox.setOptions(codeOptions);
            this.yardiChargeCodesLabel = new Label();
            this.yardiChargeCodesLabel.getElement().getStyle().setPaddingLeft(4, Unit.EM);
            this.yardiChargeCodesLabel.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
        }

        @Override
        public IsWidget createContent() {
            FlowPanel panel = new FlowPanel();
            panel.add(inject(proto().arCode(), arCodeBox));
            panel.add(yardiChargeCodesLabel);
            return panel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);
            if (!inlineARCodes && !getValue().arCode().yardiChargeCodes().isEmpty()) {
                yardiChargeCodesLabel.setVisible(true);
                yardiChargeCodesLabel.setText(i18n.tr("Warning! Includes the following Yardi charge codes: {0}", yardiChargeCodesLabel(getValue().arCode()
                        .yardiChargeCodes())));
            } else {
                yardiChargeCodesLabel.setVisible(false);
                yardiChargeCodesLabel.setText("");
            }
        }

        public void setArCodeOptions(List<ARCode> arCodes) {
            arCodeBox.setOptions(arCodes);
        }

        private static String yardiChargeCodesLabel(List<YardiChargeCode> yardiChargeCodes) {
            StringBuilder builder = new StringBuilder();

            int last = yardiChargeCodes.size();
            int current = 1;
            for (YardiChargeCode chargeCode : yardiChargeCodes) {
                builder.append(chargeCode.getStringView());
                if (current != last) {
                    builder.append(", ");
                }
                current += 1;
            }
            return builder.toString();
        }
    }

}
