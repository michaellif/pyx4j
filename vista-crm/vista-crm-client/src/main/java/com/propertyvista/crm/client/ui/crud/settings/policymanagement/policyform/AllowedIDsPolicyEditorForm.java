/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.policymanagement.policyform;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.CEntityListBox;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.domain.policy.policies.AllowedIDs;
import com.propertyvista.domain.policy.policies.IdentificationDocument;

public class AllowedIDsPolicyEditorForm extends CEntityDecoratableEditor<AllowedIDs> {

    public AllowedIDsPolicyEditorForm() {
        super(AllowedIDs.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().allowedIDs(), new CEntityListBox<IdentificationDocument>())).build());
        return content;
    }

    private static class IdEdtiorForm extends CEntityDecoratableEditor<IdentificationDocument> {

        public IdEdtiorForm() {
            super(IdentificationDocument.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel content = new FormFlexPanel();
            content.setWidget(0, 0, new DecoratorBuilder(inject(proto().name())).labelWidth(7).componentWidth(10).build());

            return content;
        }
    }

    private static class IdFolder extends VistaBoxFolder<IdentificationDocument> {

        public IdFolder() {
            super(IdentificationDocument.class);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof IdentificationDocument) {
                return new IdEdtiorForm();
            }
            return super.create(member);
        }
    }

}