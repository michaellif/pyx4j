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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.EntitySelectorDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm;
import com.propertyvista.crm.client.ui.crud.settings.financial.tax.TaxFolder;
import com.propertyvista.crm.rpc.services.selections.SelectLeaseAdjustmentReasonCrudService;
import com.propertyvista.domain.policy.dto.LeaseAdjustmentPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.LeaseAdjustmentPolicyItem;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;

public class LeaseAdjustmentPolicyEditorForm extends PolicyDTOTabPanelBasedEditorForm<LeaseAdjustmentPolicyDTO> {

    private final static I18n i18n = I18n.get(LeaseAdjustmentPolicyEditorForm.class);

    public LeaseAdjustmentPolicyEditorForm() {
        this(false);
    }

    public LeaseAdjustmentPolicyEditorForm(boolean viewMode) {
        super(LeaseAdjustmentPolicyDTO.class, viewMode);
    }

    @Override
    protected List<com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm.TabDescriptor> createCustomTabPanels() {
        return Arrays.asList(//@formatter:off
                new TabDescriptor(createItemsPanel(), i18n.tr("Taxes"))
        );//@formatter:on
    }

    private Widget createItemsPanel() {
        FormFlexPanel panel = new FormFlexPanel();

        int row = -1;
        panel.setWidget(++row, 0, inject(proto().policyItems(), new LeaseAdjustmentPolicyItemFolder(isEditable())));

        return panel;
    }

    private static class LeaseAdjustmentPolicyItemFolder extends VistaBoxFolder<LeaseAdjustmentPolicyItem> {

        public LeaseAdjustmentPolicyItemFolder(boolean modifiable) {
            super(LeaseAdjustmentPolicyItem.class, modifiable);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof LeaseAdjustmentPolicyItem) {
                return new LeaseAdjustmentPolicyItemEditor();
            } else {
                return super.create(member);
            }
        }

        @Override
        protected void addItem() {
            new ProductSelectorDialog().show();
        }

        // internals:

        private static class LeaseAdjustmentPolicyItemEditor extends CEntityDecoratableEditor<LeaseAdjustmentPolicyItem> {

            public LeaseAdjustmentPolicyItemEditor() {
                super(LeaseAdjustmentPolicyItem.class);
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel content = new FormFlexPanel();

                int row = -1;
                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseAdjustmentReason())).build());
                get(proto().leaseAdjustmentReason()).setViewable(true);

                content.setH2(++row, 0, 1, proto().taxes().getMeta().getCaption());
                content.setWidget(++row, 0, inject(proto().taxes(), new TaxFolder(isEditable())));

                return content;
            }
        }

        List<LeaseAdjustmentReason> getAlreadySelected() {
            List<LeaseAdjustmentReason> selected = new Vector<LeaseAdjustmentReason>();
            for (LeaseAdjustmentPolicyItem item : getValue()) {
                if (!item.leaseAdjustmentReason().isNull()) {
                    selected.add(item.leaseAdjustmentReason());
                }
            }
            return selected;
        }

        private class ProductSelectorDialog extends EntitySelectorDialog<LeaseAdjustmentReason> {

            public ProductSelectorDialog() {
                super(LeaseAdjustmentReason.class, true, getAlreadySelected(), i18n.tr("Select Product"));
                setWidth("700px");
            }

            @Override
            public boolean onClickOk() {
                if (getSelectedItems().isEmpty()) {
                    return false;
                } else {
                    for (LeaseAdjustmentReason selected : getSelectedItems()) {
                        LeaseAdjustmentPolicyItem item = EntityFactory.create(LeaseAdjustmentPolicyItem.class);
                        item.leaseAdjustmentReason().set(selected);
                        addItem(item);
                    }

                    return true;
                }
            }

            @Override
            protected List<ColumnDescriptor> defineColumnDescriptors() {
                return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().name()).build(),
                    new MemberColumnDescriptor.Builder(proto().glCode()).build()
                );//@formatter:on
            }

            @Override
            protected AbstractListService<LeaseAdjustmentReason> getSelectService() {
                return GWT.<AbstractListService<LeaseAdjustmentReason>> create(SelectLeaseAdjustmentReasonCrudService.class);
            }
        }
    }
}
