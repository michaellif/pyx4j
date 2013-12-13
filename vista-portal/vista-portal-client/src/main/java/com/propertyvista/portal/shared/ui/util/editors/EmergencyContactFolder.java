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

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

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
}
