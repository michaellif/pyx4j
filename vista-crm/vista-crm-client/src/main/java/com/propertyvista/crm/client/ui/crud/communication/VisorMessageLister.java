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
 * @version $Id: VisorMessageLister.java 21572 2014-12-13 16:48:45Z michaellif $
 */
package com.propertyvista.crm.client.ui.crud.communication;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.AndCriterion;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView.IPrimePanePresenter;
import com.pyx4j.site.client.ui.SiteDataTablePanel;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.rpc.CrmSiteMap.Communication.Message;
import com.propertyvista.crm.rpc.services.MessageCrudService;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.CategoryType;
import com.propertyvista.domain.communication.MessageCategory.TicketType;
import com.propertyvista.dto.MessageDTO;

public class VisorMessageLister extends SiteDataTablePanel<MessageDTO> {
    private static final I18n i18n = I18n.get(VisorMessageLister.class);

    private Button newMessage;

    private Button newTicket;

    private final IPrimePanePresenter presenter;

    private List<? extends CommunicationEndpoint> recipientScope;

    public VisorMessageLister(IPrimePanePresenter presenter, List<? extends CommunicationEndpoint> recipientScope) {
        this(presenter);
        this.recipientScope = recipientScope;
    }

    public VisorMessageLister(IPrimePanePresenter presenter) {
        super(MessageDTO.class, GWT.<AbstractListCrudService<MessageDTO>> create(MessageCrudService.class), true);

        this.presenter = presenter;

        setColumnDescriptors(createColumnDescriptors(CategoryType.Message));
        setDataTableModel(new DataTableModel<MessageDTO>());

        setFilteringEnabled(true);
        // No sorting work for it
        getDataTable().setHasColumnClickSorting(false);

        addUpperActionItem(newMessage = new Button(FolderImages.INSTANCE.addIcon(), i18n.tr("New Message"), new Command() {
            @Override
            public void execute() {
                editNewEntity(CategoryType.Message);
            }
        }));
        addUpperActionItem(newTicket = new Button(FolderImages.INSTANCE.addIcon(), i18n.tr("New Ticket"), new Command() {
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
                    new ColumnDescriptor.Builder(proto.star()).searchable(false).width("30px").formatter(
                            booleanField2Image(proto.star().getPath(),CrmImages.INSTANCE.fullStar(), CrmImages.INSTANCE.noStar()))
                            .columnTitleShown(false).build(),
                    new ColumnDescriptor.Builder(proto.highImportance()).searchable(false).width("30px").formatter(
                            booleanField2Image( proto.highImportance().getPath(),CrmImages.INSTANCE.messageImportant(), null))
                            .columnTitleShown(false).build(),
                    new ColumnDescriptor.Builder(proto.hasAttachments()).searchable(false).width("30px").formatter(
                            booleanField2Image( proto.hasAttachments().getPath(),CrmImages.INSTANCE.attachement(), null))
                            .columnTitleShown(false).build(),
                    new ColumnDescriptor.Builder(proto.senderDTO().name()).columnTitle(i18n.tr("Sender")).searchable(false).width("150px").formatter(baseFieldViewOnIsRead(proto.senderDTO().name().getPath())).build(),
                    new ColumnDescriptor.Builder(proto.subject()).searchable(false).width("300px").formatter(baseFieldViewOnIsRead(proto.subject().getPath())).build(),
                    new ColumnDescriptor.Builder(proto.date()).searchable(false).width("80px").build(),
                    new ColumnDescriptor.Builder(proto.deliveryMethod()).searchable(true).build(),
                    new ColumnDescriptor.Builder(proto.category(), false).searchable(false).build(),
                    new ColumnDescriptor.Builder(proto.category().categoryType(), false).searchableOnly().columnTitle(i18n.tr("Category")).build(),
                    new ColumnDescriptor.Builder(proto.allowedReply()).searchable(false).width("100px").build(),
                    new ColumnDescriptor.Builder(proto.thread().owner()).searchable(false).width("150px").build(),
                    new ColumnDescriptor.Builder(proto.ownerForList(), false).columnTitle(i18n.tr("Owner")).searchableOnly().build(),
                    new ColumnDescriptor.Builder(proto.status()).searchable(true).width("100px").build(),
                    new ColumnDescriptor.Builder(proto.hidden(), false).searchableOnly().columnTitle(i18n.tr("Hidden")).build()};
        }
        else {
        switch (category) {
        case Ticket:
            return new ColumnDescriptor[] {//@formatter:off
                new ColumnDescriptor.Builder(proto.star()).searchable(false).width("25px").formatter(
                        booleanField2Image(proto.star().getPath(),CrmImages.INSTANCE.fullStar(), CrmImages.INSTANCE.noStar()))
                        .columnTitleShown(false).build(),
                new ColumnDescriptor.Builder(proto.highImportance()).searchable(false).width("25px").formatter(
                        booleanField2Image( proto.highImportance().getPath(),CrmImages.INSTANCE.messageImportant(), null))
                        .columnTitleShown(false).build(),
                new ColumnDescriptor.Builder(proto.hasAttachments()).searchable(false).width("25px").formatter(
                        booleanField2Image( proto.hasAttachments().getPath(),CrmImages.INSTANCE.attachement(), null))
                        .columnTitleShown(false).build(),
                new ColumnDescriptor.Builder(proto.senderDTO().name()).columnTitle(i18n.tr("Sender")).searchable(false).width("200px").formatter(baseFieldViewOnIsRead(proto.senderDTO().name().getPath())).build(),
                new ColumnDescriptor.Builder(proto.subject()).searchable(false).width("1000px").formatter(baseFieldViewOnIsRead(proto.subject().getPath())).build(),
                new ColumnDescriptor.Builder(proto.date()).searchable(false).width("200px").build(),
                new ColumnDescriptor.Builder(proto.category(), false).searchable(false).width("300px").build(),
                new ColumnDescriptor.Builder(proto.category().categoryType(), false).searchableOnly().columnTitle(i18n.tr("Category")).build(),
                new ColumnDescriptor.Builder(proto.allowedReply()).searchable(false).width("100px").build(),
                new ColumnDescriptor.Builder(proto.thread().owner()).searchable(false).width("200px").build(),
                new ColumnDescriptor.Builder(proto.ownerForList(), false).columnTitle(i18n.tr("Owner")).searchableOnly().build(),
                new ColumnDescriptor.Builder(proto.status()).searchable(true).width("100px").build() };
        case Message:
            return new ColumnDescriptor[] {//@formatter:off
                new ColumnDescriptor.Builder(proto.star()).searchable(false).width("25px").formatter(
                        booleanField2Image(proto.star().getPath(),CrmImages.INSTANCE.fullStar(), CrmImages.INSTANCE.noStar()))
                        .columnTitleShown(false).build(),
                new ColumnDescriptor.Builder(proto.highImportance()).searchable(false).width("25px").formatter(
                        booleanField2Image( proto.highImportance().getPath(),CrmImages.INSTANCE.messageImportant(), null))
                        .columnTitleShown(false).build(),
                new ColumnDescriptor.Builder(proto.hasAttachments()).searchable(false).width("25px").formatter(
                        booleanField2Image( proto.hasAttachments().getPath(),CrmImages.INSTANCE.attachement(), null))
                        .columnTitleShown(false).build(),
                new ColumnDescriptor.Builder(proto.senderDTO().name()).columnTitle(i18n.tr("Sender")).searchable(false).width("200px").formatter(baseFieldViewOnIsRead(proto.senderDTO().name().getPath())).build(),
                new ColumnDescriptor.Builder(proto.subject()).searchable(false).width("1000px").formatter(baseFieldViewOnIsRead(proto.subject().getPath())).build(),
                new ColumnDescriptor.Builder(proto.date()).searchable(false).width("200px").build(),
                new ColumnDescriptor.Builder(proto.deliveryMethod()).searchable(true).width("300px").build(),
                new ColumnDescriptor.Builder(proto.category(), false).searchable(false).width("300px").build(),
                new ColumnDescriptor.Builder(proto.category().categoryType(), false).searchableOnly().columnTitle(i18n.tr("Category")).build(),
                new ColumnDescriptor.Builder(proto.allowedReply()).searchable(false).width("100px").build() };
     //@formatter:on
            default:
                return new ColumnDescriptor[] {//@formatter:off
                    new ColumnDescriptor.Builder(proto.star()).searchable(false).width("25px").formatter(
                            booleanField2Image(proto.star().getPath(),CrmImages.INSTANCE.fullStar(), CrmImages.INSTANCE.noStar()))
                            .columnTitleShown(false).build(),
                    new ColumnDescriptor.Builder(proto.highImportance()).searchable(false).width("25px").formatter(
                            booleanField2Image( proto.highImportance().getPath(),CrmImages.INSTANCE.messageImportant(), null))
                            .columnTitleShown(false).build(),
                    new ColumnDescriptor.Builder(proto.hasAttachments()).searchable(false).width("25px").formatter(
                            booleanField2Image( proto.hasAttachments().getPath(),CrmImages.INSTANCE.attachement(), null))
                            .columnTitleShown(false).build(),
                    new ColumnDescriptor.Builder(proto.senderDTO().name()).columnTitle(i18n.tr("Sender")).searchable(false).width("200px").formatter(baseFieldViewOnIsRead(proto.senderDTO().name().getPath())).build(),
                    new ColumnDescriptor.Builder(proto.subject()).searchable(false).width("1000px").formatter(baseFieldViewOnIsRead(proto.subject().getPath())).build(),
                    new ColumnDescriptor.Builder(proto.date()).searchable(false).width("200px").build(),
                    new ColumnDescriptor.Builder(proto.deliveryMethod()).searchable(true).width("300px").build(),
                    new ColumnDescriptor.Builder(proto.category(), false).searchable(false).width("300px").build(),
                    new ColumnDescriptor.Builder(proto.category().categoryType(), false).searchableOnly().columnTitle(i18n.tr("Category")).build(),
                    new ColumnDescriptor.Builder(proto.allowedReply()).searchable(false).width("100px").build(),
                    new ColumnDescriptor.Builder(proto.thread().owner()).searchable(false).width("200px").build(),
                    new ColumnDescriptor.Builder(proto.ownerForList(), false).columnTitle(i18n.tr("Owner")).searchableOnly().build(),
                    new ColumnDescriptor.Builder(proto.status()).searchable(true).width("100px").build() };
            }//@formatter:on
        }
    }

    @Override
    protected EntityListCriteria<MessageDTO> updateCriteria(EntityListCriteria<MessageDTO> criteria) {
        AppPlace place = presenter.getPlace();
        Object placeCriteria = place instanceof Message ? ((Message) place).getCriteria() : null;
        CategoryType category = null;
        if (placeCriteria == null) {
            newMessage.setVisible(true);
            newTicket.setVisible(true);
            setAddNewActionEnabled(false);
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
            setAddNewActionEnabled(true);
            if (placeCriteria instanceof CategoryType) {
                setAddNewActionCaption(i18n.tr("New") + " " + placeCriteria.toString());
                criteria.eq(criteria.proto().category().categoryType(), category = (CategoryType) placeCriteria);
            } else if (placeCriteria instanceof MessageCategory) {
                MessageCategory mc = (MessageCategory) placeCriteria;
                if (TicketType.Maintenance.equals(mc.ticketType().getValue())) {
                    setAddNewActionEnabled(false);
                } else {
                    setAddNewActionCaption(i18n.tr("New") + " " + (category = mc.categoryType().getValue()).toString());
                }
                criteria.eq(criteria.proto().category(), mc);
            }
        }

        setColumnDescriptors(createColumnDescriptors(category));
        setDataTableModel(new DataTableModel<MessageDTO>());

        EntityListCriteria<MessageDTO> result = super.updateCriteria(criteria);
        if (placeCriteria == null) {
            addOrIgnoreHidden(criteria);
        }
        return result;
    }

    private static IFormatter<IEntity, SafeHtml> booleanField2Image(final Path path, final ImageResource trueValueResource,
            final ImageResource falseValueResource) {
        return new IFormatter<IEntity, SafeHtml>() {
            @Override
            public SafeHtml format(IEntity value) {
                SafeHtmlBuilder builder = new SafeHtmlBuilder();
                Boolean v = (Boolean) value.getMember(path).getValue();
                if (v != null && v.booleanValue()) {
                    builder.appendHtmlConstant(new Image(trueValueResource).toString());
                } else if (falseValueResource != null) {
                    builder.appendHtmlConstant(new Image(falseValueResource).toString());
                }

                return builder.toSafeHtml();
            }
        };
    }

    private static IFormatter<IEntity, SafeHtml> baseFieldViewOnIsRead(final Path path) {
        return new IFormatter<IEntity, SafeHtml>() {
            @Override
            public SafeHtml format(IEntity value) {
                SafeHtmlBuilder builder = new SafeHtmlBuilder();
                if (value != null) {
                    MessageDTO v = (MessageDTO) value;
                    Boolean isRead = v.isRead().getValue();
                    String s = value.getMember(path).getValue().toString();
                    if (!CategoryType.Ticket.equals(v.category().categoryType().getValue()) && (isRead == null || !isRead.booleanValue())) {
                        Label messageField = new Label(s);
                        messageField.getElement().getStyle().setFontWeight(FontWeight.BOLD);
                        builder.appendHtmlConstant(messageField.toString());
                    } else {
                        builder.appendHtmlConstant(s);
                    }
                }
                return builder.toSafeHtml();
            }
        };
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
        AppPlace place = presenter.getPlace();
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
        editNew(Message.class, initData);
    }
}
