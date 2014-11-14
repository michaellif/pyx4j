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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.components.boxes.LocalizedContentFolderBase;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.TimeWindow;
import com.propertyvista.domain.maintenance.MaintenanceRequestWindow;
import com.propertyvista.domain.maintenance.PermissionToEnterNote;
import com.propertyvista.domain.policy.dto.MaintenanceRequestPolicyDTO;
import com.propertyvista.domain.site.AvailableLocale;

public class MaintenanceRequestPolicyForm extends PolicyDTOTabPanelBasedForm<MaintenanceRequestPolicyDTO> {

    private final static I18n i18n = I18n.get(MaintenanceRequestPolicyForm.class);

    public MaintenanceRequestPolicyForm(IPrimeFormView<MaintenanceRequestPolicyDTO, ?> view) {
        super(MaintenanceRequestPolicyDTO.class, view);
        addTab(createGeneralPanel(), i18n.tr("General"));
    }

    private IsWidget createGeneralPanel() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(proto().permissionToEnterNote().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().permissionToEnterNote(), new PermissionToEnterNoteFolder(isEditable()));

        formPanel.h1(i18n.tr("Tenant Preferred Time Windows"));
        formPanel.append(Location.Dual, proto().tenantPreferredWindows(), new PreferredWindowsFolder(isEditable()));

        formPanel.h1(i18n.tr("Scheduling"));
        formPanel.append(Location.Dual, proto().maxAllowedWindowHours()).decorate().componentWidth(40).labelWidth(200);
        formPanel.append(Location.Dual, proto().allow24HourSchedule()).decorate().labelWidth(200);
        formPanel.append(Location.Dual, proto().schedulingWindow(), new TimeWindowEditor<TimeWindow>(TimeWindow.class));

        get(proto().allow24HourSchedule()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().schedulingWindow()).setVisible(!event.getValue());
            }
        });
        return formPanel;
    }

    @Override
    public void onValueSet(boolean populate) {
        super.onValueSet(populate);

        MaintenanceRequestPolicyDTO value = getValue();

        if (value != null) {
            get(proto().schedulingWindow()).setVisible(!value.allow24HourSchedule().getValue(false));
        }
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

    class PreferredWindowsFolder extends VistaBoxFolder<MaintenanceRequestWindow> {

        public PreferredWindowsFolder(boolean modifiable) {
            super(MaintenanceRequestWindow.class, modifiable);
        }

        @Override
        protected CForm<? extends MaintenanceRequestWindow> createItemForm(IObject<?> member) {
            return new TimeWindowEditor<MaintenanceRequestWindow>(MaintenanceRequestWindow.class);
        }

    }

    class TimeWindowEditor<E extends TimeWindow> extends CForm<E> {

        public TimeWindowEditor(Class<E> entityClass) {
            super(entityClass);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel content = new FormPanel(this);

            content.append(Location.Left, proto().timeFrom()).decorate().labelWidth(40);
            content.append(Location.Right, proto().timeTo()).decorate().labelWidth(40);

            return content;
        }

    }
}
