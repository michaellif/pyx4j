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
package com.propertyvista.portal.shared.ui;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderDecorator;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolder;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class EmergencyContactFolder extends CEntityFolder<EmergencyContact> {

    private static final I18n i18n = I18n.get(EmergencyContactFolder.class);

    public EmergencyContactFolder() {
        super(EmergencyContact.class);
        setOrderable(true);
        setRemovable(true);
        setAddable(true);
    }

    @Override
    public IFolderItemDecorator<EmergencyContact> createItemDecorator() {
        BoxFolderItemDecorator<EmergencyContact> decor = new BoxFolderItemDecorator<EmergencyContact>(VistaImages.INSTANCE);
        return decor;
    }

    @Override
    protected IFolderDecorator<EmergencyContact> createFolderDecorator() {
        return new BoxFolderDecorator<EmergencyContact>(VistaImages.INSTANCE, "Add Emergency Contact");
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof EmergencyContact) {
            return new EmergencyContactForm();
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

    class EmergencyContactForm extends CEntityForm<EmergencyContact> {

        public EmergencyContactForm() {
            super(EmergencyContact.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();

            int row = -1;
            mainPanel.setWidget(++row, 0,
                    new FormWidgetDecoratorBuilder(inject(proto().name(), new CEntityLabel<Name>()), 200).customLabel(i18n.tr("Full Name")).build());

            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().name().firstName()), 200).build());
            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().name().lastName()), 200).build());
            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().name().namePrefix()), 70).build());

            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().email()), 230).build());
            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().homePhone()), 200).build());
            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().mobilePhone()), 200).build());
            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().workPhone()), 200).build());

            mainPanel.setWidget(++row, 0, inject(proto().address(), new PortalAddressSimpleEditor()));

            calculateFieldsStatus();

            addPropertyChangeHandler(new PropertyChangeHandler() {

                @Override
                public void onPropertyChange(PropertyChangeEvent event) {
                    if (event.isEventOfType(PropertyName.viewable)) {
                        calculateFieldsStatus();
                    }
                }
            });

            return mainPanel;
        }

        private void calculateFieldsStatus() {
            if (isViewable()) {
                get(proto().name()).setVisible(true);
                get(proto().name().firstName()).setVisible(false);
                get(proto().name().lastName()).setVisible(false);
                get(proto().name().namePrefix()).setVisible(false);
            } else {
                get(proto().name()).setVisible(false);
                get(proto().name().firstName()).setVisible(true);
                get(proto().name().lastName()).setVisible(true);
                get(proto().name().namePrefix()).setVisible(true);
            }

        }

    }
}
