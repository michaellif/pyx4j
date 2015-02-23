/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 */
package com.propertyvista.portal.shared.ui.communication;

import java.util.Date;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextOverflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CRichTextArea;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.form.FormDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.gwt.commons.ClientEventBus;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Toolbar;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.domain.communication.DeliveryHandle;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.CategoryType;
import com.propertyvista.dto.communication.CommunicationThreadDTO;
import com.propertyvista.dto.communication.MessageDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.shared.CommunicationStatusUpdateEvent;
import com.propertyvista.portal.shared.activity.PortalClientCommunicationManager;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.themes.CommunicationTheme;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;
import com.propertyvista.portal.shared.ui.communication.CommunicationPageView.MessagePagePresenter;

public class CommunicationPage extends CPortalEntityForm<CommunicationThreadDTO> {

    private static final I18n i18n = I18n.get(CommunicationPage.class);

    private final OpenMessageFolder messagesFolder;

    private Anchor associationLink;

    private final Button btnDelete;

    public CommunicationPage(CommunicationPageView view) {
        super(CommunicationThreadDTO.class, view, i18n.tr("Message"), ThemeColor.contrast5);
        btnDelete = new Button(i18n.tr("Delete"), new Command() {
            @Override
            public void execute() {
                MessageDialog.confirm(i18n.tr("Please confirm"), i18n.tr("Are you sure you would like to delete this message thread?"), new Command() {

                    @Override
                    public void execute() {
                        ((MessagePagePresenter) CommunicationPage.this.getView().getPresenter()).hideThread();
                    }
                });
            }
        });
        messagesFolder = new OpenMessageFolder();
        inheritEditable(false);
        inheritViewable(false);
        inheritEnabled(false);
        setEditable(true);
        setEnabled(true);
        asWidget().setStyleName(com.propertyvista.portal.shared.themes.EntityViewTheme.StyleName.EntityView.name());
        asWidget().addStyleName(CommunicationTheme.StyleName.CommunicationFolderView.name());
    }

    @Override
    public IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);
        inject(proto().allowedReply());
        inject(proto().status());
        inject(proto().category());
        CLabel<String> threadLabel = new CLabel<String>();
        threadLabel.asWidget().setStylePrimaryName(CommunicationTheme.StyleName.CommunicationThreadName.name());
        formPanel.append(Location.Left, proto().subject(), threadLabel);
        formPanel.append(Location.Left, proto().childMessages(), messagesFolder);
        formPanel.br();
        associationLink = new Anchor(i18n.tr("Associated Maintenance Request"), new Command() {

            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(
                        new ResidentPortalSiteMap.Maintenance.MaintenanceRequestPage().formPlace(getValue().associated().getPrimaryKey()));
            }
        });
        formPanel.append(Location.Left, associationLink);
        formPanel.br();

        return formPanel;
    }

    @Override
    protected FormDecorator<CommunicationThreadDTO> createDecorator() {
        FormDecorator<CommunicationThreadDTO> decorator = super.createDecorator();

        decorator.addHeaderToolbarWidget(btnDelete);
        return decorator;
    }

    private class OpenMessageFolder extends VistaBoxFolder<MessageDTO> {

        public OpenMessageFolder() {
            super(MessageDTO.class, false);
            setAddable(true);
        }

        @Override
        public VistaBoxFolderItemDecorator<MessageDTO> createItemDecorator() {
            VistaBoxFolderItemDecorator<MessageDTO> decor = super.createItemDecorator();
            decor.setCaptionFormatter(new IFormatter<MessageDTO, SafeHtml>() {
                @Override
                public SafeHtml format(MessageDTO value) {

                    Label messageField = new Label(HtmlUtils.removeHtmlTags(value.content().getValue("")));
                    messageField.getElement().getStyle().setWidth(100, Unit.PCT);
                    messageField.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
                    messageField.getElement().getStyle().setOverflow(Overflow.HIDDEN);
                    messageField.getElement().getStyle().setTextOverflow(TextOverflow.ELLIPSIS);

                    SafeHtmlBuilder loginTermsBuilder = new SafeHtmlBuilder();
                    return loginTermsBuilder
                            .appendHtmlConstant(SimpleMessageFormat.format("{0}, {1}:", value.header().sender().getValue(""), dateToGoogleStyle(value.date())))
                            .appendHtmlConstant("<br/>").appendHtmlConstant(messageField.toString()).toSafeHtml();

                }
            });

            decor.setExpended(false);
            return decor;
        }

        private String dateToGoogleStyle(IPrimitive<Date> date) {
            if (date == null || date.getValue() == null) {
                return "";
            }
            long now = System.currentTimeMillis();
            long diff = now - date.getValue().getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);
            if (diffDays > 0) {
                return date.getStringView();
            }

            return TimeUtils.simpleFormat(date.getValue(), "HH:mm");
        }

        @Override
        public void addItem() {
            super.addItem();
        }

        @Override
        public void removeItem(CFolderItem<MessageDTO> item) {
            super.removeItem(item);

        }

        @Override
        protected CForm<? extends MessageDTO> createItemForm(IObject<?> member) {
            return new MessageFolderItem();
        }

    }

    public class MessageFolderItem extends CForm<MessageDTO> {
        private Anchor btnSend;

        private Anchor btnCancel;

        private Anchor btnMarkAsUnread;

        private Anchor btnReply;

        Image starImage;

        Image highImportnaceImage;

        Toolbar statusToolBar;

        Widget attachmentCaption;

        Widget attachmentBr;

        MessageAttachmentFolder attachemnts;

        public MessageFolderItem() {
            super(MessageDTO.class, new VistaViewersComponentFactory());
            inheritEditable(false);
            inheritViewable(false);
            inheritEnabled(false);
        }

        @Override
        public IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            inject(proto().star());
            inject(proto().isRead());
            inject(proto().thread());
            inject(proto().thread().allowedReply());
            inject(proto().isInRecipients());

            FlowPanel detailsPanel = new FlowPanel();
            detailsPanel.getElement().getStyle().setPosition(Position.RELATIVE);

            statusToolBar = new Toolbar();
            statusToolBar.getElement().getStyle().setPosition(Position.ABSOLUTE);
            statusToolBar.getElement().getStyle().setRight(0, Unit.PX);
            statusToolBar.getElement().getStyle().setTop(0, Unit.PX);

            starImage = new Image(PortalImages.INSTANCE.noStar());
            starImage.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    MessageDTO m = getValue();
                    m.star().setValue(!m.star().getValue(false));
                    if (m.star().getValue(false)) {
                        starImage.setResource(PortalImages.INSTANCE.fullStar());
                    } else {
                        starImage.setResource(PortalImages.INSTANCE.noStar());
                    }
                    ((MessagePagePresenter) CommunicationPage.this.getView().getPresenter()).saveMessageItem(new DefaultAsyncCallback<MessageDTO>() {

                        @Override
                        public void onSuccess(MessageDTO result) {
                        }
                    }, m);
                }

            });

            highImportnaceImage = new Image(PortalImages.INSTANCE.messageImportance());

            statusToolBar.addItem(highImportnaceImage);
            statusToolBar.addItem(starImage);
            detailsPanel.add(statusToolBar);

            CField<?, ?> headerPanel = inject(proto().header());
            detailsPanel.add(headerPanel);

            formPanel.append(Location.Left, detailsPanel);
            formPanel.append(Location.Left, proto().highImportance(), new CCheckBox()).decorate();
            formPanel.hr();

            formPanel.append(Location.Left, proto().content(), new CRichTextArea());

            attachmentBr = formPanel.br();
            attachmentCaption = formPanel.h3("Attachments");
            formPanel.append(Location.Left, proto().attachments(), attachemnts = new MessageAttachmentFolder());
            formPanel.br();
            formPanel.append(Location.Left, createLowerToolbar());

            get(proto().content()).asWidget().getElement().getStyle().setWidth(100, Unit.PCT);
            get(proto().header()).asWidget().getElement().getStyle().setWidth(100, Unit.PCT);

            return formPanel;
        }

        public void setFocusForEditingText() {
            get(proto().content()).asWidget().getElement().focus();
        }

        protected Toolbar createLowerToolbar() {
            Toolbar tb = new Toolbar();

            btnSend = new Anchor(i18n.tr("Send"), new Command() {
                @Override
                public void execute() {
                    if (!isValid()) {
                        setVisited(true);
                        MessageDialog.error(i18n.tr("Error"), getValidationResults().getValidationMessage(true));
                    } else {
                        ((MessagePagePresenter) CommunicationPage.this.getView().getPresenter()).saveMessageItem(new DefaultAsyncCallback<MessageDTO>() {
                            @Override
                            public void onSuccess(MessageDTO result) {
                                AppSite.getPlaceController().goTo(new PortalSiteMap.Message.MessageView());
                            }
                        }, getValue());

                    }
                };
            });

            btnReply = new Anchor(i18n.tr("Reply"), new Command() {
                @Override
                public void execute() {
                    if (!isValid()) {
                        setVisited(true);
                        MessageDialog.error(i18n.tr("Error"), getValidationResults().getValidationMessage(true));
                    } else {
                        String text = buildReplyForwardText(false);
                        MessageDTO currentMessage = getCurrent();
                        messagesFolder.addItem();
                        CFolderItem<MessageDTO> newItem = messagesFolder.getItem(messagesFolder.getItemCount() - 1);
                        newItem.getValue().content().setValue(text);
                        if (!MessageCategory.CategoryType.Ticket.equals(CommunicationPage.this.getValue().category().getValue())) {
                            if (!ClientContext.getUserVisit().getName().equals(currentMessage.header().sender().getValue())) {
                                DeliveryHandle dh = EntityFactory.create(DeliveryHandle.class);
                                dh.isRead().setValue(false);
                                dh.star().setValue(false);
                                dh.recipient().set(currentMessage.senderDTO().endpoint());

                                newItem.getValue().recipients().add(dh);
                            }
                        }
                        for (int i = 0; i < messagesFolder.getItemCount(); i++) {
                            ((MessageFolderItem) messagesFolder.getItem(i).getEntityForm()).setCanReply(false);
                        }
                        newItem.refresh(false);
                        newItem.asWidget().getElement().scrollIntoView();
                        CForm<MessageDTO> form = newItem.getEntityForm();
                        if (form != null && form instanceof MessageFolderItem) {
                            ((MessageFolderItem) form).setFocusForEditingText();
                        }
                    }
                }

            });

            btnCancel = new Anchor(i18n.tr("Cancel"), new Command() {
                @Override
                public void execute() {
                    ((OpenMessageFolder) getParent().getParent()).removeItem((CFolderItem<MessageDTO>) getParent());
                    for (int i = 0; i < messagesFolder.getItemCount(); i++) {
                        ((MessageFolderItem) messagesFolder.getItem(i).getEntityForm()).setCanReply(true);
                    }
                }
            });

            btnMarkAsUnread = new Anchor(i18n.tr("Mark as unread"), new Command() {
                @Override
                public void execute() {
                    MessageDTO m = getValue();

                    m.isRead().setValue(false);
                    ((MessagePagePresenter) CommunicationPage.this.getView().getPresenter()).saveMessageItem(new DefaultAsyncCallback<MessageDTO>() {
                        @Override
                        public void onSuccess(MessageDTO result) {
                            ClientEventBus.fireEvent(new CommunicationStatusUpdateEvent(PortalClientCommunicationManager.instance()
                                    .getLatestCommunicationNotification()));
                            AppSite.getPlaceController().goTo(new PortalSiteMap.Message.MessageView());
                        }
                    }, m);
                }
            });

            tb.addItem(btnSend);
            tb.addItem(btnReply);
            tb.addItem(btnCancel);
            tb.addItem(btnMarkAsUnread);
            btnSend.setVisible(false);
            btnCancel.setVisible(false);
            btnMarkAsUnread.setVisible(false);

            return tb;
        }

        private String buildReplyForwardText(boolean isForward) {
            CFolderItem<MessageDTO> current = (CFolderItem<MessageDTO>) getParent();
            if (isForward) {
                return current == null ? null : "\nFwd:\n" + current.getValue().content().getValue("");
            }
            return current == null ? null : "\nRe:\n" + current.getValue().content().getValue("");
        }

        private MessageDTO getCurrent() {
            CFolderItem<MessageDTO> current = (CFolderItem<MessageDTO>) getParent();
            return current.getValue();
        }

        @Override
        protected MessageDTO preprocessValue(MessageDTO value, boolean fireEvent, boolean populate) {

            if (value != null && value.getPrimaryKey() != null && !value.getPrimaryKey().isDraft()) {
                if (!value.isRead().getValue(true) && value.isInRecipients().getValue(false)) {
                    value.isRead().setValue(true);
                    BoxFolderItemDecorator<DeliveryHandle> d = (BoxFolderItemDecorator<DeliveryHandle>) getParent().getDecorator();
                    d.setExpended(true);
                    ((MessagePagePresenter) CommunicationPage.this.getView().getPresenter()).saveMessageItem(new DefaultAsyncCallback<MessageDTO>() {
                        @Override
                        public void onSuccess(MessageDTO result) {
                            ClientEventBus.fireEvent(new CommunicationStatusUpdateEvent(PortalClientCommunicationManager.instance()
                                    .getLatestCommunicationNotification()));
                        }
                    }, value);
                }

            } else {
                value.isRead().setValue(false);
                if (!value.thread().hasValues()) {
                    value.thread().set(CommunicationPage.this.getValue());
                }
            }
            return super.preprocessValue(value, fireEvent, populate);
        }

        protected void setCanReply(boolean canReply) {
            boolean isNew = getValue().isPrototype() || getValue().date() == null || getValue().date().isNull();
            btnReply.setVisible(canReply && CommunicationPage.this.getValue().allowedReply().getValue(true) && !isNew);
            btnMarkAsUnread.setVisible(canReply && !isNew && getValue().isInRecipients().getValue(false));
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);
            if (getValue().isPrototype() || getValue().date() == null || getValue().date().isNull()) {
                BoxFolderItemDecorator<MessageDTO> d = (BoxFolderItemDecorator<MessageDTO>) getParent().getDecorator();
                d.setExpended(true);
                setViewable(false);
                setEditable(true);
                setEnabled(true);
                btnSend.setVisible(true);
                btnCancel.setVisible(true);
                btnReply.setVisible(false);
                starImage.setVisible(false);
                btnMarkAsUnread.setVisible(false);
                get(proto().star()).setVisible(false);
                attachmentCaption.setVisible(false);
                attachmentBr.setVisible(false);
                attachemnts.setVisible(true);
                highImportnaceImage.setVisible(false);
                get(proto().highImportance()).setVisible(true);
                statusToolBar.asWidget().setVisible(false);
                associationLink.setVisible(false);
            } else {
                setViewable(true);
                setEditable(false);
                setEnabled(false);
                btnSend.setVisible(false);
                btnCancel.setVisible(false);
                btnReply.setVisible(CommunicationPage.this.getValue().allowedReply().getValue(true));
                get(proto().star()).setVisible(getValue().isInRecipients().getValue(false));
                btnMarkAsUnread.setVisible(getValue().isInRecipients().getValue(false));
                starImage.setVisible(getValue().isInRecipients().getValue(false));
                starImage.setResource(get(proto().star()).getValue() ? PortalImages.INSTANCE.fullStar() : (PortalImages.INSTANCE.noStar()));

                attachmentCaption.setVisible(getValue().attachments().size() > 0);
                attachmentBr.setVisible(getValue().attachments().size() > 0);
                attachemnts.setVisible(getValue().attachments().size() > 0);
                highImportnaceImage.setVisible(getValue().highImportance().getValue(false));
                get(proto().highImportance()).setVisible(false);
                statusToolBar.asWidget().setVisible(starImage.isVisible() || highImportnaceImage.isVisible());
                associationLink.setVisible(CategoryType.Ticket.equals(CommunicationPage.this.getValue().category().categoryType().getValue())
                        && CommunicationPage.this.getValue().associated() != null && !CommunicationPage.this.getValue().associated().isNull());
            }
        }
    }
}
