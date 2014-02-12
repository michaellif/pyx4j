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
package com.propertyvista.portal.shared.ui.util.editors;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class EmergencyContactFolder extends PortalBoxFolder<EmergencyContact> {

    private static final I18n i18n = I18n.get(EmergencyContactFolder.class);

    public EmergencyContactFolder() {
        super(EmergencyContact.class, i18n.tr("Emergency Contact"));
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof EmergencyContact) {
            return new EmergencyContactEditor();
        } else {
            return super.create(member);
        }
    }

    @Override
    protected void removeItem(final CEntityFolderItem<EmergencyContact> item) {
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

    class EmergencyContactEditor extends CEntityForm<EmergencyContact> {

        public EmergencyContactEditor() {
            super(EmergencyContact.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel main = new BasicFlexFormPanel();
            int row = -1;

            main.setWidget(++row, 0, 1, inject(proto().name(), new NameEditor(i18n.tr("Person"))));
            main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().sex()), 85).build());
            main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().birthDate()), 120).build());

            main.setH3(++row, 0, 1, i18n.tr("Contact Info"));
            main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().relationship())).build());
            main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().homePhone()), 180).build());
            main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().mobilePhone()), 180).build());
            main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().workPhone()), 180).build());
            main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().email()), 250).build());

            main.setHR(++row, 0, 1);
            main.setWidget(++row, 0, 1, inject(proto().address(), new AddressSimpleEditor()));

            return main;
        }

        @Override
        public void generateMockData() {
            get(proto().relationship()).setMockValue(PersonRelationship.Spouse);
        }

        @Override
        public void addValidations() {
            super.addValidations();

            get(proto().birthDate()).addValueValidator(new BirthdayDateValidator());
        }
    }
}
