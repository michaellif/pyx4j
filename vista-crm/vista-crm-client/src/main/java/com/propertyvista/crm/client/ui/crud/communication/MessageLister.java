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
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.rpc.CrmSiteMap.Communication.Message;
import com.propertyvista.crm.rpc.CrmSiteMap.Communication.Ticket;
import com.propertyvista.crm.rpc.services.MessageCrudService;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.MessageGroupCategory;
import com.propertyvista.dto.MessageDTO;

public class MessageLister extends AbstractLister<MessageDTO> {
    private static final I18n i18n = I18n.get(MessageLister.class);

    public MessageLister() {
        super(MessageDTO.class, true);

        setDataTableModel(new DataTableModel<MessageDTO>(createColumnDescriptors(false)));

        getDataTablePanel().setFilteringEnabled(true);
        // No sorting work for it
        getDataTablePanel().getDataTable().setHasColumnClickSorting(false);
    }

    public static ColumnDescriptor[] createColumnDescriptors(boolean isTicket) {
        MessageDTO proto = EntityFactory.getEntityPrototype(MessageDTO.class);

        return isTicket ? new ColumnDescriptor[] {//@formatter:off
        new MemberColumnDescriptor.Builder(proto.isRead()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.star()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.highImportance()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.hasAttachments()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.sender().name()).columnTitle(i18n.tr("Sender")).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.date()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.subject()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.topic(), false).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.topic().category(), false).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto.allowedReply()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.thread().owner()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.ownerForList(), false).columnTitle(i18n.tr("Owner")).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto.status()).searchable(true).build() }:
            new ColumnDescriptor[] {//@formatter:off
        new MemberColumnDescriptor.Builder(proto.isRead()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.star()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.highImportance()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.hasAttachments()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.sender().name()).columnTitle(i18n.tr("Sender")).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.date()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.subject()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.topic(), false).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.topic().category(), false).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto.allowedReply()).searchable(false).build() };
      //@formatter:on
    }

    @Override
    protected EntityListCriteria<MessageDTO> updateCriteria(EntityListCriteria<MessageDTO> criteria) {
        com.pyx4j.site.client.ui.prime.IPrimePane.Presenter p = getPresenter();
        AppPlace place = p.getPlace();
        MessageCategory mc = place instanceof Ticket ? ((Ticket) place).getMessageCategory() : ((Message) place).getMessageCategory();
        if (mc == null) {
            if (place instanceof Ticket) {
                getDataTablePanel().getAddButton().setCaption(i18n.tr("New Ticket"));
                criteria.ne(criteria.proto().topic().category(), MessageGroupCategory.Custom);
            } else {
                getDataTablePanel().getAddButton().setCaption(i18n.tr("New Message"));
                criteria.eq(criteria.proto().topic().category(), MessageGroupCategory.Custom);
            }
        } else {
            criteria.eq(criteria.proto().topic(), mc);
            getDataTablePanel().getAddButton().setCaption(
                    MessageGroupCategory.Custom.equals(mc.category().getValue()) ? i18n.tr("New Message") : i18n.tr("New Ticket"));
        }
        setDataTableModel(new DataTableModel<MessageDTO>(createColumnDescriptors(place instanceof Ticket)));

        return super.updateCriteria(criteria);
    }

    @Override
    protected void onItemNew() {
        com.pyx4j.site.client.ui.prime.IPrimePane.Presenter p = getPresenter();
        AppPlace place = p.getPlace();
        MessageCrudService.MessageInitializationData initData = EntityFactory.create(MessageCrudService.MessageInitializationData.class);
        MessageCategory mc = place instanceof Ticket ? ((Ticket) place).getMessageCategory() : ((Message) place).getMessageCategory();

        if (mc != null) {
            initData.messageCategory().set(mc);
        }
        if (place instanceof Ticket) {
            getPresenter().editNew(Ticket.class, initData);
        } else {
            getPresenter().editNew(Message.class, initData);
        }
    }
}
