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

import java.util.LinkedList;
import java.util.Queue;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.domain.policy.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.dto.PolicyDTOBase;

public abstract class PolicyListerBase<P extends PolicyDTOBase> extends ListerBase<P> {

    private static final I18n i18n = I18n.get(PolicyListerBase.class);

//    protected List<ColumnDescriptor<P>> defaultColumns;

    public PolicyListerBase(Class<P> clazz, Class<? extends CrudAppPlace> itemOpenPlaceClass) {
        super(clazz, itemOpenPlaceClass, false, true);
        getDataTablePanel().setFilteringEnabled(false);
        getDataTablePanel().getDataTable().setHasCheckboxColumn(true);

        addActionItem(new Button(i18n.tr("Delete Checked Items"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Queue<P> checkedItems = new LinkedList<P>(getDataTablePanel().getDataTable().getCheckedItems());
                validateAndRemoveRecursively(checkedItems);
            }
        }));

        // TODO : just compilation fix!!!
//        defaultColumns = Arrays.asList(//@formatter:off
//                 (ColumnDescriptor<P>)new MemberColumnDescriptor.Builder(proto().nodeType()).sortable(false).build(),
//                 (ColumnDescriptor<P>)new MemberColumnDescriptor.Builder(proto().nodeRepresentation()).sortable(false).build()
//        );//@formatter:on
//        setColumnDescriptors(new LinkedList<ColumnDescriptor<P>>());
    }

//    @Override
//    public void setColumnDescriptors(List<ColumnDescriptor<P>> columnDescriptors) {
//        List<ColumnDescriptor<P>> columns = new LinkedList<ColumnDescriptor<P>>(defaultColumns);
//        columns.addAll(columnDescriptors);
//        super.setColumnDescriptors(columns);
//    }

    public void validateAndRemoveRecursively(final Queue<P> itemsToRemove) {
        if (!itemsToRemove.isEmpty()) {
            final P item = itemsToRemove.poll();
            if (item.node().isInstanceOf(OrganizationPoliciesNode.class)) {
                (new OkCancelDialog(i18n.tr("Are you sure it's ok to remove the policy for the whole company?")) {
                    @Override
                    public boolean onClickOk() {
                        getPresenter().delete(item.getPrimaryKey());
                        validateAndRemoveRecursively(itemsToRemove);
                        return true;
                    }
                }).show();
            } else {
                getPresenter().delete(item.getPrimaryKey());
                validateAndRemoveRecursively(itemsToRemove);
            }
        }
    }
}
