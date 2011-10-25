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
package com.propertyvista.common.client.ui.components.folders;

import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderItem;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.editors.CEmergencyContact;
import com.propertyvista.domain.EmergencyContact;

public class EmergencyContactFolder extends VistaBoxFolder<EmergencyContact> {

    private final boolean modifyable;

    public EmergencyContactFolder(boolean modifyable) {
        super(EmergencyContact.class, modifyable);
        this.modifyable = modifyable;
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member instanceof EmergencyContact) {
            return new CEmergencyContact();
        } else {
            return super.create(member);
        }
    }

    @Override
    protected CEntityFolderItem<EmergencyContact> createItem(final boolean first) {
        CEntityFolderItem<EmergencyContact> item = super.createItem(first);
        item.setMovable(!first);
        item.setRemovable(!first);
        return item;
    }

    @Override
    public void populate(IList<EmergencyContact> value) {
        super.populate(value);
        if (modifyable && value.isEmpty()) {
            addItem(); // at least one Emergency Contact should be present!..
        }
    }
}