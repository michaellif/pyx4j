/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 23, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.maintenancerequest;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.FluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.components.boxes.LocalizedContentFolderBase;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.maintenance.PermissionToEnterNote;
import com.propertyvista.domain.policy.dto.MaintenanceRequestPolicyDTO;
import com.propertyvista.domain.site.AvailableLocale;

public class MaintenanceRequestPolicyForm extends PolicyDTOTabPanelBasedForm<MaintenanceRequestPolicyDTO> {

    private final static I18n i18n = I18n.get(MaintenanceRequestPolicyForm.class);

    public MaintenanceRequestPolicyForm(IForm<MaintenanceRequestPolicyDTO> view) {
        super(MaintenanceRequestPolicyDTO.class, view);
        addTab(createGeneralPanel(), i18n.tr("General"));
    }

    private IsWidget createGeneralPanel() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(proto().permissionToEnterNote().getMeta().getCaption());
        formPanel.append(Location.Left, proto().permissionToEnterNote(), new PermissionToEnterNoteFolder(isEditable()));

        return formPanel;
    }

    class PermissionToEnterNoteFolder extends LocalizedContentFolderBase<PermissionToEnterNote> {

        public PermissionToEnterNoteFolder(boolean editable) {
            super(PermissionToEnterNote.class, editable);
        }

        @Override
        public IsWidget createEditorContent(CForm<PermissionToEnterNote> editor) {
            FormPanel formPanel = new FormPanel(editor);

            formPanel.append(Location.Left, proto().locale(), new CEntityLabel<AvailableLocale>()).decorate().componentWidth(120);
            formPanel.append(Location.Left, proto().text()).decorate();

            return formPanel;
        }
    }
}
