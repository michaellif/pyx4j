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
import com.propertyvista.crm.rpc.services.MessageCrudService;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.MessageGroupCategory;
import com.propertyvista.dto.MessageDTO;

public class MessageLister extends AbstractLister<MessageDTO> {
    private static final I18n i18n = I18n.get(MessageLister.class);

    public MessageLister() {
        super(MessageDTO.class, true);

        setDataTableModel(new DataTableModel<MessageDTO>(createColumnDescriptors(MessageGroupCategory.Message)));

        getDataTablePanel().setFilteringEnabled(true);
        // No sorting work for it
        getDataTablePanel().getDataTable().setHasColumnClickSorting(false);
    }

    public static ColumnDescriptor[] createColumnDescriptors(MessageGroupCategory category) {
        MessageDTO proto = EntityFactory.getEntityPrototype(MessageDTO.class);

        switch (category) {
        case Ticket:
            return new ColumnDescriptor[] {//@formatter:off
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
                new MemberColumnDescriptor.Builder(proto.status()).searchable(true).build() };
        case Message:
            return new ColumnDescriptor[] {//@formatter:off
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
        default:
            return new ColumnDescriptor[] {//@formatter:off
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
        }//@formatter:on
    }

    @Override
    protected EntityListCriteria<MessageDTO> updateCriteria(EntityListCriteria<MessageDTO> criteria) {
        com.pyx4j.site.client.ui.prime.IPrimePane.Presenter p = getPresenter();
        AppPlace place = p.getPlace();
        Object placeCriteria = place instanceof Message ? ((Message) place).getCriteria() : null;
        MessageGroupCategory category;
        if (placeCriteria == null) {
            getDataTablePanel().getAddButton().setCaption(i18n.tr("New Message"));
            criteria.eq(criteria.proto().topic().category(), category = MessageGroupCategory.Message);
        } else if (placeCriteria instanceof MessageGroupCategory) {
            getDataTablePanel().getAddButton().setCaption(i18n.tr("New" + " " + placeCriteria.toString()));
            criteria.eq(criteria.proto().topic().category(), category = (MessageGroupCategory) placeCriteria);
        } else {
            MessageCategory mc = (MessageCategory) placeCriteria;
            getDataTablePanel().getAddButton().setCaption(i18n.tr("New" + " " + (category = mc.category().getValue()).toString()));
            criteria.eq(criteria.proto().topic(), mc);
        }
        setDataTableModel(new DataTableModel<MessageDTO>(createColumnDescriptors(category)));

        return super.updateCriteria(criteria);
    }

    @Override
    protected void onItemNew() {
        com.pyx4j.site.client.ui.prime.IPrimePane.Presenter p = getPresenter();
        AppPlace place = p.getPlace();
        MessageCrudService.MessageInitializationData initData = EntityFactory.create(MessageCrudService.MessageInitializationData.class);
        Object placeCriteria = place instanceof Message ? ((Message) place).getCriteria() : null;

        if (placeCriteria == null) {
            initData.categoryType().setValue(MessageGroupCategory.Message);
        } else if (placeCriteria instanceof MessageCategory) {
            initData.messageCategory().set((MessageCategory) placeCriteria);
        } else if (placeCriteria instanceof MessageGroupCategory) {
            initData.categoryType().setValue((MessageGroupCategory) placeCriteria);
        }
        getPresenter().editNew(Message.class, initData);

    }
}
