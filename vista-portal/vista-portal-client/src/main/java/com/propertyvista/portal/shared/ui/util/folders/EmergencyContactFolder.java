/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui.util.folders;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.editors.InternationalAddressEditor;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.editors.NameEditor;

public class EmergencyContactFolder extends PortalBoxFolder<EmergencyContact> {

    private static final I18n i18n = I18n.get(EmergencyContactFolder.class);

    public EmergencyContactFolder() {
        this(true);

        setNoDataLabel(i18n.tr("Please enter emergency contact information if present"));
    }

    public EmergencyContactFolder(boolean modifiable) {
        super(EmergencyContact.class, i18n.tr("Emergency Contact"), modifiable);
    }

    @Override
    protected CForm<EmergencyContact> createItemForm(IObject<?> member) {
        return new EmergencyContactEditor();
    }

    @Override
    protected void removeItem(final CFolderItem<EmergencyContact> item) {
        MessageDialog.confirm(i18n.tr("Emergency contact removal"), i18n.tr("Do you really want to remove emergency contact information?"), new Command() {
            @Override
            public void execute() {
                EmergencyContactFolder.super.removeItem(item);
            }
        });
    }

    @Override
    public void generateMockData() {

        if (getItemCount() == 0) {
            EmergencyContact contact = EntityFactory.create(EmergencyContact.class);
            addItem(contact);
        }
    }

    class EmergencyContactEditor extends CForm<EmergencyContact> {

        public EmergencyContactEditor() {
            super(EmergencyContact.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Left, proto().name(), new NameEditor(i18n.tr("Full Name")));
            formPanel.append(Location.Left, proto().sex()).decorate().componentWidth(85);
            formPanel.append(Location.Left, proto().birthDate()).decorate().componentWidth(120);

            formPanel.h3(i18n.tr("Contact Info"));
            formPanel.append(Location.Left, proto().relationship()).decorate();
            formPanel.append(Location.Left, proto().homePhone()).decorate().componentWidth(180);
            formPanel.append(Location.Left, proto().mobilePhone()).decorate().componentWidth(180);
            formPanel.append(Location.Left, proto().workPhone()).decorate().componentWidth(180);
            formPanel.append(Location.Left, proto().email()).decorate().componentWidth(250);

            formPanel.h3(i18n.tr("Address"));
            formPanel.append(Location.Left, inject(proto().address(), new InternationalAddressEditor()));

            return formPanel;
        }

        @Override
        public void generateMockData() {
            get(proto().relationship()).setMockValue(PersonRelationship.Spouse);
        }

        @Override
        public void addValidations() {
            super.addValidations();

            get(proto().birthDate()).addComponentValidator(new BirthdayDateValidator());
        }
    }
}
