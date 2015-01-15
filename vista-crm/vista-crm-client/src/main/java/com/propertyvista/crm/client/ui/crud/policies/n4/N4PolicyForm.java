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
 */
package com.propertyvista.crm.client.ui.crud.policies.n4;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.forms.client.events.OptionsChangeEvent;
import com.pyx4j.forms.client.events.OptionsChangeHandler;
import com.pyx4j.forms.client.ui.CEntityComboBox;
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
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.editors.InternationalAddressEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.YardiChargeCode;
import com.propertyvista.domain.policy.dto.N4PolicyDTO;
import com.propertyvista.domain.policy.dto.N4PolicyDTOARCodeHolderDTO;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep;
import com.propertyvista.shared.config.VistaFeatures;

public class N4PolicyForm extends PolicyDTOTabPanelBasedForm<N4PolicyDTO> {

    public static final I18n i18n = I18n.get(N4PolicyForm.class);

    private final EvictionStepSelector stepSelector = new EvictionStepSelector();

    private FormPanel contactPanel;

    private ARCodeFolder arCodeFolder;

    public N4PolicyForm(IPrimeFormView<N4PolicyDTO, ?> view) {
        super(N4PolicyDTO.class, view);

        addTab(getEvictionFlowTab(), i18n.tr("Eviction Flow"));
        addTab(getSignatureTab(), i18n.tr("Signature"));
        addTab(getArCodesTab(), i18n.tr("AR Codes"));
        addTab(getDeliveryTab(), i18n.tr("Delivery"));
        addTab(getAutoCancellationTab(), i18n.tr("Auto Cancellation"));

        if (isEditable()) {
            get(proto().node()).addValueChangeHandler(new ValueChangeHandler<PolicyNode>() {

                @Override
                public void onValueChange(ValueChangeEvent<PolicyNode> event) {
                    stepSelector.setPolicyNode(event.getValue());
                }
            });
        }
    }

    @Override
    protected void onValuePropagation(N4PolicyDTO value, boolean fireEvent, boolean populate) {
        if (value != null && isEditable()) {
            stepSelector.setPolicyNode(value.node());
        }

        super.onValuePropagation(value, fireEvent, populate);
    }

    public void setARCodeOptions(List<ARCode> arCodeOptions) {
        arCodeFolder.setARCodeOptions(arCodeOptions);
    }

    private IsWidget getEvictionFlowTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().evictionStep(), stepSelector).decorate();
        return formPanel;
    }

    private IsWidget getSignatureTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Agent Info"));
        formPanel.append(Location.Left, proto().agentSelectionMethod()).decorate();
        formPanel.append(Location.Right, proto().includeSignature()).decorate();

        formPanel.h1(i18n.tr("Contact Info"));
        formPanel.append(Location.Left, proto().companyName()).decorate();
        formPanel.append(Location.Left, proto().useAgentContactInfo()).decorate();
        get(proto().useAgentContactInfo()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                contactPanel.setVisible(!event.getValue());
            }
        });

        contactPanel = new FormPanel(this);
        contactPanel.append(Location.Left, proto().emailAddress()).decorate();
        contactPanel.append(Location.Left, proto().phoneNumber(), new CPhoneField(PhoneType.northAmerica)).decorate();
        contactPanel.append(Location.Left, proto().faxNumber(), new CPhoneField(PhoneType.northAmerica)).decorate();
        formPanel.append(Location.Right, contactPanel);

        formPanel.h1(i18n.tr("Mailing Address"));
        formPanel.append(Location.Dual, proto().mailingAddress(), new InternationalAddressEditor());

        return formPanel;
    }

    private IsWidget getArCodesTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.h1(i18n.tr("Use the following AR Codes for calculation of charged vs. owed rent amount:"));
        formPanel.append(Location.Dual, proto().arCodes(), arCodeFolder = new ARCodeFolder());
        return formPanel;
    }

    private IsWidget getDeliveryTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Termination date calculation:"));
        formPanel.append(Location.Left, proto().terminationDateAdvanceDaysLongRentPeriod()).decorate();
        formPanel.append(Location.Left, proto().terminationDateAdvanceDaysShortRentPeriod()).decorate();

        formPanel.h1(i18n.tr("Additional advance days based on delivery method:"));
        formPanel.append(Location.Left, proto().handDeliveryAdvanceDays()).decorate();
        formPanel.append(Location.Left, proto().mailDeliveryAdvanceDays()).decorate();
        formPanel.append(Location.Left, proto().courierDeliveryAdvanceDays()).decorate();

        return formPanel;
    }

    private IsWidget getAutoCancellationTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().cancellationThreshold()).decorate();
        formPanel.append(Location.Left, proto().expiryDays()).decorate();

        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        contactPanel.setVisible(!getValue().useAgentContactInfo().getValue(false));
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

    static class EvictionStepSelector extends CEntityComboBox<EvictionFlowStep> {

        private PolicyNode node;

        private boolean noteSeen = false;

        public EvictionStepSelector() {
            super(EvictionFlowStep.class);

            addOptionsChangeHandler(new OptionsChangeHandler<List<EvictionFlowStep>>() {

                @Override
                public void onOptionsChange(OptionsChangeEvent<List<EvictionFlowStep>> event) {
                    validatePolicySetup();
                }
            });
        }

        void setPolicyNode(PolicyNode node) {
            this.node = node;
            this.noteSeen = false;
            if (node != null && isEditable()) {
                resetCriteria();
                addCriterion(PropertyCriterion.eq(proto().policy().node(), node));
                refreshOptions();
            }
        }

        @Override
        protected void onEditingStop() {
            super.onEditingStop();
            this.noteSeen = false;

            validatePolicySetup();
        }

        private void validatePolicySetup() {
            if (!noteSeen && node != null && isEditable() && (getOptions() == null || getOptions().isEmpty())) {
                noteSeen = true;
                MessageDialog.error(i18n.tr("Eviction Flow Undefined"),
                        i18n.tr("Eviction Steps or Eviction Flow Policy not found for {0}", node.getStringView()));
            }
        }
    }
}
