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
package com.propertyvista.portal.web.client.ui.profile;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.BoxFolderDecorator;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolder;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.domain.tenant.EmergencyContact;

public class EmergencyContactFolder extends CEntityFolder<EmergencyContact> {

    private static final I18n i18n = I18n.get(EmergencyContactFolder.class);

    private final ProfilePageViewImpl view;

    public EmergencyContactFolder(ProfilePageViewImpl view) {
        super(EmergencyContact.class);
        this.view = view;
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
            return new EmergencyContactForm(view);
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
}
