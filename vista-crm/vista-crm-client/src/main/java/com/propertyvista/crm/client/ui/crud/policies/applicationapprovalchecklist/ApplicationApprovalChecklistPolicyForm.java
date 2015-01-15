/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 14, 2015
 * @author VladL
 */
package com.propertyvista.crm.client.ui.crud.policies.applicationapprovalchecklist;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.ApplicationApprovalChecklistPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.ApplicationApprovalChecklistPolicyItem;
import com.propertyvista.domain.policy.policies.domain.ApplicationApprovalChecklistPolicyItem.StatusSelectionItem;

public class ApplicationApprovalChecklistPolicyForm extends PolicyDTOTabPanelBasedForm<ApplicationApprovalChecklistPolicyDTO> {

    private static final I18n i18n = I18n.get(ApplicationApprovalChecklistPolicyForm.class);

    public ApplicationApprovalChecklistPolicyForm(IPrimeFormView<ApplicationApprovalChecklistPolicyDTO, ?> view) {
        super(ApplicationApprovalChecklistPolicyDTO.class, view);

        addTab(createGeneralTab(), i18n.tr("Checklist Items"));
    }

    private IsWidget createGeneralTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().itemsToCheck(), new ApplicationApprovalChecklistItemFolder(isEditable()));

        return formPanel;
    }

    private class ApplicationApprovalChecklistItemFolder extends VistaBoxFolder<ApplicationApprovalChecklistPolicyItem> {

        public ApplicationApprovalChecklistItemFolder(boolean editable) {
            super(ApplicationApprovalChecklistPolicyItem.class, editable);
        }

        @Override
        protected CForm<? extends ApplicationApprovalChecklistPolicyItem> createItemForm(IObject<?> member) {
            return new CForm<ApplicationApprovalChecklistPolicyItem>(ApplicationApprovalChecklistPolicyItem.class) {

                @Override
                protected IsWidget createContent() {
                    FormPanel formPanel = new FormPanel(this);

                    formPanel.append(Location.Left, proto().itemToCheck()).decorate();
                    formPanel.append(Location.Right, proto().statusesToSelect(), new StatusSelectionItemFolder(isEditable()));

                    return formPanel;
                }

                class StatusSelectionItemFolder extends VistaTableFolder<StatusSelectionItem> {

                    public StatusSelectionItemFolder(boolean editable) {
                        super(StatusSelectionItem.class, editable);
                    }

                    @Override
                    public List<FolderColumnDescriptor> columns() {
                        return Arrays.asList(//@formatter:off
                                new FolderColumnDescriptor(proto().statusSelection(), "300px")
                        );//@formatter:on
                    }
                }
            };
        }
    }
}
