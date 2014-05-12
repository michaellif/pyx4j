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

import java.util.Arrays;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComboBoxBoolean;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.form.FormDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
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
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.CommunicationMessage;
import com.propertyvista.portal.resident.ui.communication.CommunicationMessagePageView.CommunicationMessagePagePresenter;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.communication.CommunicationMessageDTO;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;
import com.propertyvista.portal.shared.ui.PortalFormPanel;

public class CommunicationMessagePage extends CPortalEntityForm<CommunicationMessageDTO> {

    private static final I18n i18n = I18n.get(CommunicationMessagePage.class);

    private final Button btnReplay;

    private final OpenMessageFolder messagesFolder;

    public CommunicationMessagePage(CommunicationMessagePageView view) {
        super(CommunicationMessageDTO.class, view, "Message", ThemeColor.contrast5);
        btnReplay = new Button(i18n.tr("Reply"), new Command() {

            @Override
            public void execute() {
                messagesFolder.addItem();
            }
        });

        messagesFolder = new OpenMessageFolder();
        inheritEditable(false);
        inheritViewable(false);
        inheritEnabled(false);
        setEditable(true);
        setEnabled(true);
        asWidget().setStyleName(com.propertyvista.portal.shared.themes.EntityViewTheme.StyleName.EntityView.name());
    }

    @Override
    protected FormDecorator<CommunicationMessageDTO> createDecorator() {
        FormDecorator<CommunicationMessageDTO> decorator = super.createDecorator();

        decorator.addFooterToolbarWidget(btnReplay);
        decorator.getFooterPanel().setVisible(true);

        return decorator;
    }

    @Override
    public IsWidget createContent() {
        PortalFormPanel formPanel = new PortalFormPanel(this);
        formPanel.append(Location.Left, proto().thread().created(), new CLabel<String>()).decorate().componentWidth(250);
        formPanel.append(Location.Left, proto().subject(), new CLabel<String>()).decorate().componentWidth(250);
        formPanel.append(Location.Left, proto().thread().content(), messagesFolder);
        formPanel.br();

        return formPanel;
    }

    private class OpenMessageFolder extends VistaBoxFolder<CommunicationMessage> {

        public OpenMessageFolder() {
            super(CommunicationMessage.class, false);
            setAddable(true);
        }

        @Override
        public BoxFolderItemDecorator<CommunicationMessage> createItemDecorator() {
            BoxFolderItemDecorator<CommunicationMessage> decor = (BoxFolderItemDecorator<CommunicationMessage>) super.createItemDecorator();
            decor.setExpended(false);
            return decor;
        }

        @Override
        public void addItem() {
            super.addItem();
        }

        @Override
        public void removeItem(CFolderItem<CommunicationMessage> item) {
            super.removeItem(item);

        }

        @Override
        protected CForm<? extends CommunicationMessage> createItemForm(IObject<?> member) {
            return new MessageFolderItem(this);
        }

    }

    public class MessageFolderItem extends CForm<CommunicationMessage> {
        private Anchor btnSend;

        private Anchor btnCancel;

        private Anchor btnmarkAsUnread;

        CComboBoxBoolean cmbStar;

        Image starImage;

        private final OpenMessageFolder parent;

        public MessageFolderItem(OpenMessageFolder parent) {
            super(CommunicationMessage.class, new VistaViewersComponentFactory());
            inheritEditable(false);
            inheritViewable(false);
            inheritEnabled(false);
            this.parent = parent;
        }

        @Override
        public IsWidget createContent() {
            PortalFormPanel formPanel = new PortalFormPanel(this);
            inject(proto().star());
            starImage = new Image(PortalImages.INSTANCE.noStar());
            starImage.setStyleName(PortalRootPaneTheme.StyleName.CommHeaderWriteAction.name());
            starImage.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    CommunicationMessage m = getValue();
                    m.star().setValue(!m.star().getValue(false));
                    if (m.star().getValue(false)) {
                        starImage.setResource(PortalImages.INSTANCE.fullStar());
                    } else {
                        starImage.setResource(PortalImages.INSTANCE.noStar());
                    }
                    ((CommunicationMessagePagePresenter) CommunicationMessagePage.this.getView().getPresenter()).saveMessage(
                            new DefaultAsyncCallback<CommunicationMessage>() {
                                @Override
                                public void onSuccess(CommunicationMessage result) {
                                }
                            }, m);
                }
            });
            formPanel.append(Location.Left, starImage);
            formPanel.h1("Details");
            formPanel.append(Location.Left, proto().data().date(), new CLabel<String>()).decorate().componentWidth(200);
            CComboBoxBoolean cmbBoolean = new CComboBoxBoolean();
            cmbBoolean.setOptions(Arrays.asList(new Boolean[] { Boolean.TRUE, Boolean.FALSE }));

            formPanel.append(Location.Left, proto().data().isHighImportance(), cmbBoolean).decorate().componentWidth(200);
            formPanel.append(Location.Left, proto().data().sender(), new SenderLabel()).decorate().componentWidth(200);
            formPanel.append(Location.Left, proto().data().text()).decorate().componentWidth(200);
            formPanel.br();
            formPanel.h1("Attachments");
            formPanel.append(Location.Left, proto().data().attachments(), new CommunicationMessageAttachmentFolder());
            formPanel.append(Location.Left, createLowerToolbar());
            return formPanel;
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
                        ((CommunicationMessagePagePresenter) CommunicationMessagePage.this.getView().getPresenter()).saveMessage(
                                new DefaultAsyncCallback<CommunicationMessage>() {
                                    @Override
                                    public void onSuccess(CommunicationMessage result) {
                                        getValue().setPrimaryKey(result.getPrimaryKey());
                                        getValue().isRead().setValue(false);
                                        getValue().data().date().setValue(result.data().date().getValue());
                                        refresh(false);
                                    }
                                }, getValue());

                    }
                };
            });
            btnCancel = new Anchor(i18n.tr("Cancel"), new Command() {
                @Override
                public void execute() {
                    ((OpenMessageFolder) getParent().getParent()).removeItem((CFolderItem<CommunicationMessage>) getParent());
                }
            });

            btnmarkAsUnread = new Anchor(i18n.tr("Mark as unread"), new Command() {
                @Override
                public void execute() {
                    CommunicationMessage m = getValue();

                    m.isRead().setValue(false);
                    ((CommunicationMessagePagePresenter) CommunicationMessagePage.this.getView().getPresenter()).saveMessage(
                            new DefaultAsyncCallback<CommunicationMessage>() {
                                @Override
                                public void onSuccess(CommunicationMessage result) {
                                    AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.CommunicationMessage.CommunicationMessageView());
                                }
                            }, m);
                }
            });

            tb.addItem(btnSend);
            tb.addItem(btnCancel);
            tb.addItem(btnmarkAsUnread);
            btnSend.setVisible(false);
            btnCancel.setVisible(false);
            btnmarkAsUnread.setVisible(false);

            return tb;
        }

        @Override
        protected CommunicationMessage preprocessValue(CommunicationMessage value, boolean fireEvent, boolean populate) {
            if (value != null && value.getPrimaryKey() != null && !value.getPrimaryKey().isDraft()) {
                if (!value.isRead().getValue() && ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(value.recipient().getPrimaryKey())) {
                    value.isRead().setValue(true);
                    BoxFolderItemDecorator<CommunicationMessage> d = (BoxFolderItemDecorator<CommunicationMessage>) getParent().getDecorator();
                    d.setExpended(true);
                    ((CommunicationMessagePagePresenter) CommunicationMessagePage.this.getView().getPresenter()).saveMessage(
                            new DefaultAsyncCallback<CommunicationMessage>() {
                                @Override
                                public void onSuccess(CommunicationMessage result) {
                                }
                            }, value);
                }

            } else {
                value.isRead().setValue(false);
            }
            return super.preprocessValue(value, fireEvent, populate);
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);
            if (getValue().isPrototype() || getValue().data().text() == null || getValue().data().text().isNull()) {
                BoxFolderItemDecorator<CommunicationMessage> d = (BoxFolderItemDecorator<CommunicationMessage>) getParent().getDecorator();
                d.setExpended(true);
                setViewable(false);
                setEditable(true);
                setEnabled(true);
                btnSend.setVisible(true);
                btnCancel.setVisible(true);
                starImage.setVisible(false);
                btnmarkAsUnread.setVisible(false);
                get(proto().data().date()).setVisible(false);
                get(proto().star()).setVisible(false);
                get(proto().data().sender()).setVisible(false);
            } else {
                setViewable(true);
                setEditable(false);
                setEnabled(false);
                btnSend.setVisible(false);
                btnCancel.setVisible(false);
                get(proto().data().date()).setVisible(true);
                get(proto().data().sender()).setVisible(true);
                get(proto().star()).setVisible(ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(getValue().recipient().getPrimaryKey()));
                btnmarkAsUnread.setVisible(ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(getValue().recipient().getPrimaryKey()));
                starImage.setVisible(ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(getValue().recipient().getPrimaryKey()));
                if (get(proto().star()).getValue()) {
                    starImage.setResource(PortalImages.INSTANCE.fullStar());
                } else {
                    starImage.setResource(PortalImages.INSTANCE.noStar());
                }
            }
        }

        public class SenderLabel extends CEntityLabel<CommunicationEndpoint> {

            @Override
            public String format(CommunicationEndpoint value) {
                if (value == null) {
                    return "";
                } else {
                    return value.getStringView();
                }
            }
        }
    }
}
