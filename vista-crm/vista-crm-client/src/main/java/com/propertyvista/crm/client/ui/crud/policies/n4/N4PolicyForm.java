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

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CPhoneField;
import com.pyx4j.forms.client.ui.CPhoneField.PhoneType;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog.Formatter;

import com.propertyvista.common.client.ui.components.editors.InternationalAddressEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.YardiChargeCode;
import com.propertyvista.domain.policy.dto.N4PolicyDTO;
import com.propertyvista.domain.policy.dto.N4PolicyDTOARCodeHolderDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class N4PolicyForm extends PolicyDTOTabPanelBasedForm<N4PolicyDTO> {

    public static final I18n i18n = I18n.get(N4PolicyForm.class);

    private ARCodeFolder arCodeFolder;

    public N4PolicyForm(IPrimeFormView<N4PolicyDTO, ?> view) {
        super(N4PolicyDTO.class, view);

        FormPanel signatureFormPanel = new FormPanel(this);
        FormPanel companyNameAndPhonesFormPanel = new FormPanel(this);

        signatureFormPanel.append(Location.Left, proto().includeSignature()).decorate();
        signatureFormPanel.append(Location.Left, proto().agentSelectionMethod()).decorate();
        signatureFormPanel.h1(i18n.tr("The following information will be used for signing N4 letters:"));

        companyNameAndPhonesFormPanel.append(Location.Left, proto().companyName()).decorate();
        companyNameAndPhonesFormPanel.append(Location.Right, proto().emailAddress()).decorate();
        companyNameAndPhonesFormPanel.append(Location.Left, proto().phoneNumber(), new CPhoneField(PhoneType.northAmerica)).decorate();
        companyNameAndPhonesFormPanel.append(Location.Right, proto().faxNumber(), new CPhoneField(PhoneType.northAmerica)).decorate();

        signatureFormPanel.append(Location.Dual, companyNameAndPhonesFormPanel);

        signatureFormPanel.append(Location.Dual, proto().mailingAddress(), new InternationalAddressEditor());

        FormPanel arCodesFormPanel = new FormPanel(this);
        arCodesFormPanel.h1(i18n.tr("Use the following AR Codes for calculation of charged vs. owed rent amount:"));
        arCodesFormPanel.append(Location.Dual, proto().arCodes(), arCodeFolder = new ARCodeFolder());

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
            N4PolicyDTOARCodeHolderForm form = new N4PolicyDTOARCodeHolderForm();
            form.inheritViewable(false);
            form.setViewable(true);
            return form;
        }

        @Override
        protected void addItem() {
            new EntitySelectorListDialog<ARCode>(i18n.tr("Select AR Code(s)"), true, getCurrentArCodeOptions(), new Formatter<ARCode>() {
                @Override
                public String format(ARCode o) {
                    String result = "";
                    if (o != null) {
                        result = o.getStringView();
                        if (VistaFeatures.instance().yardiIntegration() && !o.yardiChargeCodes().isEmpty()) {
                            result += " " + i18n.tr("(Includes Yardi charge codes: {0})", yardiChargeCodesLabel(o.yardiChargeCodes()));
                        }
                    }
                    return result;
                }
            }) {
                @Override
                public boolean onClickOk() {
                    for (ARCode item : getSelectedItems()) {
                        N4PolicyDTOARCodeHolderDTO newItem = EntityFactory.create(N4PolicyDTOARCodeHolderDTO.class);
                        newItem.arCode().set(item);
                        addItem(newItem);
                    }
                    return true;
                }
            }.show();
        }

        private List<ARCode> getCurrentArCodeOptions() {
            List<ARCode> options = new LinkedList<ARCode>(arCodeOptions);
            for (N4PolicyDTOARCodeHolderDTO item : getValue()) {
                options.remove(item.arCode());
            }
            return options;
        }
    }

    private static String yardiChargeCodesLabel(List<YardiChargeCode> yardiChargeCodes) {
        StringBuilder builder = new StringBuilder();

        for (YardiChargeCode chargeCode : yardiChargeCodes) {
            builder.append(chargeCode.getStringView());
            builder.append(", ");
        }

        builder.setLength(builder.length() - 2);
        return builder.toString();
    }

    public static class N4PolicyDTOARCodeHolderForm extends CForm<N4PolicyDTOARCodeHolderDTO> {

        public N4PolicyDTOARCodeHolderForm() {
            super(N4PolicyDTOARCodeHolderDTO.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel panel = new FormPanel(this);

            panel.append(Location.Dual, proto().arCode(), new CEntityLabel<ARCode>()).decorate().labelWidth(100);

            return panel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            if (VistaFeatures.instance().yardiIntegration()) {
                if (!getValue().arCode().yardiChargeCodes().isEmpty()) {
                    get(proto().arCode()).setNote(
                            i18n.tr("Includes the following Yardi charge codes: {0}", yardiChargeCodesLabel(getValue().arCode().yardiChargeCodes())));
                } else {
                    get(proto().arCode()).setNote(null);
                }
            }
        }
    }
}
