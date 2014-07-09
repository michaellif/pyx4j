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
import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
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
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.CrudAppPlace.Type;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Toolbar;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.activity.crud.communication.MessageViewerActivity;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectorDialog;
import com.propertyvista.crm.client.ui.components.boxes.TenantSelectorDialog;
import com.propertyvista.crm.client.ui.components.boxes.UnitSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.tools.common.selectors.CommunicationEndpointSelector;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.selections.SelectCrmUserListService;
import com.propertyvista.crm.rpc.services.selections.SelectPortfolioListService;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.CommunicationEndpoint.ContactType;
import com.propertyvista.domain.communication.DeliveryHandle;
import com.propertyvista.domain.communication.MessageCategory.MessageGroupCategory;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.common.AbstractPmcUser;
import com.propertyvista.domain.tenant.lease.Tenant;
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

        MessageAttachmentFolder attachemnts;

        private FormPanel searchCriteriaPanel;

        private Button.ButtonMenuBar subMenu;

        private final Button actionsButton;

        private CommunicationEndpointSelector communicationEndpointSelector;

        public MessageFolderItem() {
            super(MessageDTO.class, new VistaViewersComponentFactory());
            actionsButton = new Button(i18n.tr("Select Recipients"));
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
            subMenu = new Button.ButtonMenuBar();
            subMenu.addItem(new MenuItem(i18n.tr("Tenant"), new Command() {
                @Override
                public void execute() {
                    new TenantSelectorDialog(MessageForm.this.getParentView(), true) {
                        @Override
                        public void onClickOk() {
                            Collection<Tenant> ts = getSelectedItems();
                            if (ts != null && getSelectedItems().size() > 0) {
                                for (Tenant selected : ts) {
                                    addRecipient(selected);
                                }
                            }
                        }
                    }.show();
                }
            }));
            subMenu.addItem(new MenuItem(i18n.tr("Corporate"), new Command() {
                @Override
                public void execute() {
                    new CommunicationEndpointSelectorDialog<CrmUser>(MessageForm.this.getParentView(), CrmUser.class) {

                        @Override
                        protected AbstractListService<CrmUser> getSelectService() {
                            return GWT.<AbstractListService<CrmUser>> create(SelectCrmUserListService.class);
                        }
                    }.show();
                }
            }));
            subMenu.addItem(new MenuItem(i18n.tr("Building"), new Command() {
                @Override
                public void execute() {
                    BuildingSelectorDialog dialog = new BuildingSelectorDialog(MessageForm.this.getParentView(), true) {
                        @Override
                        public void onClickOk() {
                            onAdd(getSelectedItems());
                        }
                    };
                    dialog.getCancelButton().setVisible(true);
                    dialog.show();
                }
            }));
            subMenu.addItem(new MenuItem(i18n.tr("Portfolio"), new Command() {
                @Override
                public void execute() {
                    new PortfolioSelectorDialog(MessageForm.this.getParentView()) {
                        @Override
                        public void onClickOk() {
                            onAdd(getSelectedItems());
                        }
                    }.show();
                }
            }));

            subMenu.addItem(new MenuItem(i18n.tr("Unit"), new Command() {
                @Override
                public void execute() {
                    new UnitSelectorDialog(MessageForm.this.getParentView(), true) {
                        @Override
                        public void onClickOk() {
                            onAdd(getSelectedItems());
                        }
                    }.show();
                }
            }));

            actionsButton.setMenu(subMenu);
            searchCriteriaPanel = new FormPanel(this);
            searchCriteriaPanel.append(Location.Dual, createCommunicationEndpointSelector());
            searchCriteriaPanel.h4("", actionsButton);
            formPanel.append(Location.Dual, searchCriteriaPanel);
            formPanel.br();
            formPanel.br();
            formPanel.br();

            formPanel.append(Location.Dual, proto().highImportance(), new CCheckBox()).decorate();
            formPanel.append(Location.Dual, proto().text()).decorate().labelWidth(0).customLabel("").useLabelSemicolon(false);

            attachmentCaption = formPanel.h3("Attachments");
            formPanel.append(Location.Dual, proto().attachments(), attachemnts = new MessageAttachmentFolder());
            formPanel.br();

            formPanel.append(Location.Dual, createLowerToolbar());

            return formPanel;
        }

        private abstract class PortfolioSelectorDialog extends EntitySelectorTableVisorController<Portfolio> {

            public PortfolioSelectorDialog(IPane parentView) {
                super(parentView, Portfolio.class, true, null, i18n.tr("Select Portfolio"));
            }

            @Override
            protected List<ColumnDescriptor> defineColumnDescriptors() {
                return Arrays.asList(//@formatter:off
                        new MemberColumnDescriptor.Builder(proto().name()).build(),
                        new MemberColumnDescriptor.Builder(proto().description()).wordWrap(true).build()
                ); //@formatter:on
            }

            @Override
            public List<Sort> getDefaultSorting() {
                return Arrays.asList(new Sort(proto().name(), false));
            }

            @Override
            protected AbstractListService<Portfolio> getSelectService() {
                return GWT.<AbstractListService<Portfolio>> create(SelectPortfolioListService.class);
            }
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
                onAdd(getSelectedItems());
            }
        }

        private void onAdd(Collection<? extends CommunicationEndpoint> eps) {
            if (eps != null && eps.size() > 0) {
                for (CommunicationEndpoint selected : eps) {
                    if (!ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(selected.getPrimaryKey())) {
                        addRecipient(selected);
                    }
                }
            }
        }

        private void addRecipient(CommunicationEndpoint selected) {
            CommunicationEndpointDTO proto = EntityFactory.create(CommunicationEndpointDTO.class);
            Class<? extends IEntity> epType = selected.getInstanceValueClass();
            if (epType.equals(Building.class)) {
                proto.name().set(((Building) selected).propertyCode());
                proto.type().setValue(ContactType.Building);
            } else if (epType.equals(Portfolio.class)) {
                proto.name().set(((Portfolio) selected).name());
                proto.type().setValue(ContactType.Portfolio);
            }
            if (epType.equals(AptUnit.class)) {
                proto.name().setValue(((AptUnit) selected).getStringView());
                proto.type().setValue(ContactType.Unit);
            } else if (epType.equals(CustomerUser.class)) {
                proto.name().set(((CustomerUser) selected).name());
                proto.type().setValue(ContactType.Tenant);
            } else if (epType.equals(CrmUser.class)) {
                proto.name().set(((CrmUser) selected).name());
                proto.type().setValue(ContactType.Employee);
            } else if (epType.equals(Tenant.class)) {
                proto.name().setValue(((Tenant) selected).customer().person().name().getStringView());
                proto.type().setValue(ContactType.Tenant);
            }

            proto.endpoint().set(selected);
            communicationEndpointSelector.addItem(proto);
        }

        private String buildForwardText(MessageDTO currentMessage) {
            if (currentMessage == null) {
                return null;
            }
            StringBuffer bodyText = new StringBuffer();
            StringBuffer buffer = null;
            new StringBuffer();
            for (CommunicationEndpointDTO recipient : currentMessage.to()) {
                if (buffer == null) {
                    buffer = new StringBuffer();
                } else {
                    buffer.append(", ");
                }
                buffer.append(recipient.name().getValue());
            }

            bodyText.append("\nFrom: ");
            bodyText.append(currentMessage.sender().name().getValue());
            bodyText.append("\nDate: ");
            bodyText.append(currentMessage.date().getStringView());
            bodyText.append("\nSubject: ");
            bodyText.append(currentMessage.subject().getValue());
            bodyText.append("\nTo: ");
            bodyText.append(buffer.toString());
            bodyText.append("\n\nFw:\n");
            bodyText.append(currentMessage.text().getValue());

            return bodyText.toString();
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
                        messagesFolder.addItem();
                        CFolderItem<MessageDTO> newItem = messagesFolder.getItem(messagesFolder.getItemCount() - 1);
                        newItem.getValue().text().setValue(currentMessage == null ? null : "\nRe:\n" + currentMessage.text().getValue());

                        if (!ClientContext.getUserVisit().getName().equals(currentMessage.header().sender().getValue())) {
                            newItem.getValue().to().add(currentMessage.sender());
                        }

                        newItem.refresh(false);
                        CForm<MessageDTO> form = newItem.getEntityForm();
                        if (form != null && form instanceof MessageFolderItem) {
                            MessageFolderItem folderItemForm = (MessageFolderItem) form;
                            folderItemForm.setFocusForEditingText();
                            folderItemForm.communicationEndpointSelector.addAll(newItem.getValue().to(), false);
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
                        MessageDTO currentMessage = getCurrent();
                        String forwardSubject = currentMessage == null ? null : "\nFw:\n" + currentMessage.subject().getValue();
                        String forwardText = buildForwardText(currentMessage);
                        CrudAppPlace place = new CrmSiteMap.Communication.Message(forwardSubject, forwardText);
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
                if (getValue().to().size() > 0) {
                    communicationEndpointSelector.addAll(getValue().to(), false);
                }
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

                attachmentCaption.setVisible(true);
                attachemnts.setVisible(true);
                highImportnaceImage.setVisible(false);
                get(proto().highImportance()).setVisible(true);
                statusToolBar.asWidget().setVisible(false);
                subMenu.setVisible(true);
                communicationEndpointSelector.setReadOnly(false);
                actionsButton.setVisible(true);
            } else {
                setViewable(true);
                setEditable(false);
                setEnabled(false);
                subMenu.setVisible(false);
                actionsButton.setVisible(false);
                communicationEndpointSelector.addAll(getValue().to(), true);
                communicationEndpointSelector.setReadOnly(true);
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
                attachemnts.setVisible(getValue().attachments().size() > 0);
                highImportnaceImage.setVisible(getValue().highImportance().getValue(false));
                get(proto().highImportance()).setVisible(false);
                statusToolBar.asWidget().setVisible(starImage.isVisible() || highImportnaceImage.isVisible());

            }
        }

        private IsWidget createCommunicationEndpointSelector() {
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
