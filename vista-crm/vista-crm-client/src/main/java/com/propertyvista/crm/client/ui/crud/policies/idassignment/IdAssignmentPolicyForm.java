/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.crud.policies.idassignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CTextComponent;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.IdAssignmentPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdAssignmentType;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentPaymentType;
import com.propertyvista.shared.config.VistaFeatures;

public class IdAssignmentPolicyForm extends PolicyDTOTabPanelBasedForm<IdAssignmentPolicyDTO> {

    private final static I18n i18n = I18n.get(IdAssignmentPolicyForm.class);

    public IdAssignmentPolicyForm(IPrimeFormView<IdAssignmentPolicyDTO, ?> view) {
        super(IdAssignmentPolicyDTO.class, view);
        addTab(createItemsPanel(), i18n.tr("Items"));
        if (VistaFeatures.instance().yardiIntegration()) {
            addTab(createPaymentTypesPanel(), i18n.tr("Payment Types"));
        }
    }

    private IsWidget createItemsPanel() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().editableItems(), new IdAssignmentItemFolder(isEditable()));

        return formPanel;
    }

    private IsWidget createPaymentTypesPanel() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().paymentTypes().cashPrefix()).decorate().labelWidth(200);
        formPanel.append(Location.Left, proto().paymentTypes().checkPrefix()).decorate().labelWidth(200);
        formPanel.append(Location.Left, proto().paymentTypes().echeckPrefix()).decorate().labelWidth(200);
        formPanel.append(Location.Left, proto().paymentTypes().directBankingPrefix()).decorate().labelWidth(200);
        formPanel.append(Location.Left, proto().paymentTypes().creditCardVisaPrefix()).decorate().labelWidth(200);
        formPanel.append(Location.Left, proto().paymentTypes().creditCardMasterCardPrefix()).decorate().labelWidth(200);
        formPanel.append(Location.Left, proto().paymentTypes().visaDebitPrefix()).decorate().labelWidth(200);
        formPanel.append(Location.Left, proto().paymentTypes().autopayPrefix()).decorate().labelWidth(200);
        formPanel.append(Location.Left, proto().paymentTypes().oneTimePrefix()).decorate().labelWidth(200);

        return formPanel;
    }

    class YardiDocumentNumberLenghtValidator extends AbstractComponentValidator<String> {

        private final IPrimitive<String> protoMember;

        public YardiDocumentNumberLenghtValidator(IPrimitive<String> protoMember) {
            this.protoMember = protoMember;
        }

        @Override
        public BasicValidationError isValid() {
            String value1 = getCComponent().getValue();
            if (value1 == null) {
                value1 = ((CTextComponent<?, ?>) getCComponent()).getWatermark();
            }
            if (value1 == null) {
                value1 = "";
            }

            String value2 = get(protoMember).getValue();
            if (value2 == null) {
                value2 = ((CTextComponent<?, ?>) get(protoMember)).getWatermark();
            }
            if (value2 == null) {
                value2 = "";
            }

            if (value1.length() + value2.length() > getValue().yardiDocumentNumberLenght().getValue()) {
                String message = i18n.tr("Yardi Check # \"{0}{1}:123456\" exceeds maximum Yardy field lenght {2}", value1, value2, getValue()
                        .yardiDocumentNumberLenght().getValue() + 7);
                return new BasicValidationError(getCComponent(), message);
            } else {
                return null;
            }
        }
    };

    @Override
    public void addValidations() {

        if (VistaFeatures.instance().yardiIntegration()) {
            for (IObject<String> member : Arrays.asList(proto().paymentTypes().cashPrefix(),//
                    proto().paymentTypes().checkPrefix(), //
                    proto().paymentTypes().echeckPrefix(), //
                    proto().paymentTypes().directBankingPrefix(), //
                    proto().paymentTypes().creditCardVisaPrefix(), //
                    proto().paymentTypes().creditCardMasterCardPrefix(), //
                    proto().paymentTypes().visaDebitPrefix())) {

                get(member).addComponentValidator(new YardiDocumentNumberLenghtValidator(proto().paymentTypes().autopayPrefix()));
                get(member).addComponentValidator(new YardiDocumentNumberLenghtValidator(proto().paymentTypes().oneTimePrefix()));

                get(member).addValueChangeHandler(new RevalidationTrigger<String>(get(proto().paymentTypes().autopayPrefix())));
                get(member).addValueChangeHandler(new RevalidationTrigger<String>(get(proto().paymentTypes().oneTimePrefix())));
                get(proto().paymentTypes().autopayPrefix()).addValueChangeHandler(new RevalidationTrigger<String>(get(member)));
                get(proto().paymentTypes().oneTimePrefix()).addValueChangeHandler(new RevalidationTrigger<String>(get(member)));

            }
        }

    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (VistaFeatures.instance().yardiIntegration()) {
            IdAssignmentPaymentType defaults = getValue().paymentTypesDefaults();

            ((CTextComponent<?, ?>) get(proto().paymentTypes().cashPrefix())).setWatermark(defaults.cashPrefix().getValue());
            ((CTextComponent<?, ?>) get(proto().paymentTypes().checkPrefix())).setWatermark(defaults.checkPrefix().getValue());
            ((CTextComponent<?, ?>) get(proto().paymentTypes().echeckPrefix())).setWatermark(defaults.echeckPrefix().getValue());
            ((CTextComponent<?, ?>) get(proto().paymentTypes().directBankingPrefix())).setWatermark(defaults.directBankingPrefix().getValue());
            ((CTextComponent<?, ?>) get(proto().paymentTypes().creditCardVisaPrefix())).setWatermark(defaults.creditCardVisaPrefix().getValue());
            ((CTextComponent<?, ?>) get(proto().paymentTypes().creditCardMasterCardPrefix())).setWatermark(defaults.creditCardMasterCardPrefix().getValue());
            ((CTextComponent<?, ?>) get(proto().paymentTypes().visaDebitPrefix())).setWatermark(defaults.visaDebitPrefix().getValue());
            ((CTextComponent<?, ?>) get(proto().paymentTypes().autopayPrefix())).setWatermark(defaults.autopayPrefix().getValue());
            ((CTextComponent<?, ?>) get(proto().paymentTypes().oneTimePrefix())).setWatermark(defaults.oneTimePrefix().getValue());
        }
    }

    private static class IdAssignmentItemFolder extends VistaTableFolder<IdAssignmentItem> {

        public IdAssignmentItemFolder(boolean modifyable) {
            super(IdAssignmentItem.class, modifyable);
            setAddable(false);
            setRemovable(false);
            setOrderable(false);
        }

        @Override
        public List<FolderColumnDescriptor> columns() {
            List<FolderColumnDescriptor> columns;
            columns = new ArrayList<FolderColumnDescriptor>();
            columns.add(new FolderColumnDescriptor(proto().target(), "20em"));
            columns.add(new FolderColumnDescriptor(proto().type(), "20em"));

            return columns;
        }

        @Override
        protected CForm<IdAssignmentItem> createItemForm(IObject<?> member) {
            return new IdAssignmentItemEditor();
        }

        private class IdAssignmentItemEditor extends CFolderRowEditor<IdAssignmentItem> {

            public IdAssignmentItemEditor() {
                super(IdAssignmentItem.class, columns());
            }

            @Override
            protected CField<?, ?> createCell(FolderColumnDescriptor column) {
                if (column.getObject() == proto().target()) {
                    return inject(column.getObject(), new CEnumLabel());
                }

                return super.createCell(column);
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                if (isEditable()) {
                    // set predefined values for some ID types and do not allow editing:
                    CComboBox<IdAssignmentType> combo = (CComboBox<IdAssignmentType>) get(proto().type());
                    combo.getOptions().clear();
                    combo.setOptions(IdAssignmentType.selectableInPolicy());
                    switch (getValue().target().getValue()) {
                    case customer:
                    case maintenance:
                        combo.setEditable(false);
                        break;

                    default:
                        combo.setEditable(true);
                    }
                }
            }
        }

    }
}
