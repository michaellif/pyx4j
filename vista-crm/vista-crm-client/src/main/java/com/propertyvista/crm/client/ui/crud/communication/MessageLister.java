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

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.dto.MessageDTO;

public class MessageLister extends AbstractLister<MessageDTO> {
    private static final I18n i18n = I18n.get(MessageLister.class);

    public MessageLister() {
        super(MessageDTO.class, true);

        setDataTableModel(new DataTableModel<MessageDTO>(createColumnDescriptors()));

        getDataTablePanel().setFilteringEnabled(false);
        //getDataTablePanel().getFilterButton().setTextLabel(i18n.tr("Select Communication Topic"));
        // No filtering work for it
        getDataTablePanel().getDataTable().setHasColumnClickSorting(false);
    }

    public static ColumnDescriptor[] createColumnDescriptors() {
        MessageDTO proto = EntityFactory.getEntityPrototype(MessageDTO.class);

        return new ColumnDescriptor[] {//@formatter:off
        new MemberColumnDescriptor.Builder(proto.isRead()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.star()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.highImportance()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.hasAttachments()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.sender().name()).columnTitle(i18n.tr("Sender")).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.date()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.subject()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.topic(), false).searchable(true).build(),
                new MemberColumnDescriptor.Builder(proto.allowedReply()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.status()).searchable(false).build() };
      //@formatter:on
    }

}
