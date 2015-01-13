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
 * @author igors
 */
package com.propertyvista.crm.client.ui.crud.communication;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.site.client.ui.SiteDataTablePanel;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.rpc.CrmSiteMap.Communication.Message;
import com.propertyvista.crm.rpc.services.MessageCrudService;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.CategoryType;
import com.propertyvista.domain.communication.MessageCategory.TicketType;
import com.propertyvista.domain.communication.SpecialDelivery.DeliveryMethod;
import com.propertyvista.dto.MessageDTO;
import com.propertyvista.dto.MessageDTO.ViewScope;

public class MessageLister extends SiteDataTablePanel<MessageDTO> {
    private static final I18n i18n = I18n.get(MessageLister.class);

    private Button newButton;

    private MenuItem newMessage;

    private MenuItem newTicket;

    private MenuItem newIVR;

    private MenuItem newSMS;

    private MenuItem newNotification;

    private final MessageListerView view;

    protected MessageDTO messageDTO;

    public MessageLister(MessageListerView view) {
        super(MessageDTO.class, GWT.<AbstractCrudService<MessageDTO>> create(MessageCrudService.class), true);

        this.view = view;

        setColumnDescriptors(createColumnDescriptors(CategoryType.Message));
        setDataTableModel(new DataTableModel<MessageDTO>());

        setFilteringEnabled(true);
        // No sorting work for it
        getDataTable().setHasColumnClickSorting(false);
        setAddNewActionCaption(i18n.tr("New") + " " + CategoryType.Ticket.toString());
        setAddNewActionEnabled(false);

        addUpperActionItem(newButton = new Button(FolderImages.INSTANCE.addIcon(), i18n.tr("New..."), new Command() {
            @Override
            public void execute() {
            }
        }));

        Button.ButtonMenuBar subMenu = new Button.ButtonMenuBar();
        subMenu.addItem(newTicket = new MenuItem(i18n.tr("Ticket"), new Command() {
            @Override
            public void execute() {
                editNewEntity(CategoryType.Ticket, null);
            }
        }));
        subMenu.addItem(newMessage = new MenuItem(i18n.tr("Message"), new Command() {
            @Override
            public void execute() {
                editNewEntity(CategoryType.Message, null);
            }
        }));
        subMenu.addItem(newIVR = new MenuItem(i18n.tr("IVR"), new Command() {
            @Override
            public void execute() {
                editNewEntity(CategoryType.Message, DeliveryMethod.IVR);
            }
        }));
        subMenu.addItem(newSMS = new MenuItem(i18n.tr("SMS"), new Command() {
            @Override
            public void execute() {
                editNewEntity(CategoryType.Message, DeliveryMethod.SMS);
            }
        }));
        subMenu.addItem(newNotification = new MenuItem(i18n.tr("Notification"), new Command() {
            @Override
            public void execute() {
                editNewEntity(CategoryType.Message, DeliveryMethod.Notification);
            }
        }));
        newButton.setMenu(subMenu);
        newButton.setPermission(DataModelPermission.permissionCreate(MessageDTO.class));
    }

    public static List<ColumnDescriptor> createColumnDescriptors(CategoryType category) {
        final MessageDTO proto = EntityFactory.getEntityPrototype(MessageDTO.class);

        //@formatter:off
        List<ColumnDescriptor> columns = new ArrayList<>();
        columns.add( new ColumnDescriptor.Builder(proto.star()).searchable(false).width("27px").formatter(
                booleanField2Image(proto.star().getPath(),CrmImages.INSTANCE.fullStar(), CrmImages.INSTANCE.noStar()))
                .columnTitleShown(false).build());

        columns.add(new ColumnDescriptor.Builder(proto.highImportance()).searchable(false).width("27px").formatter(
                booleanField2Image( proto.highImportance().getPath(),CrmImages.INSTANCE.messageImportant(), null))
                .columnTitleShown(false).build());
        columns.add(new ColumnDescriptor.Builder(proto.hasAttachments()).searchable(false).width("27px").formatter(
                booleanField2Image( proto.hasAttachments().getPath(),CrmImages.INSTANCE.attachement(), null))
                .columnTitleShown(false).build());
        columns.add(new ColumnDescriptor.Builder(proto.thread().id()).columnTitle(i18n.tr("Ticket Id")).searchable(true).width("100px").formatter(showHideId()).build());
        columns.add(new ColumnDescriptor.Builder(proto.senderDTO().name()).columnTitle(i18n.tr("Sender")).searchable(false).width("200px").formatter(baseFieldViewOnIsRead(proto.senderDTO().name().getPath())).build());
        columns.add(new ColumnDescriptor.Builder(proto.subject()).searchable(false).width("600px").formatter(baseFieldViewOnIsRead(proto.subject().getPath())).build());
        columns.add(new ColumnDescriptor.Builder(proto.date()).formatter(baseFieldViewOnIsRead(proto.date().getPath())).searchable(false).width("100px").build());

        if (category != CategoryType.Ticket) {
            columns.add(new ColumnDescriptor.Builder(proto.deliveryMethod()).width("70px").searchable(true).build());
        }

        columns.add(new ColumnDescriptor.Builder(proto.category(), false).searchable(false).build());
        columns.add(new ColumnDescriptor.Builder(proto.category().categoryType(), false).searchableOnly().columnTitle(i18n.tr("Category")).build());
        columns.add(new ColumnDescriptor.Builder(proto.allowedReply()).searchable(false).width("70px").build());

        if (category != CategoryType.Message) {
            columns.add(new ColumnDescriptor.Builder(proto.thread().owner()).searchable(false).width("200px").build());
            columns.add(new ColumnDescriptor.Builder(proto.ownerForList(), false).columnTitle(i18n.tr("Owner")).searchableOnly().build());
            columns.add(new ColumnDescriptor.Builder(proto.status()).searchable(true).width("70px").build());
        }
        if (category == null) {
            columns.add(new ColumnDescriptor.Builder(proto.hidden(), false).searchableOnly().columnTitle(i18n.tr("Hidden")).build());
        }

        return columns;
    }

    @Override
    protected EntityListCriteria<MessageDTO> updateCriteria(EntityListCriteria<MessageDTO> criteria) {
        AppPlace place = view.getPresenter().getPlace();
        Object placeCriteria = place instanceof Message ? ((Message) place).getCriteria() : null;
        CategoryType category = null;
        newButton.setVisible(true);
        setAddNewActionEnabled(false);
        if (placeCriteria == null) {
            newMessage.setVisible(true);
            newTicket.setVisible(true);
            newIVR.setVisible(true);
            newSMS.setVisible(true);
            newNotification.setVisible(true);

            criteria.eq(criteria.proto().viewScope(), ViewScope.Direct);

        } else {
            if (placeCriteria instanceof CategoryType) {
                criteria.eq(criteria.proto().category().categoryType(), category = (CategoryType) placeCriteria);
            } else if (placeCriteria instanceof MessageCategory) {
                MessageCategory mc = (MessageCategory) placeCriteria;
                if (TicketType.Maintenance.equals(mc.ticketType().getValue())) {
                    newButton.setVisible(false);
                } else {
                    category = mc.categoryType().getValue();
                }
                criteria.eq(criteria.proto().category(), mc);
            } else if (placeCriteria instanceof UserVisit) {
                criteria.eq(criteria.proto().category().categoryType(), category = CategoryType.Ticket);
                criteria.eq(criteria.proto().category().dispatchers().$().user(), ((UserVisit) placeCriteria).getPrincipalPrimaryKey());
                criteria.eq(criteria.proto().viewScope(), ViewScope.Dispatched);
                setColumnDescriptors(createColumnDescriptors(category));
            }

            if (category != null) {
                setAddNewActionEnabled(CategoryType.Message != category && !(placeCriteria instanceof UserVisit));
                newButton.setVisible(CategoryType.Message == category);
                newMessage.setVisible(CategoryType.Message == category);
                newTicket.setVisible(false);
                newIVR.setVisible(CategoryType.Message == category);
                newSMS.setVisible(CategoryType.Message == category);
                newNotification.setVisible(CategoryType.Message == category);
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
                    Label messageField = new Label(s);
                    if (v.hidden().getValue(false)) {
                        messageField.getElement().getStyle().setTextDecoration(TextDecoration.LINE_THROUGH);
                    }
                    if (!CategoryType.Ticket.equals(v.category().categoryType().getValue()) && (isRead == null || !isRead.booleanValue())) {
                        messageField.getElement().getStyle().setFontWeight(FontWeight.BOLD);
                    } else if (CategoryType.Ticket.equals(v.category().categoryType().getValue()) && v.isDirect().getValue(false).booleanValue() && (isRead == null || !isRead.booleanValue())) {
                        messageField.getElement().getStyle().setFontWeight(FontWeight.BOLD);
                    }

                    builder.appendHtmlConstant(messageField.toString());

                }
                return builder.toSafeHtml();
            }
        };
    }


    private static IFormatter<IEntity, SafeHtml> showHideId() {
        return new IFormatter<IEntity, SafeHtml>() {
            @Override
            public SafeHtml format(IEntity value) {
                SafeHtmlBuilder builder = new SafeHtmlBuilder();
                if (value != null) {
                    MessageDTO v = (MessageDTO) value;
                    Boolean isRead = v.isRead().getValue();
                    String s = v.thread().id().getStringView();
                    Label messageField = CategoryType.Ticket.equals(v.category().categoryType().getValue()) ? new Label(s):new Label("");
                    if (v.hidden().getValue(false)) {
                        messageField.getElement().getStyle().setTextDecoration(TextDecoration.LINE_THROUGH);
                    }
                    if (!CategoryType.Ticket.equals(v.category().categoryType().getValue()) && (isRead == null || !isRead.booleanValue())) {
                        messageField.getElement().getStyle().setFontWeight(FontWeight.BOLD);
                    } else if (CategoryType.Ticket.equals(v.category().categoryType().getValue()) && v.isDirect().getValue(false).booleanValue() && (isRead == null || !isRead.booleanValue())) {
                        messageField.getElement().getStyle().setFontWeight(FontWeight.BOLD);
                    }

                    builder.appendHtmlConstant(messageField.toString());

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
        AppPlace place = view.getPresenter().getPlace();
        Object placeCriteria = place instanceof Message ? ((Message) place).getCriteria() : null;
        editNewEntity(placeCriteria, null);
    }

    private void editNewEntity(Object placeCriteria, DeliveryMethod deliveryMethod) {
        MessageCrudService.MessageInitializationData initData = EntityFactory.create(MessageCrudService.MessageInitializationData.class);
        if (placeCriteria == null) {
            initData.categoryType().setValue(null);
        } else if (placeCriteria instanceof MessageCategory) {
            initData.messageCategory().set((MessageCategory) placeCriteria);
        } else if (placeCriteria instanceof CategoryType) {
            initData.categoryType().setValue((CategoryType) placeCriteria);
        }
        initData.deliveryMethod().setValue(deliveryMethod);
        editNew(Message.class, initData);
    }
}
