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

import java.util.List;

import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CPhoneField;
import com.pyx4j.forms.client.ui.CPhoneField.PhoneType;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.components.editors.InternationalAddressEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.YardiChargeCode;
import com.propertyvista.domain.policy.dto.N4PolicyDTO;
import com.propertyvista.domain.policy.dto.N4PolicyDTOARCodeHolderDTO;

public class N4PolicyForm extends PolicyDTOTabPanelBasedForm<N4PolicyDTO> {

    public static final I18n i18n = I18n.get(N4PolicyForm.class);

    private ARCodeFolder arCodeFolder;

    public N4PolicyForm(IForm<N4PolicyDTO> view) {
        super(N4PolicyDTO.class, view);

        FormPanel signatureFormPanel = new FormPanel(this);
        FormPanel companyNameAndPhonesFormPanel = new FormPanel(this);

        signatureFormPanel.append(Location.Left, proto().includeSignature()).decorate();
        signatureFormPanel.h1(i18n.tr("The following information will be used for signing N4 letters:"));

        companyNameAndPhonesFormPanel.append(Location.Left, proto().companyName()).decorate();
        companyNameAndPhonesFormPanel.append(Location.Right, proto().emailAddress()).decorate();
        companyNameAndPhonesFormPanel.append(Location.Left, proto().phoneNumber(), new CPhoneField(PhoneType.northAmerica)).decorate();
        companyNameAndPhonesFormPanel.append(Location.Right, proto().faxNumber(), new CPhoneField(PhoneType.northAmerica)).decorate();

        signatureFormPanel.append(Location.Dual, companyNameAndPhonesFormPanel);

        signatureFormPanel.append(Location.Dual, proto().mailingAddress(), new InternationalAddressEditor());

        FormPanel arCodesFormPanel = new FormPanel(this);
        arCodesFormPanel.h1(i18n.tr("Use the following AR Codes for calculation of charged vs. owed rent amount:"));
        arCodesFormPanel.append(Location.Left, proto().arCodes(), arCodeFolder = new ARCodeFolder());

        FormPanel deliveryFormPanel = new FormPanel(this);
        deliveryFormPanel.h1(i18n.tr("Termination date calculation:"));
        deliveryFormPanel.append(Location.Left, proto().terminationDateAdvanceDaysLongRentPeriod()).decorate();
        deliveryFormPanel.append(Location.Left, proto().terminationDateAdvanceDaysShortRentPeriod()).decorate();

        deliveryFormPanel.h1(i18n.tr("Additional advance days based on delivery method:"));
        deliveryFormPanel.append(Location.Left, proto().handDeliveryAdvanceDays()).decorate();
        deliveryFormPanel.append(Location.Left, proto().mailDeliveryAdvanceDays()).decorate();
        deliveryFormPanel.append(Location.Left, proto().courierDeliveryAdvanceDays()).decorate();

        addTab(signatureFormPanel, i18n.tr("Signature"));
        addTab(arCodesFormPanel, i18n.tr("AR Codes"));
        addTab(deliveryFormPanel, i18n.tr("Delivery"));
        addTab(createAutoCancellationPanel(), i18n.tr("Auto Cancellation"));
    }

    public void setARCodeOptions(List<ARCode> arCodeOptions) {
        arCodeFolder.setARCodeOptions(arCodeOptions);
    }

    private IsWidget createAutoCancellationPanel() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().cancellationThreshold()).decorate();
        formPanel.append(Location.Left, proto().expiryDays()).decorate();

        return formPanel;
    }

    public static class ARCodeFolder extends VistaBoxFolder<N4PolicyDTOARCodeHolderDTO> {

        private List<ARCode> arCodeOptions;

        public ARCodeFolder() {
            super(N4PolicyDTOARCodeHolderDTO.class);
            setOrderable(false);
            setAddable(true);
        }

        public void setARCodeOptions(List<ARCode> arCodeOptions) {
            this.arCodeOptions = arCodeOptions;
        }

        @Override
        protected CForm<N4PolicyDTOARCodeHolderDTO> createItemForm(IObject<?> member) {
            N4PolicyDTOARCodeHolderForm form = new N4PolicyDTOARCodeHolderForm(null, false);
            form.inheritViewable(false);
            form.setViewable(true);
            return form;
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
            form.init();
            form.populateNew();
            setBody(form);
        }

        @Override
        public boolean onClickOk() {
            form.setVisitedRecursive();
            return form.isValid();
        }

        public final N4PolicyDTOARCodeHolderDTO getValue() {
            return form.getValue();
        }
    }

    public static class N4PolicyDTOARCodeHolderForm extends CForm<N4PolicyDTOARCodeHolderDTO> {

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
        protected IsWidget createContent() {
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
