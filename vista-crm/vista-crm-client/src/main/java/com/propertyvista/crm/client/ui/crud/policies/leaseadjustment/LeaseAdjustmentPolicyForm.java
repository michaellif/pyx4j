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

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.components.boxes.LeaseAdjustmentReasonSelectorDialog;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.crm.client.ui.crud.settings.financial.tax.TaxFolder;
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

    private static class LeaseAdjustmentPolicyItemFolder extends VistaBoxFolder<LeaseAdjustmentPolicyItem> {

        public LeaseAdjustmentPolicyItemFolder(boolean modifiable) {
            super(LeaseAdjustmentPolicyItem.class, modifiable);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof LeaseAdjustmentPolicyItem) {
                return new LeaseAdjustmentPolicyItemEditor();
            } else {
                return super.create(member);
            }
        }

        @Override
        protected void addItem() {
            List<ARCode> selected = new Vector<ARCode>();
            for (LeaseAdjustmentPolicyItem item : getValue()) {
                if (!item.code().isNull()) {
                    selected.add(item.code());
                }
            }

            new LeaseAdjustmentReasonSelectorDialog(selected) {
                @Override
                public boolean onClickOk() {
                    if (!getSelectedItems().isEmpty()) {
                        for (ARCode selected : getSelectedItems()) {
                            LeaseAdjustmentPolicyItem item = EntityFactory.create(LeaseAdjustmentPolicyItem.class);
                            item.code().set(selected);
                            addItem(item);
                        }
                    }
                    return !getSelectedItems().isEmpty();
                }
            }.show();
        }

        // internals:

        private static class LeaseAdjustmentPolicyItemEditor extends CEntityDecoratableForm<LeaseAdjustmentPolicyItem> {

            public LeaseAdjustmentPolicyItemEditor() {
                super(LeaseAdjustmentPolicyItem.class);
            }

            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

                int row = -1;
                content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().code()), 20, true).build());
                get(proto().code()).setEditable(false);

                content.setH3(++row, 0, 2, proto().taxes().getMeta().getCaption());
                content.setWidget(++row, 0, 2, inject(proto().taxes(), new TaxFolder(isEditable())));

                return content;
            }
        }
    }
}
