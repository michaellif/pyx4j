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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.idassignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.crm.client.ui.crud.settings.financial.tax.TaxFolder;
import com.propertyvista.domain.policy.dto.IdAssignmentPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdAssignmentType;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;

public class IdAssignmentPolicyForm extends PolicyDTOTabPanelBasedForm<IdAssignmentPolicyDTO> {

    private final static I18n i18n = I18n.get(IdAssignmentPolicyForm.class);

    public IdAssignmentPolicyForm(IForm<IdAssignmentPolicyDTO> view) {
        super(IdAssignmentPolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        return Arrays.asList(createItemsPanel());
    }

    private TwoColumnFlexFormPanel createItemsPanel() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Items"));
        int row = -1;

        panel.setWidget(++row, 0, inject(proto().editableItems(), new IdAssignmentItemFolder(isEditable())));

        return panel;
    }

    private static class IdAssignmentItemFolder extends VistaTableFolder<IdAssignmentItem> {

        private static final I18n i18n = I18n.get(TaxFolder.class);

        public IdAssignmentItemFolder(boolean modifyable) {
            super(IdAssignmentItem.class, modifyable);
            setAddable(false);
            setRemovable(false);
            setOrderable(false);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            List<EntityFolderColumnDescriptor> columns;
            columns = new ArrayList<EntityFolderColumnDescriptor>();
            columns.add(new EntityFolderColumnDescriptor(proto().target(), "20em"));
            columns.add(new EntityFolderColumnDescriptor(proto().type(), "20em"));

            return columns;
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof IdAssignmentItem) {
                return new IdAssignmentItemEditor();
            }
            return super.create(member);
        }

        private class IdAssignmentItemEditor extends CEntityFolderRowEditor<IdAssignmentItem> {

            public IdAssignmentItemEditor() {
                super(IdAssignmentItem.class, columns());
            }

            @Override
            protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                if (column.getObject() == proto().target()) {
                    return inject(column.getObject(), new CEnumLabel());
                }

                return super.createCell(column);
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                if (getValue().target().getValue() == IdTarget.application) {
                    CComboBox<IdAssignmentType> combo = (CComboBox<IdAssignmentType>) get(proto().type());
                    combo.getOptions().clear();
                    combo.setOptions(Arrays.asList(IdAssignmentType.generatedNumber, IdAssignmentType.generatedAlphaNumeric));
                }
            }
        }

    }
}
