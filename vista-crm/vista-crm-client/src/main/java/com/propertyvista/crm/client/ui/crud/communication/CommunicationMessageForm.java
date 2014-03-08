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
package com.propertyvista.crm.client.ui.crud.communication;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Toolbar;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.CommunicationMessage;
import com.propertyvista.dto.CommunicationMessageDTO;

public class CommunicationMessageForm extends CrmEntityForm<CommunicationMessageDTO> {

    private static final I18n i18n = I18n.get(CommunicationMessageForm.class);

    private final OpenMessageFolder messagesFolder;

    public CommunicationMessageForm(IForm<CommunicationMessageDTO> view) {
        super(CommunicationMessageDTO.class, view);
        messagesFolder = new OpenMessageFolder();

        selectTab(addTab(createGeneralForm()));
        inheritEditable(true);
        inheritViewable(false);
        inheritEnabled(true);
        setEnabled(true);
    }

    public void reply() {
        messagesFolder.addItem();
    }

    public void takeOwnership() {
        ((CommunicationMessageViewerView.Presenter) getParentView().getPresenter()).takeOwnership(new DefaultAsyncCallback<CommunicationMessage>() {
            @Override
            public void onSuccess(CommunicationMessage result) {
                getValue().setPrimaryKey(result.getPrimaryKey());
                refresh(false);
            }
        }, getValue());
    }

    public BasicFlexFormPanel createGeneralForm() {
        BasicFlexFormPanel mainPanel = new TwoColumnFlexFormPanel(i18n.tr("General"));
        int row = -1;
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().thread().created(), new CLabel<String>()), 20).build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().subject()), 20).build());
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
        public IFolderItemDecorator<CommunicationMessage> createItemDecorator() {
            BoxFolderItemDecorator<CommunicationMessage> decor = (BoxFolderItemDecorator<CommunicationMessage>) super.createItemDecorator();
            decor.setExpended(false);
            return decor;
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof CommunicationMessage) {
                return new MessageFolderItem();
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

        private final CommunicationEndpointFolder receiverSelector;

        private Anchor btnCancel;

        public MessageFolderItem() {
            super(CommunicationMessage.class, new VistaViewersComponentFactory());
            receiverSelector = new CommunicationEndpointFolder(CommunicationMessageForm.this);

            inheritEditable(false);
            inheritViewable(false);
            inheritEnabled(false);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel content = new BasicFlexFormPanel();
            int row = -1;
            content.setH1(++row, 0, 1, "Details");
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().date(), new CLabel<String>()), 20).build());
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().text()), 20).build());
            content.setH1(++row, 0, 1, "From");
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().sender(), new SenderLabel()), 20).build());
            content.setH1(++row, 0, 1, "To");
            content.setWidget(++row, 0, inject(proto().to(), receiverSelector));

            content.setBR(++row, 0, 1);
            content.setH1(++row, 0, 1, "Attachments");
            content.setWidget(++row, 0, inject(proto().attachments(), new CommunicationMessageAttachmentFolder()));
            content.setWidget(++row, 0, 2, createLowerToolbar());
            return content;
        }

        @Override
        protected CommunicationMessage preprocessValue(CommunicationMessage value, boolean fireEvent, boolean populate) {
            if (value != null && value.getPrimaryKey() != null && !value.getPrimaryKey().isDraft()) {
                if (!value.isRead().getValue() && userInList(ClientContext.getUserVisit(), value.to())) {
                    value.isRead().setValue(true);
                    BoxFolderItemDecorator<CommunicationMessage> d = (BoxFolderItemDecorator<CommunicationMessage>) getParent().getDecorator();
                    d.setExpended(true);
                    ((CommunicationMessageViewerView.Presenter) CommunicationMessageForm.this.getParentView().getPresenter()).saveMessage(
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

        protected Toolbar createLowerToolbar() {
            Toolbar tb = new Toolbar();

            btnSend = new Anchor(i18n.tr("Send"), new Command() {
                @Override
                public void execute() {
                    if (!isValid()) {
                        setVisited(true);
                        MessageDialog.error(i18n.tr("Error"), getValidationResults().getValidationMessage(true));
                    } else {
                        com.pyx4j.site.client.ui.prime.IPrimePane.Presenter p = getParentView().getPresenter();
                        if (p instanceof CommunicationMessageEditorView.Presenter) {

                            ((CommunicationMessageEditorView.Presenter) p).saveMessage(new DefaultAsyncCallback<CommunicationMessage>() {
                                @Override
                                public void onSuccess(CommunicationMessage result) {
                                    getValue().setPrimaryKey(result.getPrimaryKey());
                                    refresh(false);
                                }
                            }, getValue());
                        } else {

                            ((CommunicationMessageViewerView.Presenter) p).saveMessage(new DefaultAsyncCallback<CommunicationMessage>() {
                                @Override
                                public void onSuccess(CommunicationMessage result) {
                                    getValue().setPrimaryKey(result.getPrimaryKey());
                                    refresh(false);
                                }
                            }, getValue());

                        }
                    }
                };
            });
            btnCancel = new Anchor(i18n.tr("Cancel"), new Command() {
                @Override
                public void execute() {
                    ((OpenMessageFolder) getParent().getParent()).removeItem((CEntityFolderItem<CommunicationMessage>) getParent());
                }
            });

            tb.addItem(btnSend);
            tb.addItem(btnCancel);
            btnSend.setVisible(false);
            btnCancel.setVisible(false);
            return tb;
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
                get(proto().date()).setVisible(false);
                get(proto().sender()).setVisible(false);
            } else {
                setViewable(true);
                setEditable(false);
                setEnabled(false);
                btnSend.setVisible(false);
                btnCancel.setVisible(false);
                get(proto().date()).setVisible(true);
                get(proto().sender()).setVisible(true);
            }
        }
    }

    public class SenderLabel extends CEntityLabel<CommunicationEndpoint> {

        @Override
        public String format(CommunicationEndpoint value) {
            if (value == null) {
                return "";
            } else {
                StringBuilder result = new StringBuilder();

                result.append(value.name().getStringView());
                result.append(", ");
                result.append(value.email().getStringView());

                return result.toString();
            }
        }
    }
}
