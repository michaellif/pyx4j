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

import com.google.gwt.user.client.Command;
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
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
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
import com.propertyvista.portal.shared.ui.CPortalEntityForm;

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
        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
        int row = -1;
        mainPanel.setWidget(++row, 0, inject(proto().thread().created(), new CLabel<String>(), new FieldDecoratorBuilder(250).build()));
        mainPanel.setWidget(++row, 0, inject(proto().subject(), new CLabel<String>(), new FieldDecoratorBuilder(250).build()));
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

        private Anchor star;

        CComboBoxBoolean cmbStar;

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
            content.setWidget(++row, 0, inject(proto().data().date(), new CLabel<String>(), new FieldDecoratorBuilder(20).build()));
            cmbStar = new CComboBoxBoolean();
            cmbStar.setOptions(Arrays.asList(new Boolean[] { Boolean.TRUE, Boolean.FALSE }));
            CComboBoxBoolean cmbBoolean2 = new CComboBoxBoolean();
            cmbBoolean2.setOptions(Arrays.asList(new Boolean[] { Boolean.TRUE, Boolean.FALSE }));

            content.setWidget(++row, 0, inject(proto().star(), cmbStar, new FieldDecoratorBuilder(20).build()));
            content.setWidget(++row, 0, inject(proto().data().isHighImportance(), cmbBoolean2, new FieldDecoratorBuilder(20).build()));
            content.setWidget(++row, 0, inject(proto().data().sender(), new SenderLabel(), new FieldDecoratorBuilder(20).build()));
            content.setWidget(++row, 0, inject(proto().data().text(), new FieldDecoratorBuilder(20).build()));
            content.setBR(++row, 0, 1);
            content.setH1(++row, 0, 1, "Attachments");
            content.setWidget(++row, 0, inject(proto().data().attachments(), new CommunicationMessageAttachmentFolder()));
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

            star = new Anchor(i18n.tr("Star"), new Command() {
                @Override
                public void execute() {
                    CommunicationMessage m = getValue();

                    m.star().setValue(!m.star().getValue(false));
                    cmbStar.setValue(m.star().getValue(false));
                    ((CommunicationMessagePagePresenter) CommunicationMessagePage.this.getView().getPresenter()).saveMessage(
                            new DefaultAsyncCallback<CommunicationMessage>() {
                                @Override
                                public void onSuccess(CommunicationMessage result) {
                                    //AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.CommunicationMessage.CommunicationMessageView());
                                }
                            }, m);
                }
            });
            tb.addItem(btnSend);
            tb.addItem(btnCancel);
            tb.addItem(btnmarkAsUnread);
            tb.addItem(star);
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
                star.setVisible(false);
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
                star.setVisible(ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(getValue().recipient().getPrimaryKey()));
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
