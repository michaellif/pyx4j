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
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.communication;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Toolbar;
import com.pyx4j.widgets.client.dialog.DefaultDialogTheme;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.communication.CommunicationThread.ThreadStatus;
import com.propertyvista.domain.communication.DeliveryHandle;
import com.propertyvista.portal.resident.ui.communication.MessagePageView.MessagePagePresenter;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.communication.MessageDTO;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;
import com.propertyvista.portal.shared.ui.PortalFormPanel;

public class MessagePage extends CPortalEntityForm<MessageDTO> {

    private static final I18n i18n = I18n.get(MessagePage.class);

    private final OpenMessageFolder messagesFolder;

    public MessagePage(MessagePageView view) {
        super(MessageDTO.class, view, "Message", ThemeColor.contrast5);

        messagesFolder = new OpenMessageFolder();
        inheritEditable(false);
        inheritViewable(false);
        inheritEnabled(false);
        setEditable(true);
        setEnabled(true);
        asWidget().setStyleName(com.propertyvista.portal.shared.themes.EntityViewTheme.StyleName.EntityView.name());
    }

    @Override
    public IsWidget createContent() {
        PortalFormPanel formPanel = new PortalFormPanel(this);
        inject(proto().thread());
        inject(proto().allowedReply());
        formPanel.append(Location.Left, proto().subject(), new CLabel<String>()).decorate();
        formPanel.append(Location.Left, proto().status(), new CLabel<String>()).decorate();
        formPanel.append(Location.Left, proto().content(), messagesFolder);
        formPanel.br();

        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        if (getValue() == null || getValue().isPrototype()) {
            get(proto().status()).setVisible(false);
        } else {
            get(proto().status()).setVisible(!ThreadStatus.Unassigned.equals(getValue().status().getValue()));
        }
    }

    private class OpenMessageFolder extends VistaBoxFolder<MessageDTO> {

        public OpenMessageFolder() {
            super(MessageDTO.class, false);
            setAddable(true);
        }

        @Override
        public BoxFolderItemDecorator<MessageDTO> createItemDecorator() {
            BoxFolderItemDecorator<MessageDTO> decor = (BoxFolderItemDecorator<MessageDTO>) super.createItemDecorator();
            decor.setExpended(false);
            return decor;
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
            return new MessageFolderItem(this);
        }

    }

    public class MessageFolderItem extends CForm<MessageDTO> {
        private Anchor btnSend;

        private Anchor btnCancel;

        private Anchor btnMarkAsUnread;

        private Anchor btnForward;

        private Anchor btnReply;

        Image starImage;

        Image highImportnaceImage;

        Toolbar statusToolBar;

        Widget attachmentCaption;

        Widget attachmentBr;

        MessageAttachmentFolder attachemnts;

        private final OpenMessageFolder parent;

        public MessageFolderItem(OpenMessageFolder parent) {
            super(MessageDTO.class, new VistaViewersComponentFactory());
            inheritEditable(false);
            inheritViewable(false);
            inheritEnabled(false);
            this.parent = parent;
        }

        @Override
        public IsWidget createContent() {
            PortalFormPanel formPanel = new PortalFormPanel(this);

            inject(proto().star());
            inject(proto().isRead());
            inject(proto().thread());
            inject(proto().allowedReply());

            statusToolBar = new Toolbar();
            statusToolBar.setStylePrimaryName(DefaultDialogTheme.StyleName.DialogDefaultButtonsToolbar.name());
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
                    ((MessagePagePresenter) MessagePage.this.getView().getPresenter()).saveMessageItem(new DefaultAsyncCallback<MessageDTO>() {

                        @Override
                        public void onSuccess(MessageDTO result) {
                        }
                    }, m);
                }

            });

            highImportnaceImage = new Image(PortalImages.INSTANCE.messageImportance());

            statusToolBar.addItem(highImportnaceImage);
            statusToolBar.addItem(starImage);
            formPanel.append(Location.Dual, statusToolBar);

            formPanel.append(Location.Left, proto().header());
            formPanel.append(Location.Left, proto().highImportance(), new CCheckBox()).decorate();
            formPanel.hr();

            formPanel.append(Location.Left, proto().text());

            attachmentCaption = formPanel.h3("Attachments");
            formPanel.append(Location.Left, proto().attachments(), attachemnts = new MessageAttachmentFolder());
            attachmentBr = formPanel.br();
            formPanel.append(Location.Left, createLowerToolbar());

            get(proto().text()).asWidget().getElement().getStyle().setWidth(100, Unit.PCT);
            get(proto().header()).asWidget().getElement().getStyle().setWidth(100, Unit.PCT);

            return formPanel;
        }

        public void setFocusForEditingText() {
            get(proto().text()).asWidget().getElement().focus();
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
                        ((MessagePagePresenter) MessagePage.this.getView().getPresenter()).saveMessageItem(new DefaultAsyncCallback<MessageDTO>() {
                            @Override
                            public void onSuccess(MessageDTO result) {
                                AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Message.MessageView());
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
                        String forwardText = buildForwardText();
                        messagesFolder.addItem();
                        CFolderItem<MessageDTO> newItem = messagesFolder.getItem(messagesFolder.getItemCount() - 1);
                        newItem.getValue().text().setValue(forwardText);
                        newItem.refresh(false);
                        newItem.asWidget().getElement().scrollIntoView();
                        CForm<MessageDTO> form = newItem.getEntityForm();
                        if (form != null && form instanceof MessageFolderItem) {
                            ((MessageFolderItem) form).setFocusForEditingText();
                        }
                    }
                }

            });

            btnForward = new Anchor(i18n.tr("Forward"), new Command() {
                @Override
                public void execute() {
                    if (!isValid()) {
                        setVisited(true);
                        MessageDialog.error(i18n.tr("Error"), getValidationResults().getValidationMessage(true));
                    } else {
                        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Message.MessageWizard(buildForwardText()));
                    }
                };
            });

            btnCancel = new Anchor(i18n.tr("Cancel"), new Command() {
                @Override
                public void execute() {
                    ((OpenMessageFolder) getParent().getParent()).removeItem((CFolderItem<MessageDTO>) getParent());
                }
            });

            btnMarkAsUnread = new Anchor("Mark as unread", new Command() {
                @Override
                public void execute() {
                    MessageDTO m = getValue();

                    m.isRead().setValue(false);
                    ((MessagePagePresenter) MessagePage.this.getView().getPresenter()).saveMessageItem(new DefaultAsyncCallback<MessageDTO>() {
                        @Override
                        public void onSuccess(MessageDTO result) {
                            AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Message.MessageView());
                        }
                    }, m);
                }
            });

            tb.addItem(btnSend);
            tb.addItem(btnReply);
            tb.addItem(btnForward);
            tb.addItem(btnCancel);
            tb.addItem(btnMarkAsUnread);
            btnSend.setVisible(false);
            btnCancel.setVisible(false);
            btnMarkAsUnread.setVisible(false);

            return tb;
        }

        private String buildForwardText() {
            CFolderItem<MessageDTO> current = (CFolderItem<MessageDTO>) getParent();
            String forwardText = current == null ? null : "\nRe:\n" + current.getValue().text().getValue();
            return forwardText;
        }

        @Override
        protected MessageDTO preprocessValue(MessageDTO value, boolean fireEvent, boolean populate) {
            if (value != null && value.getPrimaryKey() != null && !value.getPrimaryKey().isDraft()) {
                if (!value.isRead().getValue() && !ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(value.sender().getPrimaryKey())) {
                    value.isRead().setValue(true);
                    BoxFolderItemDecorator<DeliveryHandle> d = (BoxFolderItemDecorator<DeliveryHandle>) getParent().getDecorator();
                    d.setExpended(true);
                    ((MessagePagePresenter) MessagePage.this.getView().getPresenter()).saveMessageItem(new DefaultAsyncCallback<MessageDTO>() {
                        @Override
                        public void onSuccess(MessageDTO result) {
                        }
                    }, value);
                }

            } else {
                value.isRead().setValue(false);
                if (!value.thread().hasValues()) {
                    value.thread().set(MessagePage.this.getValue().thread());
                }
            }
            return super.preprocessValue(value, fireEvent, populate);
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
                btnForward.setVisible(false);
                starImage.setVisible(false);
                btnMarkAsUnread.setVisible(false);
                get(proto().star()).setVisible(false);
                attachmentCaption.setVisible(false);
                attachmentBr.setVisible(false);
                attachemnts.setVisible(false);
                highImportnaceImage.setVisible(false);
                get(proto().highImportance()).setVisible(true);
                statusToolBar.asWidget().setVisible(false);
            } else {
                setViewable(true);
                setEditable(false);
                setEnabled(false);
                btnSend.setVisible(false);
                btnCancel.setVisible(false);
                btnReply.setVisible(!ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(getValue().sender().getPrimaryKey())
                        && getValue().allowedReply().getValue(true) && !ThreadStatus.Closed.equals(getValue().status().getValue()));
                btnForward.setVisible(true);
                get(proto().star()).setVisible(!ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(getValue().sender().getPrimaryKey()));
                btnMarkAsUnread.setVisible(!ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(getValue().sender().getPrimaryKey()));
                starImage.setVisible(!ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(getValue().sender().getPrimaryKey()));
                starImage.setResource(get(proto().star()).getValue() ? PortalImages.INSTANCE.fullStar() : (PortalImages.INSTANCE.noStar()));

                attachmentCaption.setVisible(getValue().attachments().size() > 0);
                attachmentBr.setVisible(getValue().attachments().size() > 0);
                attachemnts.setVisible(getValue().attachments().size() > 0);
                highImportnaceImage.setVisible(getValue().highImportance().getValue(false));
                get(proto().highImportance()).setVisible(false);
                statusToolBar.asWidget().setVisible(starImage.isVisible() || highImportnaceImage.isVisible());
            }
        }
    }
}
