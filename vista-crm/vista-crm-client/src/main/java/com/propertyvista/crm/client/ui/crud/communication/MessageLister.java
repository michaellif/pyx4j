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

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.rpc.CrmSiteMap.Communication.Message;
import com.propertyvista.crm.rpc.services.MessageCrudService;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.CategoryType;
import com.propertyvista.dto.MessageDTO;

public class MessageLister extends AbstractLister<MessageDTO> {
    private static final I18n i18n = I18n.get(MessageLister.class);

    private Button newMessage;

    private Button newTicket;

    public MessageLister() {
        super(MessageDTO.class, true);

        setDataTableModel(new DataTableModel<MessageDTO>(createColumnDescriptors(CategoryType.Message)));

        getDataTablePanel().setFilteringEnabled(true);
        // No sorting work for it
        getDataTablePanel().getDataTable().setHasColumnClickSorting(false);

        addActionItem(newTicket = new Button(FolderImages.INSTANCE.addButton().hover(), i18n.tr("New Ticket"), new Command() {
            @Override
            public void execute() {
                editNewEntity(CategoryType.Ticket);
            }
        }));
        newTicket.setPermission(DataModelPermission.permissionCreate(MessageDTO.class));

        addActionItem(newMessage = new Button(FolderImages.INSTANCE.addButton().hover(), i18n.tr("New Message"), new Command() {
            @Override
            public void execute() {
                editNewEntity(CategoryType.Message);
            }
        }));
        newMessage.setPermission(DataModelPermission.permissionCreate(MessageDTO.class));
    }

    public static ColumnDescriptor[] createColumnDescriptors(CategoryType category) {
        MessageDTO proto = EntityFactory.getEntityPrototype(MessageDTO.class);

        if (category == null) {
            return new ColumnDescriptor[] {//@formatter:off
                    new MemberColumnDescriptor.Builder(proto.isRead()).searchable(false).width("100px").build(),
                    new MemberColumnDescriptor.Builder(proto.star()).searchable(false).width("100px").build(),
                    new MemberColumnDescriptor.Builder(proto.highImportance()).searchable(false).width("100px").build(),
                    new MemberColumnDescriptor.Builder(proto.hasAttachments()).searchable(false).width("100px").build(),
                    new MemberColumnDescriptor.Builder(proto.sender().name()).columnTitle(i18n.tr("Sender")).searchable(false).width("200px").build(),
                    new MemberColumnDescriptor.Builder(proto.date()).searchable(false).width("200px").build(),
                    new MemberColumnDescriptor.Builder(proto.subject()).searchable(false).width("1000px").build(),
                    new MemberColumnDescriptor.Builder(proto.category(), false).searchable(false).width("300px").build(),
                    new MemberColumnDescriptor.Builder(proto.category().categoryType(), false).searchableOnly().columnTitle(i18n.tr("Category")).build(),
                    new MemberColumnDescriptor.Builder(proto.allowedReply()).searchable(false).width("100px").build(),
                    new MemberColumnDescriptor.Builder(proto.thread().owner()).searchable(false).width("200px").build(),
                    new MemberColumnDescriptor.Builder(proto.ownerForList(), false).columnTitle(i18n.tr("Owner")).searchableOnly().build(),
                    new MemberColumnDescriptor.Builder(proto.status()).searchable(true).width("100px").build() };
        }
        else {
        switch (category) {
        case Ticket:
            return new ColumnDescriptor[] {//@formatter:off
                new MemberColumnDescriptor.Builder(proto.star()).searchable(false).width("100px").build(),
                new MemberColumnDescriptor.Builder(proto.highImportance()).searchable(false).width("100px").build(),
                new MemberColumnDescriptor.Builder(proto.hasAttachments()).searchable(false).width("100px").build(),
                new MemberColumnDescriptor.Builder(proto.sender().name()).columnTitle(i18n.tr("Sender")).searchable(false).width("200px").build(),
                new MemberColumnDescriptor.Builder(proto.date()).searchable(false).width("200px").build(),
                new MemberColumnDescriptor.Builder(proto.subject()).searchable(false).width("1000px").build(),
                new MemberColumnDescriptor.Builder(proto.category(), false).searchable(false).width("300px").build(),
                new MemberColumnDescriptor.Builder(proto.category().categoryType(), false).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto.allowedReply()).searchable(false).width("100px").build(),
                new MemberColumnDescriptor.Builder(proto.thread().owner()).searchable(false).width("200px").build(),
                new MemberColumnDescriptor.Builder(proto.ownerForList(), false).columnTitle(i18n.tr("Owner")).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto.status()).searchable(true).width("100px").build() };
        case Message:
            return new ColumnDescriptor[] {//@formatter:off
                new MemberColumnDescriptor.Builder(proto.isRead()).searchable(false).width("100px").build(),
                new MemberColumnDescriptor.Builder(proto.star()).searchable(false).width("100px").build(),
                new MemberColumnDescriptor.Builder(proto.highImportance()).searchable(false).width("100px").build(),
                new MemberColumnDescriptor.Builder(proto.hasAttachments()).searchable(false).width("100px").build(),
                new MemberColumnDescriptor.Builder(proto.sender().name()).columnTitle(i18n.tr("Sender")).searchable(false).width("200px").build(),
                new MemberColumnDescriptor.Builder(proto.date()).searchable(false).width("200px").build(),
                new MemberColumnDescriptor.Builder(proto.subject()).searchable(false).width("1000px").build(),
                new MemberColumnDescriptor.Builder(proto.category(), false).searchable(false).width("300px").build(),
                new MemberColumnDescriptor.Builder(proto.category().categoryType(), false).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto.allowedReply()).searchable(false).width("100px").build() };
     //@formatter:on
            default:
                return new ColumnDescriptor[] {//@formatter:off
                    new MemberColumnDescriptor.Builder(proto.isRead()).searchable(false).width("100px").build(),
                    new MemberColumnDescriptor.Builder(proto.star()).searchable(false).width("100px").build(),
                    new MemberColumnDescriptor.Builder(proto.highImportance()).searchable(false).width("100px").build(),
                    new MemberColumnDescriptor.Builder(proto.hasAttachments()).searchable(false).width("100px").build(),
                    new MemberColumnDescriptor.Builder(proto.sender().name()).columnTitle(i18n.tr("Sender")).searchable(false).width("200px").build(),
                    new MemberColumnDescriptor.Builder(proto.date()).searchable(false).width("200px").build(),
                    new MemberColumnDescriptor.Builder(proto.subject()).searchable(false).width("1000px").build(),
                    new MemberColumnDescriptor.Builder(proto.category(), false).searchable(false).width("300px").build(),
                    new MemberColumnDescriptor.Builder(proto.category().categoryType(), false).searchableOnly().build(),
                    new MemberColumnDescriptor.Builder(proto.allowedReply()).searchable(false).width("100px").build(),
                    new MemberColumnDescriptor.Builder(proto.thread().owner()).searchable(false).width("200px").build(),
                    new MemberColumnDescriptor.Builder(proto.ownerForList(), false).columnTitle(i18n.tr("Owner")).searchableOnly().build(),
                    new MemberColumnDescriptor.Builder(proto.status()).searchable(true).width("100px").build() };
            }//@formatter:on
        }
    }

    @Override
    protected EntityListCriteria<MessageDTO> updateCriteria(EntityListCriteria<MessageDTO> criteria) {
        com.pyx4j.site.client.ui.prime.IPrimePane.Presenter p = getPresenter();
        AppPlace place = p.getPlace();
        Object placeCriteria = place instanceof Message ? ((Message) place).getCriteria() : null;
        CategoryType category = null;
        if (placeCriteria == null) {
            newMessage.setVisible(true);
            newTicket.setVisible(true);
            getDataTablePanel().getAddButton().setVisible(false);
        } else {
            newMessage.setVisible(false);
            newTicket.setVisible(false);
            getDataTablePanel().getAddButton().setVisible(true);
            if (placeCriteria instanceof CategoryType) {
                getDataTablePanel().getAddButton().setCaption(i18n.tr("New") + " " + placeCriteria.toString());
                criteria.eq(criteria.proto().category().categoryType(), category = (CategoryType) placeCriteria);
            } else {
                MessageCategory mc = (MessageCategory) placeCriteria;
                getDataTablePanel().getAddButton().setCaption(i18n.tr("New") + " " + (category = mc.categoryType().getValue()).toString());
                criteria.eq(criteria.proto().category(), mc);
            }
        }
        setDataTableModel(new DataTableModel<MessageDTO>(createColumnDescriptors(category)));

        return super.updateCriteria(criteria);
    }

    @Override
    protected void onItemNew() {
        com.pyx4j.site.client.ui.prime.IPrimePane.Presenter p = getPresenter();
        AppPlace place = p.getPlace();
        Object placeCriteria = place instanceof Message ? ((Message) place).getCriteria() : null;

        editNewEntity(placeCriteria);

    }

    private void editNewEntity(Object placeCriteria) {
        MessageCrudService.MessageInitializationData initData = EntityFactory.create(MessageCrudService.MessageInitializationData.class);
        if (placeCriteria == null) {
            initData.categoryType().setValue(null);
        } else if (placeCriteria instanceof MessageCategory) {
            initData.messageCategory().set((MessageCategory) placeCriteria);
        } else if (placeCriteria instanceof CategoryType) {
            initData.categoryType().setValue((CategoryType) placeCriteria);
        }
        getPresenter().editNew(Message.class, initData);
    }
}
