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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.form.FormDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Toolbar;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.CommunicationMessage;
import com.propertyvista.dto.CommunicationMessageDTO;
import com.propertyvista.portal.resident.ui.communication.CommunicationMessagePageView.CommunicationMessagePagePresenter;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

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
    protected FormDecorator<CommunicationMessageDTO, CEntityForm<CommunicationMessageDTO>> createDecorator() {
        FormDecorator<CommunicationMessageDTO, CEntityForm<CommunicationMessageDTO>> decorator = super.createDecorator();

        decorator.addFooterToolbarWidget(btnReplay);
        decorator.getFooterPanel().setVisible(true);

        return decorator;
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
        int row = -1;
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().thread().created(), new CLabel<String>()), 250).build());
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().subject(), new CLabel<String>()), 250).build());
        mainPanel.setWidget(++row, 0, inject(proto().thread().content(), messagesFolder));
        mainPanel.setBR(++row, 0, 1);

        return mainPanel;
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
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof CommunicationMessage) {
                return new MessageFolderItem(this);
            }
            return super.create(member);
        }

        @Override
        public void addItem() {
            super.addItem();
        }

        @Override
        public void removeItem(CEntityFolderItem<CommunicationMessage> item) {
            super.removeItem(item);

        }

    }

    public class MessageFolderItem extends CEntityForm<CommunicationMessage> {
        private Anchor btnSend;

        private Anchor btnCancel;

        private Anchor btnmarkAsUnread;

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
            BasicFlexFormPanel content = new BasicFlexFormPanel();
            int row = -1;
            content.setH1(++row, 0, 1, "Details");
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().date(), new CLabel<String>()), 180).build());
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().text()), 250).build());
            content.setBR(++row, 0, 1);
            content.setH1(++row, 0, 1, "Attachments");
            content.setWidget(++row, 0, inject(proto().attachments(), new CommunicationMessageAttachmentFolder()));
            content.setWidget(++row, 0, 2, createLowerToolbar());
            return content;
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
                                        getValue().date().setValue(result.date().getValue());
                                        refresh(false);
                                    }
                                }, getValue());

                    }
                };
            });
            btnCancel = new Anchor(i18n.tr("Cancel"), new Command() {
                @Override
                public void execute() {
                    ((OpenMessageFolder) getParent().getParent()).removeItem((CEntityFolderItem<CommunicationMessage>) getParent());
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
                if (!value.isRead().getValue() && userInList(ClientContext.getUserVisit(), value.to())) {
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
            if (getValue().isPrototype() || getValue().text() == null || getValue().text().isNull()) {
                BoxFolderItemDecorator<CommunicationMessage> d = (BoxFolderItemDecorator<CommunicationMessage>) getParent().getDecorator();
                d.setExpended(true);
                setViewable(false);
                setEditable(true);
                setEnabled(true);
                btnSend.setVisible(true);
                btnCancel.setVisible(true);
                btnmarkAsUnread.setVisible(false);
                get(proto().date()).setVisible(false);
            } else {
                setViewable(true);
                setEditable(false);
                setEnabled(false);
                btnSend.setVisible(false);
                btnCancel.setVisible(false);
                get(proto().date()).setVisible(true);
                btnmarkAsUnread.setVisible(userInList(ClientContext.getUserVisit(), getValue().to()));
            }
        }

        private boolean userInList(UserVisit user, IList<CommunicationEndpoint> list) {
            if (list == null || list.isNull()) {
                return false;
            }

            for (CommunicationEndpoint ep : list) {
                if (user.getPrincipalPrimaryKey().equals(ep.getPrimaryKey())) {
                    return true;
                }
            }
            return false;
        }

    }
}
