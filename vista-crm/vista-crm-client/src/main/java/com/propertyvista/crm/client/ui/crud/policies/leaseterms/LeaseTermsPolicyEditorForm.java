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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.policies.policy.LeaseTermsCrudService;
import com.propertyvista.domain.policy.policies.LeaseTermsPolicy;
import com.propertyvista.domain.policy.policies.specials.LeaseTermsInstance;

public class LeaseTermsPolicyEditorForm extends CEntityDecoratableEditor<LeaseTermsPolicy> {

    private static final I18n i18n = I18n.get(LeaseTermsPolicyEditorForm.class);

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
            new SelectLeaseTermsDialog() {
                @Override
                public boolean onClickOk() {
                    addItem(getSelectedItem());
                    return true;
                }
            }.show();
        }
    }

    private abstract static class SelectLeaseTermsDialog extends OkCancelDialog {

        private final IListerView<LeaseTermsInstance> leaseTermsListerView;

        public SelectLeaseTermsDialog() {
            super(i18n.tr("Choose lease terms"));

            leaseTermsListerView = new ListerInternalViewImplBase<LeaseTermsInstance>(new LeaseTermsListerViewImpl.LeaseTermsLister());

            ListerActivityBase<LeaseTermsInstance> activity = new ListerActivityBase<LeaseTermsInstance>(new CrmSiteMap.Settings.LeaseTerms(),
                    leaseTermsListerView, (AbstractCrudService<LeaseTermsInstance>) GWT.create(LeaseTermsCrudService.class), LeaseTermsInstance.class);
            SimplePanel p = new SimplePanel();

            p.setSize("100%", "100%");
            activity.start(p, new SimpleEventBus());
            setBody(p);
            setSize("800px", "600px");
        }

        public LeaseTermsInstance getSelectedItem() {
            return leaseTermsListerView.getLister().getSelectedItem();
        }
    }
}
