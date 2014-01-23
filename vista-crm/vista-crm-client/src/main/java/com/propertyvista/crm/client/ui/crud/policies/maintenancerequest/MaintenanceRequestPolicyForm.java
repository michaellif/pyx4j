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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.components.boxes.LocalizedContentFolderBase;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.maintenance.PermissionToEnterNote;
import com.propertyvista.domain.policy.dto.MaintenanceRequestPolicyDTO;
import com.propertyvista.domain.site.AvailableLocale;

public class MaintenanceRequestPolicyForm extends PolicyDTOTabPanelBasedForm<MaintenanceRequestPolicyDTO> {

    private final static I18n i18n = I18n.get(MaintenanceRequestPolicyForm.class);

    public MaintenanceRequestPolicyForm(IForm<MaintenanceRequestPolicyDTO> view) {
        super(MaintenanceRequestPolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        return Arrays.asList(createGeneralPanel());
    }

    private TwoColumnFlexFormPanel createGeneralPanel() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("General"));
        int row = -1;

        panel.setH1(++row, 0, 2, proto().permissionToEnterNote().getMeta().getCaption());
        panel.setWidget(++row, 0, 2, inject(proto().permissionToEnterNote(), new PermissionToEnterNoteFolder(isEditable())));

        return panel;
    }

    class PermissionToEnterNoteFolder extends LocalizedContentFolderBase<PermissionToEnterNote> {

        public PermissionToEnterNoteFolder(boolean editable) {
            super(PermissionToEnterNote.class, editable);
        }

        @Override
        public IsWidget createEditorContent(CEntityForm<PermissionToEnterNote> editor) {
            TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("General"));
            int row = -1;

            panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(editor.inject(proto().locale(), new CEntityLabel<AvailableLocale>()), 10, true).build());
            panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(editor.inject(proto().text()), 50, true).build());

            return panel;
        }
    }
}
