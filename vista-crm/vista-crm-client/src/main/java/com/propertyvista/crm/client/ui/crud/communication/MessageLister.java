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

import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.AndCriterion;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractPrimeLister;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.rpc.CrmSiteMap.Communication.Message;
import com.propertyvista.crm.rpc.services.MessageCrudService;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.CategoryType;
import com.propertyvista.domain.communication.MessageCategory.TicketType;
import com.propertyvista.dto.MessageDTO;

public class MessageLister extends AbstractPrimeLister<MessageDTO> {
    private static final I18n i18n = I18n.get(MessageLister.class);

    private Button newMessage;

    private Button newTicket;

    private List<? extends CommunicationEndpoint> recipientScope;

    public MessageLister(List<? extends CommunicationEndpoint> recipientScope) {
        this();
        this.recipientScope = recipientScope;
    }

    public MessageLister() {
        super(MessageDTO.class, true);

        setDataTableModel(new DataTableModel<MessageDTO>(createColumnDescriptors(CategoryType.Message)));

        getDataTablePanel().setFilteringEnabled(true);
        // No sorting work for it
        getDataTablePanel().getDataTable().setHasColumnClickSorting(false);

        addActionItem(newMessage = new Button(FolderImages.INSTANCE.addButton().hover(), i18n.tr("New Message"), new Command() {
            @Override
            public void execute() {
                editNewEntity(CategoryType.Message);
            }
        }));
        addActionItem(newTicket = new Button(FolderImages.INSTANCE.addButton().hover(), i18n.tr("New Ticket"), new Command() {
            @Override
            public void execute() {
                editNewEntity(CategoryType.Ticket);
            }
        }));

        newTicket.setPermission(DataModelPermission.permissionCreate(MessageDTO.class));
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
                    new MemberColumnDescriptor.Builder(proto.senderDTO().name()).columnTitle(i18n.tr("Sender")).searchable(false).width("200px").build(),
                    new MemberColumnDescriptor.Builder(proto.date()).searchable(false).width("200px").build(),
                    new MemberColumnDescriptor.Builder(proto.subject()).searchable(false).width("1000px").build(),
                    new MemberColumnDescriptor.Builder(proto.category(), false).searchable(false).width("300px").build(),
                    new MemberColumnDescriptor.Builder(proto.category().categoryType(), false).searchableOnly().columnTitle(i18n.tr("Category")).build(),
                    new MemberColumnDescriptor.Builder(proto.allowedReply()).searchable(false).width("100px").build(),
                    new MemberColumnDescriptor.Builder(proto.thread().owner()).searchable(false).width("200px").build(),
                    new MemberColumnDescriptor.Builder(proto.ownerForList(), false).columnTitle(i18n.tr("Owner")).searchableOnly().build(),
                    new MemberColumnDescriptor.Builder(proto.status()).searchable(true).width("100px").build(),
                    new MemberColumnDescriptor.Builder(proto.hidden(), false).searchableOnly().columnTitle(i18n.tr("Hidden")).build()};
        }
        else {
        switch (category) {
        case Ticket:
            return new ColumnDescriptor[] {//@formatter:off
                new MemberColumnDescriptor.Builder(proto.star()).searchable(false).width("100px").build(),
                new MemberColumnDescriptor.Builder(proto.highImportance()).searchable(false).width("100px").build(),
                new MemberColumnDescriptor.Builder(proto.hasAttachments()).searchable(false).width("100px").build(),
                new MemberColumnDescriptor.Builder(proto.senderDTO().name()).columnTitle(i18n.tr("Sender")).searchable(false).width("200px").build(),
                new MemberColumnDescriptor.Builder(proto.date()).searchable(false).width("200px").build(),
                new MemberColumnDescriptor.Builder(proto.subject()).searchable(false).width("1000px").build(),
                new MemberColumnDescriptor.Builder(proto.category(), false).searchable(false).width("300px").build(),
                new MemberColumnDescriptor.Builder(proto.category().categoryType(), false).searchableOnly().columnTitle(i18n.tr("Category")).build(),
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
                new MemberColumnDescriptor.Builder(proto.senderDTO().name()).columnTitle(i18n.tr("Sender")).searchable(false).width("200px").build(),
                new MemberColumnDescriptor.Builder(proto.date()).searchable(false).width("200px").build(),
                new MemberColumnDescriptor.Builder(proto.subject()).searchable(false).width("1000px").build(),
                new MemberColumnDescriptor.Builder(proto.category(), false).searchable(false).width("300px").build(),
                new MemberColumnDescriptor.Builder(proto.category().categoryType(), false).searchableOnly().columnTitle(i18n.tr("Category")).build(),
                new MemberColumnDescriptor.Builder(proto.allowedReply()).searchable(false).width("100px").build() };
     //@formatter:on
            default:
                return new ColumnDescriptor[] {//@formatter:off
                    new MemberColumnDescriptor.Builder(proto.isRead()).searchable(false).width("100px").build(),
                    new MemberColumnDescriptor.Builder(proto.star()).searchable(false).width("100px").build(),
                    new MemberColumnDescriptor.Builder(proto.highImportance()).searchable(false).width("100px").build(),
                    new MemberColumnDescriptor.Builder(proto.hasAttachments()).searchable(false).width("100px").build(),
                    new MemberColumnDescriptor.Builder(proto.senderDTO().name()).columnTitle(i18n.tr("Sender")).searchable(false).width("200px").build(),
                    new MemberColumnDescriptor.Builder(proto.date()).searchable(false).width("200px").build(),
                    new MemberColumnDescriptor.Builder(proto.subject()).searchable(false).width("1000px").build(),
                    new MemberColumnDescriptor.Builder(proto.category(), false).searchable(false).width("300px").build(),
                    new MemberColumnDescriptor.Builder(proto.category().categoryType(), false).searchableOnly().columnTitle(i18n.tr("Category")).build(),
                    new MemberColumnDescriptor.Builder(proto.allowedReply()).searchable(false).width("100px").build(),
                    new MemberColumnDescriptor.Builder(proto.thread().owner()).searchable(false).width("200px").build(),
                    new MemberColumnDescriptor.Builder(proto.ownerForList(), false).columnTitle(i18n.tr("Owner")).searchableOnly().build(),
                    new MemberColumnDescriptor.Builder(proto.status()).searchable(true).width("100px").build() };
            }//@formatter:on
        }
    }

    @Override
    protected EntityListCriteria<MessageDTO> updateCriteria(EntityListCriteria<MessageDTO> criteria) {
        com.pyx4j.site.client.backoffice.ui.prime.IPrimePane.Presenter p = getPresenter();
        AppPlace place = p.getPlace();
        Object placeCriteria = place instanceof Message ? ((Message) place).getCriteria() : null;
        CategoryType category = null;
        if (placeCriteria == null) {
            newMessage.setVisible(true);
            newTicket.setVisible(true);
            getDataTablePanel().getAddButton().setVisible(false);
            if (recipientScope != null) {
                OrCriterion senderOrRecipientCriteria = new OrCriterion(PropertyCriterion.in(criteria.proto().sender(), recipientScope), PropertyCriterion.in(
                        criteria.proto().recipients().$().recipient(), recipientScope));

                AndCriterion onBehalfCriteria = new AndCriterion(PropertyCriterion.in(criteria.proto().onBehalf(), recipientScope), PropertyCriterion.eq(
                        criteria.proto().onBehalfVisible(), true));

                criteria.or(senderOrRecipientCriteria, onBehalfCriteria);

            }
        } else {
            newMessage.setVisible(false);
            newTicket.setVisible(false);
            getDataTablePanel().getAddButton().setVisible(true);
            if (placeCriteria instanceof CategoryType) {
                getDataTablePanel().getAddButton().setCaption(i18n.tr("New") + " " + placeCriteria.toString());
                criteria.eq(criteria.proto().category().categoryType(), category = (CategoryType) placeCriteria);
            } else if (placeCriteria instanceof MessageCategory) {
                MessageCategory mc = (MessageCategory) placeCriteria;
                if (TicketType.Maintenance.equals(mc.ticketType().getValue())) {
                    getDataTablePanel().getAddButton().setVisible(false);
                } else {
                    getDataTablePanel().getAddButton().setCaption(i18n.tr("New") + " " + (category = mc.categoryType().getValue()).toString());
                }
                criteria.eq(criteria.proto().category(), mc);
            }
        }
        setDataTableModel(new DataTableModel<MessageDTO>(createColumnDescriptors(category)));

        EntityListCriteria<MessageDTO> result = super.updateCriteria(criteria);
        if (placeCriteria == null) {
            addOrIgnoreHidden(criteria);
        }
        return result;
    }

    private void addOrIgnoreHidden(EntityListCriteria<MessageDTO> criteria) {
        List<Criterion> currentCriterias = criteria.getFilters();
        boolean ignoreHidden = true;
        if (currentCriterias != null && currentCriterias.size() > 0) {
            java.util.Iterator<Criterion> i = currentCriterias.iterator();

            while (i.hasNext()) {
                Criterion criterion = i.next();
                if (criterion instanceof PropertyCriterion) {
                    PropertyCriterion propertyCriterion = (PropertyCriterion) criterion;

                    if (propertyCriterion.getPropertyPath().equals(criteria.proto().hidden().getPath().toString())) {
                        ignoreHidden = false;
                        break;
                    }
                }
            }
        }
        if (ignoreHidden) {
            criteria.notExists(criteria.proto().thread().userPolicy(), PropertyCriterion.eq(criteria.proto().thread().userPolicy().$().hidden(), true));
        } else {
            PropertyCriterion policy = criteria.getCriterion(criteria.proto().hidden());
            if (policy != null && policy.getValue() != null) {
                Boolean val = Boolean.valueOf(policy.getValue().toString());
                if (val != null && !val.booleanValue()) {
                    currentCriterias.remove(policy);
                    criteria.notExists(criteria.proto().thread().userPolicy(), PropertyCriterion.eq(criteria.proto().thread().userPolicy().$().hidden(), true));
                }
            }
        }
    }

    @Override
    protected void onItemNew() {
        com.pyx4j.site.client.backoffice.ui.prime.IPrimePane.Presenter p = getPresenter();
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
        if (recipientScope != null && recipientScope.size() > 0) {
            initData.recipients().addAll(recipientScope);
        }
        getPresenter().editNew(Message.class, initData);
    }
}
