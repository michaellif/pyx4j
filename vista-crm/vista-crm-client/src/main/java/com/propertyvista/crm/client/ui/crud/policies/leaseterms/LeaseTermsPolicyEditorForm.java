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
package com.propertyvista.crm.client.ui.crud.policies.leaseterms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.domain.policy.policies.LeaseTermsPolicy;
import com.propertyvista.domain.policy.policies.specials.LeaseTermsInstance;

public class LeaseTermsPolicyEditorForm extends CEntityDecoratableEditor<LeaseTermsPolicy> {

    public LeaseTermsPolicyEditorForm() {
        super(LeaseTermsPolicy.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();

        int row = -1;
        content.setWidget(++row, 0, inject(proto().termsList(), new LeaseTermsFolder()));

        return content;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();
    }

    private static class LeaseTermsFolder extends VistaBoxFolder<LeaseTermsInstance> {

        public LeaseTermsFolder() {
            super(LeaseTermsInstance.class, true);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if ((member instanceof LeaseTermsInstance)) {
                return new LeaseTermsEditorForm();
            }
            return super.create(member);
        }

        @Override
        protected void addItem() {
            new OkCancelDialog("Ladies and Gentelmen, behold: Available Lease Terms!!!") {

                @Override
                public boolean onClickOk() {

                    return false;
                }
            }.show();
        }
    }
}
