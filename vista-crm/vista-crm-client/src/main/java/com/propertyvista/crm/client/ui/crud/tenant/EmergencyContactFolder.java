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
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.AddressUtils;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.domain.EmergencyContact;

class EmergencyContactFolder extends VistaBoxFolder<EmergencyContact> {

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
    public void populate(IList<EmergencyContact> value) {
        super.populate(value);
        if (isEditable() && value.isEmpty()) {
            addItem(); // at least one Emergency Contact should be present!..
        }
    }

    class EmergencyContactEditor extends CEntityEditor<EmergencyContact> {

        protected I18n i18n = I18n.get(EmergencyContactEditor.class);

        public EmergencyContactEditor() {
            super(EmergencyContact.class);
        }

        @Override
        public IsWidget createContent() {
            VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());
            VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!isEditable());
            main.add(split);

            if (isEditable()) {
                split.getLeftPanel().add(inject(proto().name().namePrefix()), 6);
                split.getLeftPanel().add(inject(proto().name().firstName()), 12);
                split.getLeftPanel().add(inject(proto().name().middleName()), 12);
                split.getLeftPanel().add(inject(proto().name().lastName()), 20);
            } else {
                split.getLeftPanel().add(inject(proto().name(), new CEntityLabel()), 20, i18n.tr("Contactee"));
                get(proto().name()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
            }

            split.getRightPanel().add(inject(proto().homePhone()), 15);
            split.getRightPanel().add(inject(proto().mobilePhone()), 15);
            split.getRightPanel().add(inject(proto().workPhone()), 15);

            VistaDecoratorsSplitFlowPanel split2 = new VistaDecoratorsSplitFlowPanel(!isEditable());
            main.add(new VistaLineSeparator());
            main.add(split2);

            AddressUtils.injectIAddress(split2, proto().address(), this);

            return main;
        }
    }
}