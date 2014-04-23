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

import java.util.Arrays;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComboBoxBoolean;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Toolbar;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.dto.CommunicationEndpointDTO;
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
        IList<CommunicationEndpointDTO> to = messagesFolder.getValue().get(0).to();
        messagesFolder.addItem();
        CommunicationMessageDTO newMessage = messagesFolder.getItem(messagesFolder.getItemCount() - 1).getValue();
        newMessage.to().addAll(to);
        messagesFolder.getItem(messagesFolder.getItemCount() - 1).setValue(newMessage);
    }

    public void takeOwnership() {
        ((CommunicationMessageViewerView.Presenter) getParentView().getPresenter()).takeOwnership(new DefaultAsyncCallback<CommunicationMessageDTO>() {
            @Override
            public void onSuccess(CommunicationMessageDTO result) {
                getValue().setPrimaryKey(result.getPrimaryKey());
                refresh(false);
            }
        }, getValue());
    }

    public BasicFlexFormPanel createGeneralForm() {
        BasicFlexFormPanel mainPanel = new TwoColumnFlexFormPanel(i18n.tr("General"));
        int row = -1;
        mainPanel.setWidget(++row, 0, inject(proto().threadDTO().created(), new FieldDecoratorBuilder(20).build()));
        mainPanel.setWidget(++row, 0, inject(proto().subject(), new FieldDecoratorBuilder(20).build()));
        mainPanel.setWidget(++row, 0, inject(proto().threadDTO().content(), messagesFolder));
        mainPanel.setBR(++row, 0, 1);

        return mainPanel;
    }

    private class OpenMessageFolder extends VistaBoxFolder<CommunicationMessageDTO> {
        public OpenMessageFolder() {
            super(CommunicationMessageDTO.class, false);
            setAddable(true);
        }

        @Override
        public IFolderItemDecorator<CommunicationMessageDTO> createItemDecorator() {
            BoxFolderItemDecorator<CommunicationMessageDTO> decor = (BoxFolderItemDecorator<CommunicationMessageDTO>) super.createItemDecorator();
            decor.setExpended(false);
            return decor;
        }

        @Override
        public void addItem() {
            super.addItem();
        }

        @Override
        public void removeItem(CFolderItem<CommunicationMessageDTO> item) {
            super.removeItem(item);

        }

        @Override
        protected CForm<? extends CommunicationMessageDTO> createItemForm(IObject<?> member) {
            return new MessageFolderItem();
        }

    }

    public class MessageFolderItem extends CForm<CommunicationMessageDTO> {
        private Anchor btnSend;

        private final CommunicationEndpointFolder receiverSelector;

        private Anchor btnCancel;

        public MessageFolderItem() {
            super(CommunicationMessageDTO.class, new VistaViewersComponentFactory());
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
            content.setWidget(++row, 0, inject(proto().date(), new FieldDecoratorBuilder(20).build()));
            CComboBoxBoolean cmbBoolean = new CComboBoxBoolean();
            cmbBoolean.setOptions(Arrays.asList(new Boolean[] { Boolean.TRUE, Boolean.FALSE }));
            content.setWidget(++row, 0, inject(proto().isHighImportance(), cmbBoolean, new FieldDecoratorBuilder(20).build()));
            content.setWidget(++row, 0, inject(proto().text(), new FieldDecoratorBuilder(20).build()));
            content.setH1(++row, 0, 1, "From");
            content.setWidget(++row, 0, inject(proto().sender(), new FieldDecoratorBuilder(20).build()));
            content.setH1(++row, 0, 1, "To");
            content.setWidget(++row, 0, inject(proto().to(), receiverSelector));

            content.setBR(++row, 0, 1);
            content.setH1(++row, 0, 1, "Attachments");
            content.setWidget(++row, 0, inject(proto().attachments(), new CommunicationMessageAttachmentFolder()));
            content.setWidget(++row, 0, 2, createLowerToolbar());
            return content;
        }

        /*
         * @Override
         * protected CommunicationMessageDTO preprocessValue(CommunicationMessageDTO value, boolean fireEvent, boolean populate) {
         * if (value != null && value.getPrimaryKey() != null && !value.getPrimaryKey().isDraft()) {
         * if (!value.isRead().getValue() && ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(value.recipient().getPrimaryKey())) {
         * value.isRead().setValue(true);
         * BoxFolderItemDecorator<CommunicationMessage> d = (BoxFolderItemDecorator<CommunicationMessage>) getParent().getDecorator();
         * d.setExpended(true);
         * ((CommunicationMessageViewerView.Presenter) CommunicationMessageForm.this.getParentView().getPresenter()).saveMessage(
         * new DefaultAsyncCallback<CommunicationMessageDTO>() {
         * 
         * @Override
         * public void onSuccess(CommunicationMessageDTO result) {
         * }
         * }, value);
         * }
         * 
         * } else {
         * value.isRead().setValue(false);
         * }
         * return super.preprocessValue(value, fireEvent, populate);
         * }
         */
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

                            ((CommunicationMessageEditorView.Presenter) p).saveMessage(new DefaultAsyncCallback<CommunicationMessageDTO>() {
                                @Override
                                public void onSuccess(CommunicationMessageDTO result) {
                                    getValue().setPrimaryKey(result.getPrimaryKey());
                                    refresh(false);
                                }
                            }, getValue());
                        } else {

                            ((CommunicationMessageViewerView.Presenter) p).saveMessage(new DefaultAsyncCallback<CommunicationMessageDTO>() {
                                @Override
                                public void onSuccess(CommunicationMessageDTO result) {
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
                    ((OpenMessageFolder) getParent().getParent()).removeItem((CFolderItem<CommunicationMessageDTO>) getParent());
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
                BoxFolderItemDecorator<CommunicationMessageDTO> d = (BoxFolderItemDecorator<CommunicationMessageDTO>) getParent().getDecorator();
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
                return value.getStringView();
            }
        }
    }
}
