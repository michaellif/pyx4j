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
package com.propertyvista.common.client.ui.editors;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderItem;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.AddressUtils;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.domain.EmergencyContact;

public class EmergencyContactFolder extends VistaBoxFolder<EmergencyContact> {
    public EmergencyContactFolder() {
        super(EmergencyContact.class);
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member instanceof EmergencyContact) {
            return new EmergencyContactEditor();
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
        if (value.isEmpty()) {
            addItem(); // at least one Emergency Contact should be present!..
        }
    }

    static class EmergencyContactEditor extends CEntityEditor<EmergencyContact> {

        public EmergencyContactEditor() {
            super(EmergencyContact.class);
        }

        @Override
        public IsWidget createContent() {
            VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
            main.add(inject(proto().name().firstName()), 12);
            main.add(inject(proto().name().middleName()), 12);
            main.add(inject(proto().name().lastName()), 20);
            main.add(inject(proto().homePhone()), 15);
            main.add(inject(proto().mobilePhone()), 15);
            main.add(inject(proto().workPhone()), 15);
            AddressUtils.injectIAddress(main, proto().address(), this);
            main.add(new HTML());
            return main;
        }
    }
}