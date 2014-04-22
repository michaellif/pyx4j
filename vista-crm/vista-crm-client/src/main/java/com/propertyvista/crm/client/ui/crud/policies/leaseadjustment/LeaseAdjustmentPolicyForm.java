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

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.components.boxes.LeaseAdjustmentReasonSelectorDialog;
import com.propertyvista.crm.client.ui.crud.administration.financial.tax.TaxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.policy.dto.LeaseAdjustmentPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.LeaseAdjustmentPolicyItem;

public class LeaseAdjustmentPolicyForm extends PolicyDTOTabPanelBasedForm<LeaseAdjustmentPolicyDTO> {

    private final static I18n i18n = I18n.get(LeaseAdjustmentPolicyForm.class);

    public LeaseAdjustmentPolicyForm(IForm<LeaseAdjustmentPolicyDTO> view) {
        super(LeaseAdjustmentPolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        return Arrays.asList(createItemsPanel());
    }

    private TwoColumnFlexFormPanel createItemsPanel() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Items"));

        int row = -1;
        panel.setWidget(++row, 0, 2, inject(proto().policyItems(), new LeaseAdjustmentPolicyItemFolder(isEditable())));

        return panel;
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

            new LeaseAdjustmentReasonSelectorDialog(LeaseAdjustmentPolicyForm.this.getParentView()) {
                @Override
                public void onClickOk() {
                    for (ARCode selected : getSelectedItems()) {
                        LeaseAdjustmentPolicyItem item = EntityFactory.create(LeaseAdjustmentPolicyItem.class);
                        item.code().set(selected);
                        addItem(item);
                    }
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
                TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

                int row = -1;
                content.setWidget(++row, 0, 2, inject(proto().code(), new FieldDecoratorBuilder(20, true).build()));
                get(proto().code()).setEditable(false);

                content.setH3(++row, 0, 2, proto().taxes().getMeta().getCaption());
                content.setWidget(++row, 0, 2, inject(proto().taxes(), new TaxFolder(LeaseAdjustmentPolicyForm.this)));

                return content;
            }
        }
    }
}
