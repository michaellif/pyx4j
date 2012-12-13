/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 6, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.common;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;

import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.framework.PolicyDTOBase;

public abstract class PolicyListerBase<P extends PolicyDTOBase> extends ListerBase<P> {

    private static final I18n i18n = I18n.get(PolicyListerBase.class);

    private final Button deleteButton;

    protected List<ColumnDescriptor> defaultColumns;

    public PolicyListerBase(Class<P> clazz) {
        super(clazz, true);
        getDataTablePanel().setFilteringEnabled(false);
        getDataTablePanel().getDataTable().setHasCheckboxColumn(true);

        addActionItem(deleteButton = new Button(i18n.tr("Delete Checked Items"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                MessageDialog.confirm(i18n.tr("Confirm"), i18n.tr("Do you really want to delete checked items?"), new Command() {
                    @Override
                    public void execute() {
                        Queue<P> checkedItems = new LinkedList<P>(getDataTablePanel().getDataTable().getCheckedItems());
                        validateAndRemoveRecursively(checkedItems);
                    }
                });
            }
        }));

        defaultColumns = Arrays.asList(//@formatter:off
                 new MemberColumnDescriptor.Builder(proto().nodeType()).sortable(false).build(),
                 new MemberColumnDescriptor.Builder(proto().nodeRepresentation()).sortable(false).build()
        );//@formatter:on
        setColumnDescriptors(new LinkedList<ColumnDescriptor>());
    }

    /**
     * Set column descriptors for the {@link PolicyListerBase}, the columns for {@link PolicyDTOBase#nodeType()} and {@link PolicyDTOBase#nodeRepresentation()}
     * are added implicitly, so there's no need to `set' them.
     */
    @Override
    public void setColumnDescriptors(List<ColumnDescriptor> columnDescriptors) {
        List<ColumnDescriptor> columns = new LinkedList<ColumnDescriptor>(defaultColumns);
        columns.addAll(columnDescriptors);
        super.setColumnDescriptors(columns);
    }

    /**
     * Set column descriptors for the {@link PolicyListerBase}, the columns for {@link PolicyDTOBase#nodeType()} and {@link PolicyDTOBase#nodeRepresentation()}
     * are added implicitly, so there's no need to `set' them.
     */
    @Override
    public void setColumnDescriptors(ColumnDescriptor... columnDescriptors) {
        List<ColumnDescriptor> columns = new LinkedList<ColumnDescriptor>(defaultColumns);
        columns.addAll(Arrays.asList(columnDescriptors));
        super.setColumnDescriptors(columns.toArray(new ColumnDescriptor[columns.size()]));
    };

    public Button getAddButton() {
        return getDataTablePanel().getAddButton();
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    private void validateAndRemoveRecursively(final Queue<P> itemsToRemove) {
        if (!itemsToRemove.isEmpty()) {
            final P item = itemsToRemove.poll();
            if (item.node().isInstanceOf(OrganizationPoliciesNode.class)) {
                MessageDialog.info(i18n.tr("Deletion of the organization policy is forbidden"));
                validateAndRemoveRecursively(itemsToRemove);
            } else {
                getPresenter().delete(item.getPrimaryKey());
                validateAndRemoveRecursively(itemsToRemove);
            }
        }
    }
}
