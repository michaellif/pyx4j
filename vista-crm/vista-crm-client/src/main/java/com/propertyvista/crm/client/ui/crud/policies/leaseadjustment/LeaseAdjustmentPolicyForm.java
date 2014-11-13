/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.leaseadjustment;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.components.boxes.LeaseAdjustmentReasonSelectionDialog;
import com.propertyvista.crm.client.ui.crud.administration.financial.tax.TaxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.policy.dto.LeaseAdjustmentPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.LeaseAdjustmentPolicyItem;

public class LeaseAdjustmentPolicyForm extends PolicyDTOTabPanelBasedForm<LeaseAdjustmentPolicyDTO> {

    private final static I18n i18n = I18n.get(LeaseAdjustmentPolicyForm.class);

    public LeaseAdjustmentPolicyForm(IFormView<LeaseAdjustmentPolicyDTO, ?> view) {
        super(LeaseAdjustmentPolicyDTO.class, view);
        addTab(createItemsPanel(), i18n.tr(i18n.tr("Items")));

    }

    private IsWidget createItemsPanel() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().policyItems(), new LeaseAdjustmentPolicyItemFolder(isEditable()));
        return formPanel;
    }

    class LeaseAdjustmentPolicyItemFolder extends VistaBoxFolder<LeaseAdjustmentPolicyItem> {

        public LeaseAdjustmentPolicyItemFolder(boolean modifiable) {
            super(LeaseAdjustmentPolicyItem.class, modifiable);
        }

        @Override
        protected CForm<LeaseAdjustmentPolicyItem> createItemForm(IObject<?> member) {
            return new LeaseAdjustmentPolicyItemEditor();
        }

        @Override
        protected void addItem() {
            List<ARCode> selected = new Vector<ARCode>();
            for (LeaseAdjustmentPolicyItem item : getValue()) {
                if (!item.code().isNull()) {
                    selected.add(item.code());
                }
            }

            new LeaseAdjustmentReasonSelectionDialog() {
                @Override
                public boolean onClickOk() {
                    for (ARCode selected : getSelectedItems()) {
                        LeaseAdjustmentPolicyItem item = EntityFactory.create(LeaseAdjustmentPolicyItem.class);
                        item.code().set(selected);
                        addItem(item);
                    }
                    return true;
                }
            }.show();
        }

        // internals:

        class LeaseAdjustmentPolicyItemEditor extends CForm<LeaseAdjustmentPolicyItem> {

            public LeaseAdjustmentPolicyItemEditor() {
                super(LeaseAdjustmentPolicyItem.class);
            }

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().code()).decorate().componentWidth(200);
                get(proto().code()).setEditable(false);

                formPanel.h3(proto().taxes().getMeta().getCaption());
                formPanel.append(Location.Dual, proto().taxes(), new TaxFolder(LeaseAdjustmentPolicyForm.this));

                return formPanel;
            }
        }
    }
}
