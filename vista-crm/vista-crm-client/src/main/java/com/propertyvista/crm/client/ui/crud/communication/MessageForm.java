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
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.EntitySelectorTableVisorController;
import com.pyx4j.site.client.ui.IPane;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.CrudAppPlace.Type;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Toolbar;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.activity.crud.communication.MessageViewerActivity;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.tools.common.selectors.CommunicationEndpointSelector;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.selections.SelectCrmUserListService;
import com.propertyvista.crm.rpc.services.selections.SelectCustomerUserListService;
import com.propertyvista.domain.communication.CommunicationEndpoint.ContactType;
import com.propertyvista.domain.communication.DeliveryHandle;
import com.propertyvista.domain.communication.MessageCategory.MessageGroupCategory;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.common.AbstractPmcUser;
import com.propertyvista.dto.CommunicationEndpointDTO;
import com.propertyvista.dto.MessageDTO;

public class MessageForm extends CrmEntityForm<MessageDTO> {

    private static final I18n i18n = I18n.get(MessageForm.class);

    private final MessageFolder messagesFolder;

    public MessageForm(IForm<MessageDTO> view) {
        super(MessageDTO.class, view);
        setTabBarVisible(false);
        messagesFolder = new MessageFolder();

        selectTab(addTab(createGeneralForm(), i18n.tr("Communication")));
        inheritEditable(true);
        inheritViewable(false);
        inheritEnabled(true);
        setEnabled(true);
    }

    public void assignOwnership(Employee employee) {
        ((MessageViewerView.Presenter) getParentView().getPresenter()).assignOwnership(getValue(), employee);
    }

    public IsWidget createGeneralForm() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().subject()).decorate();
        formPanel.append(Location.Left, proto().allowedReply()).decorate();
        formPanel.append(Location.Right, proto().topic()).decorate();
        formPanel.append(Location.Left, proto().owner().name()).decorate().customLabel(i18n.tr("Owner"));
        formPanel.append(Location.Right, proto().status()).decorate();
        formPanel.append(Location.Dual, proto().content(), messagesFolder);
        formPanel.br();

        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        if (getValue() == null || getValue().isPrototype()) {
            get(proto().owner().name()).setVisible(false);
            get(proto().status()).setVisible(false);

        } else {
            get(proto().owner().name()).setVisible(!MessageGroupCategory.Custom.equals(getValue().topic().category().getValue()));
            get(proto().status()).setVisible(!MessageGroupCategory.Custom.equals(getValue().topic().category().getValue()));
        }
    }

    protected void addItem(CommunicationEndpointDTO proto) {
        new SelectEnumDialog<ContactType>(i18n.tr("Select contact type"), EnumSet.of(ContactType.Employee, ContactType.Tenants)) {
            @Override
            public boolean onClickOk() {
                final ContactType type = getSelectedType();
                if (type != null) {
                    if (type.equals(ContactType.Employee)) {
                        new CommunicationEndpointSelectorDialog<CrmUser>(getParentView(), CrmUser.class) {

                            @Override
                            protected AbstractListService<CrmUser> getSelectService() {
                                return GWT.<AbstractListService<CrmUser>> create(SelectCrmUserListService.class);
                            }
                        }.show();
                    } else if (type.equals(ContactType.Tenants)) {
                        new CommunicationEndpointSelectorDialog<CustomerUser>(getParentView(), CustomerUser.class) {

                            @Override
                            protected AbstractListService<CustomerUser> getSelectService() {
                                return GWT.<AbstractListService<CustomerUser>> create(SelectCustomerUserListService.class);
                            }
                        }.show();
                    }
                }
                return true;
            }

            @Override
            public String getEmptySelectionMessage() {
                return i18n.tr("No recipient type to choose from.");
            }
        }.show();
    }

    private abstract class CommunicationEndpointSelectorDialog<E extends AbstractPmcUser> extends EntitySelectorTableVisorController<E> {

        public CommunicationEndpointSelectorDialog(IPane parentView, Class<E> entityClass) {
            super(parentView, entityClass, true, i18n.tr("Select User"));
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(new MemberColumnDescriptor.Builder(proto().name()).searchable(true).build());
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().name(), false));
        }

        @Override
        public void onClickOk() {
            if (!getSelectedItems().isEmpty()) {
                for (AbstractPmcUser selected : getSelectedItems()) {
                    CommunicationEndpointDTO proto = EntityFactory.create(CommunicationEndpointDTO.class);
                    proto.name().set(selected.name());
                    proto.type().setValue(selected.getInstanceValueClass().equals(CustomerUser.class) ? ContactType.Tenants : ContactType.Employee);
                    proto.endpoint().set(selected);
                    addItem(proto);
                }
            }
        }
    }

    private class MessageFolder extends VistaBoxFolder<MessageDTO> {
        public MessageFolder() {
            super(MessageDTO.class, false);
            setAddable(true);
        }

        @Override
        public IFolderItemDecorator<MessageDTO> createItemDecorator() {
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
            return new MessageFolderItem();
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

        private FormPanel searchCriteriaPanel;

        private CommunicationEndpointSelector communicationEndpointSelector;

        public MessageFolderItem() {
            super(MessageDTO.class, new VistaViewersComponentFactory());

            inheritEditable(false);
            inheritViewable(false);
            inheritEnabled(false);
        }

        public void setFocusForEditingText() {
            get(proto().text()).asWidget().getElement().focus();
        }

        @Override
        public IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);
            inject(proto().thread());
            inject(proto().star());
            inject(proto().date());
            inject(proto().allowedReply());
            inject(proto().isInRecipients());

            statusToolBar = new Toolbar();

            starImage = new Image(CrmImages.INSTANCE.noStar());
            starImage.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    MessageDTO m = getValue();
                    m.star().setValue(!m.star().getValue(false));
                    if (m.star().getValue(false)) {
                        starImage.setResource(CrmImages.INSTANCE.fullStar());
                    } else {
                        starImage.setResource(CrmImages.INSTANCE.noStar());
                    }
                    ((MessageViewerActivity) MessageForm.this.getParentView().getPresenter()).saveMessage(m, null, false);
                }

            });

            highImportnaceImage = new Image(CrmImages.INSTANCE.noticeWarning());

            statusToolBar.addItem(highImportnaceImage);
            statusToolBar.addItem(starImage);

            formPanel.append(Location.Dual, proto().header()).decorate().labelWidth(0).customLabel("").useLabelSemicolon(false).assistantWidget(statusToolBar);
            formPanel.br();

            formPanel.h4("To");
            searchCriteriaPanel = new FormPanel(this);

            searchCriteriaPanel.append(Location.Dual, createCommunicationEndpointSelector());
            formPanel.append(Location.Dual, searchCriteriaPanel);
            formPanel.br();
            formPanel.br();
            formPanel.br();

            formPanel.append(Location.Dual, proto().highImportance(), new CCheckBox()).decorate();
            formPanel.append(Location.Dual, proto().text()).decorate().labelWidth(0).customLabel("").useLabelSemicolon(false);

            attachmentCaption = formPanel.h3("Attachments");
            formPanel.append(Location.Dual, proto().attachments(), attachemnts = new MessageAttachmentFolder());
            attachmentBr = formPanel.br();

            formPanel.append(Location.Dual, createLowerToolbar());

            return formPanel;
        }

        private String buildForwardText() {
            CFolderItem<MessageDTO> current = (CFolderItem<MessageDTO>) getParent();
            String forwardText = current == null ? null : "\nRe:\n" + current.getValue().text().getValue();
            return forwardText;
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
                    BoxFolderItemDecorator<MessageDTO> d = (BoxFolderItemDecorator<MessageDTO>) getParent().getDecorator();
                    d.setExpended(true);
                    saveMessage(value, false);
                }

            } else {
                value.isRead().setValue(false);

                if (!value.thread().hasValues()) {
                    value.thread().set(MessageForm.this.getValue().thread());
                }
            }
            return super.preprocessValue(value, fireEvent, populate);
        }

        protected Toolbar createLowerToolbar() {
            Toolbar tb = new Toolbar();

            btnSend = new Anchor(i18n.tr("Send"), new Command() {
                @Override
                public void execute() {
                    if (!isValid()) {
                        setVisited(true);
                        MessageDialog.error(i18n.tr("Error"), getValidationResults().getValidationMessage(true));
                    } else if (getValue().to().size() < 1) {
                        setVisited(true);
                        MessageDialog.error(i18n.tr("Error"), i18n.tr("No message recipient has been provided."));
                    } else {
                        saveMessage(getValue(), true);
                    }
                }

            });

            btnReply = new Anchor(i18n.tr("Reply"), new Command() {
                @Override
                public void execute() {
                    if (!isValid()) {
                        setVisited(true);
                        MessageDialog.error(i18n.tr("Error"), getValidationResults().getValidationMessage(true));
                    } else {
                        MessageDTO currentMessage = getCurrent();
                        String forwardText = buildForwardText();
                        messagesFolder.addItem();
                        CFolderItem<MessageDTO> newItem = messagesFolder.getItem(messagesFolder.getItemCount() - 1);
                        newItem.getValue().text().setValue(forwardText);

                        if (!ClientContext.getUserVisit().getName().equals(currentMessage.header().sender().getValue())) {
                            newItem.getValue().to().add(currentMessage.sender());
                        }
                        for (CommunicationEndpointDTO to : currentMessage.to()) {
                            if (!ContactType.System.equals(to.type().getValue()) && !ClientContext.getUserVisit().getName().equals(to.name().getValue())) {
                                newItem.getValue().to().add(to);
                            }
                        }

                        newItem.refresh(false);
                        CForm<MessageDTO> form = newItem.getEntityForm();
                        if (form != null && form instanceof MessageFolderItem) {
                            ((MessageFolderItem) form).setFocusForEditingText();
                        }
                        newItem.asWidget().getElement().scrollIntoView();
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
                        CrudAppPlace place = new CrmSiteMap.Communication.Message(buildForwardText());
                        place.setType(Type.editor);
                        AppSite.getPlaceController().goTo(place);
                    }
                };
            });

            btnCancel = new Anchor(i18n.tr("Cancel"), new Command() {
                @Override
                public void execute() {
                    ((MessageFolder) getParent().getParent()).removeItem((CFolderItem<MessageDTO>) getParent());
                }
            });

            btnMarkAsUnread = new Anchor("Mark as unread", new Command() {
                @Override
                public void execute() {
                    MessageDTO m = getValue();

                    m.isRead().setValue(false);
                    saveMessage(m, false);
                    CrudAppPlace place = new CrmSiteMap.Communication.Message();
                    place.setType(Type.lister);
                    AppSite.getPlaceController().goTo(place);
                }
            });

            tb.addItem(btnSend);
            tb.addItem(btnReply);
            tb.addItem(btnForward);
            tb.addItem(btnCancel);
            tb.addItem(btnMarkAsUnread);
            return tb;
        }

        public void saveMessage(MessageDTO m, boolean refresh) {
            com.pyx4j.site.client.ui.prime.IPrimePane.Presenter p = getParentView().getPresenter();
            if (p instanceof MessageEditorView.Presenter) {

                ((MessageEditorView.Presenter) p).saveMessage(new DefaultAsyncCallback<MessageDTO>() {
                    @Override
                    public void onSuccess(MessageDTO result) {
                        getValue().setPrimaryKey(result.getPrimaryKey());
                        refresh(false);
                    }
                }, getValue());
            } else {
                ((MessageViewerView.Presenter) p).saveMessage(m, null, refresh);
            }
        };

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);
            if (getValue().isPrototype() || getValue().date() == null || getValue().date().isNull()) {
                communicationEndpointSelector.removeAll();
                BoxFolderItemDecorator<DeliveryHandle> d = (BoxFolderItemDecorator<DeliveryHandle>) getParent().getDecorator();
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
                get(proto().header()).setVisible(false);

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
                communicationEndpointSelector.addAll(getValue().to(), true);
                btnSend.setVisible(false);
                btnCancel.setVisible(false);
                btnReply.setVisible(!ClientContext.getUserVisit().getName().equals(getValue().header().sender().getValue())
                        && getValue().allowedReply().getValue(true));
                btnForward.setVisible(true);
                get(proto().header()).setVisible(true);
                get(proto().star()).setVisible(getValue().isInRecipients().getValue(false));
                btnMarkAsUnread.setVisible(getValue().isInRecipients().getValue(false));

                starImage.setVisible(getValue().isInRecipients().getValue(false));
                starImage.setResource(get(proto().star()).getValue() ? CrmImages.INSTANCE.fullStar() : (CrmImages.INSTANCE.noStar()));

                attachmentCaption.setVisible(getValue().attachments().size() > 0);
                attachmentBr.setVisible(true);
                attachemnts.setVisible(getValue().attachments().size() > 0);
                highImportnaceImage.setVisible(getValue().highImportance().getValue(false));
                get(proto().highImportance()).setVisible(false);
                statusToolBar.asWidget().setVisible(starImage.isVisible() || highImportnaceImage.isVisible());

            }
        }

        private Widget createCommunicationEndpointSelector() {
            return communicationEndpointSelector = new CommunicationEndpointSelector() {//@formatter:off
                @Override protected void onItemAdded(CommunicationEndpointDTO item) {
                    super.onItemAdded(item);
                    MessageFolderItem.this.addToItem(item);
                }
                @Override
                protected void onItemRemoved(CommunicationEndpointDTO item) {
                    MessageFolderItem.this.removeToItem(item);
                }
            };//@formatter:on
        }

        public void addToItem(CommunicationEndpointDTO item) {
            getValue().to().add(item);
        }

        public void removeToItem(CommunicationEndpointDTO item) {
            getValue().to().remove(item);
        }
    }
}
