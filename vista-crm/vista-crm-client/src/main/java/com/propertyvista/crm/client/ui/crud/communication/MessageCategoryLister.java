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
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemSelectionHandler;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.SiteDataTablePanel;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.event.BoardUpdateEvent;
import com.propertyvista.crm.rpc.services.MessageCategoryCrudService;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.CategoryType;

public class MessageCategoryLister extends SiteDataTablePanel<MessageCategory> {

    private static final I18n i18n = I18n.get(MessageCategoryLister.class);

    public MessageCategoryLister() {
        super(MessageCategory.class, GWT.<AbstractCrudService<MessageCategory>> create(MessageCategoryCrudService.class), true, true);
        addItemSelectionHandler(new ItemSelectionHandler() {

            @Override
            public void onChange() {
                if (getSelectedItem() != null && CategoryType.Ticket.equals(getSelectedItem().categoryType().getValue())) {
                    MessageDialog.warn(i18n.tr("Error"), i18n.tr("No delete operation is allowed for predefined message categories"));
                }
            }

        });
        setDataTableModel(new DataTableModel<MessageCategory>(createColumnDescriptors()));
    }

    public static ColumnDescriptor[] createColumnDescriptors() {
        MessageCategory proto = EntityFactory.getEntityPrototype(MessageCategory.class);

        return new ColumnDescriptor[] {//@formatter:off
                new MemberColumnDescriptor.Builder(proto.category()).columnTitle(i18n.tr("Name")).build(),
                new MemberColumnDescriptor.Builder(proto.categoryType()).columnTitle(i18n.tr("Type")).build(),
                new MemberColumnDescriptor.Builder(proto.roles(),false).sortable(false).build(),
                new MemberColumnDescriptor.Builder(proto.dispatchers(),false).sortable(false).build()};
      //@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().category(), false));
    }

    @Override
    protected void onItemsDelete(final Collection<MessageCategory> items) {
        for (MessageCategory item : items) {
            if (CategoryType.Ticket.equals(item.categoryType().getValue())) {
                MessageDialog.warn(i18n.tr("Error"), i18n.tr("No delete operation is allowed for predefined message categories"));
                return;
            }
        }

        super.onItemsDelete(items);
    }

    @Override
    public void onDeleted(Key itemID, boolean isSuccessful) {
        super.onDeleted(itemID, isSuccessful);
        if (isSuccessful) {
            AppSite.instance();
            AppSite.getEventBus().fireEvent(new BoardUpdateEvent(MessageCategory.class));
        }
    }
}
