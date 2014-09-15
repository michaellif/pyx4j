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
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.backoffice.prime.form.IForm;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.IdAssignmentPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdAssignmentType;

public class IdAssignmentPolicyForm extends PolicyDTOTabPanelBasedForm<IdAssignmentPolicyDTO> {

    private final static I18n i18n = I18n.get(IdAssignmentPolicyForm.class);

    public IdAssignmentPolicyForm(IForm<IdAssignmentPolicyDTO> view) {
        super(IdAssignmentPolicyDTO.class, view);
        addTab(createItemsPanel(), i18n.tr("Items"));
    }

    private IsWidget createItemsPanel() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().editableItems(), new IdAssignmentItemFolder(isEditable()));

        return formPanel;
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
