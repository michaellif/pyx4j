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
package com.propertyvista.crm.client.ui.crud.communication;

import java.util.Date;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.TextOverflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CEntityHyperlink;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CRichTextArea;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.ui.prime.CEntitySelectorHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.CrudAppPlace.Type;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Toolbar;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.crm.client.activity.crud.communication.MessageViewerActivity;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.themes.CommunicationCrmTheme;
import com.propertyvista.crm.client.ui.components.boxes.TenantSelectionDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.communication.selector.CommunicationEndpointSelector;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.communication.CommunicationAssociation;
import com.propertyvista.domain.communication.DeliveryHandle;
import com.propertyvista.domain.communication.MessageCategory.CategoryType;
import com.propertyvista.domain.communication.SpecialDelivery.DeliveryMethod;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.MessageDTO;

public class MessageForm extends CrmEntityForm<MessageDTO> {

    private static final I18n i18n = I18n.get(MessageForm.class);

    private final MessageFolder messagesFolder;

    public MessageForm(IPrimeFormView<MessageDTO, ?> view) {
        super(MessageDTO.class, view);
        setTabBarVisible(false);
        messagesFolder = new MessageFolder(this);

        selectTab(addTab(createGeneralForm(), i18n.tr("Communication")));
        inheritEditable(true);
        inheritViewable(false);
        inheritEnabled(true);
        setEnabled(true);
    }

    public void assignOwnership(IEntity employee, String additionalComment) {
        ((MessageViewerView.Presenter) getParentView().getPresenter()).assignOwnership(getValue(), additionalComment, employee);
    }

    public void hideUnhide() {
        ((MessageViewerView.Presenter) getParentView().getPresenter()).hideUnhide(getValue());
    }

    public IsWidget createGeneralForm() {
        FormPanel formPanel = new FormPanel(this);
        CLabel<String> threadLabel = new CLabel<String>();
        threadLabel.asWidget().setStylePrimaryName(CommunicationCrmTheme.StyleName.CommunicationThreadName.name());
        inject(proto().deliveryMethod());

        formPanel.append(Location.Dual, proto().subject(), threadLabel);
        formPanel.br();

        formPanel.append(Location.Left, proto().category()).decorate();
        formPanel.append(Location.Right, proto().allowedReply()).decorate();
        formPanel.append(Location.Left, proto().owner().name()).decorate().customLabel(i18n.tr("Owner"));
        formPanel.append(Location.Right, proto().status()).decorate();
        formPanel.append(Location.Left, proto().header()).decorate().customLabel(i18n.tr("Sent"));
        formPanel.append(Location.Dual, proto().to(), new CommunicationEndpointSelector()).decorate();
        formPanel.append(Location.Dual, proto().associated(), new CAssociationLabel()).decorate();
        formPanel.append(Location.Dual, proto().deliveredText()).decorate();
        formPanel.append(Location.Left, proto().dateFrom()).decorate().customLabel(i18n.tr("Notification Date"));
        formPanel.append(Location.Right, proto().dateTo()).decorate().customLabel(i18n.tr("Expiration Date"));
        formPanel.append(Location.Dual, proto().text(), new CRichTextArea()).decorate();

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
            get(proto().associated()).setVisible(false);
            get(proto().content()).setVisible(true);
            get(proto().dateFrom()).setVisible(false);
            get(proto().dateTo()).setVisible(false);
            get(proto().deliveredText()).setVisible(false);
            get(proto().text()).setVisible(false);
            get(proto().header()).setVisible(false);
            get(proto().to()).setVisible(false);
        } else {
            get(proto().owner().name()).setVisible(CategoryType.Ticket.equals(getValue().category().categoryType().getValue()));
            get(proto().status()).setVisible(CategoryType.Ticket.equals(getValue().category().categoryType().getValue()));
            get(proto().associated()).setVisible(
                    CategoryType.Ticket.equals(getValue().category().categoryType().getValue()) && getValue().associated() != null
                            && !getValue().associated().isNull());
            boolean isEmpty = getValue().isNull() || getValue().isEmpty();
            boolean isDeliveryMethodEmpty = isEmpty || getValue().deliveryMethod() == null || getValue().deliveryMethod().isNull()
                    || getValue().deliveryMethod().isPrototype();
            boolean isNotification = !isDeliveryMethodEmpty && DeliveryMethod.Notification.equals(getValue().deliveryMethod().getValue());
            get(proto().content()).setVisible(isEmpty || isDeliveryMethodEmpty);
            get(proto().subject()).setVisible(isEmpty || isDeliveryMethodEmpty || isNotification);
            get(proto().dateFrom()).setVisible(isNotification && getValue().dateFrom() != null && !getValue().dateFrom().isNull());
            get(proto().dateTo()).setVisible(isNotification && getValue().dateTo() != null && !getValue().dateTo().isNull());
            get(proto().header()).setVisible(!isEmpty && !isDeliveryMethodEmpty);
            get(proto().to()).setVisible(!isEmpty && !isDeliveryMethodEmpty);
            get(proto().allowedReply()).setVisible(isEmpty || isDeliveryMethodEmpty);

            if (!isEmpty && !isDeliveryMethodEmpty) {
                get(proto().deliveredText()).setTitle(getValue().deliveryMethod().getValue().toString());
                get(proto().deliveredText()).setVisible(true);
                get(proto().text()).setTitle(i18n.tr("Fallback"));
                get(proto().text()).setVisible(!isNotification);
            } else {
                get(proto().deliveredText()).setVisible(false);
                get(proto().text()).setVisible(false);
                get(proto().text()).setTitle(i18n.tr("Text"));
            }
        }
    }

    public class CAssociationLabel extends CEntityHyperlink<CommunicationAssociation> {

        public CAssociationLabel() {
            super();
            setNavigationCommand(new Command() {
                @Override
                public void execute() {
                    CommunicationAssociation value = getValue();
                    if (value != null && value.getPrimaryKey() != null) {
                        if (value.getInstanceValueClass().equals(MaintenanceRequest.class)) {
                            AppSite.getPlaceController().goTo(
                                    AppPlaceEntityMapper.resolvePlace(MaintenanceRequest.class).formViewerPlace(value.getPrimaryKey()));
                        }
                    }
                }
            });
        }

        @Override
        public String format(CommunicationAssociation value) {
            if (value == null) {
                return "";
            } else {
                StringBuilder result = new StringBuilder();

                if (value.getInstanceValueClass().equals(MaintenanceRequest.class)) {
                    result.append(i18n.tr("Maintenance Request"));
                    //result.append(", ");
                } else {
                    result.append(value.getInstanceValueClass().getSimpleName());
                }

                //result.append(value.getPrimaryKey().toString());

                return result.toString();
            }
        }
    }

    public static class MessageFolder extends VistaBoxFolder<MessageDTO> {
        private final CrmEntityForm<? extends IEntity> parentForm;

        public MessageFolder(CrmEntityForm<? extends IEntity> parentForm) {
            super(MessageDTO.class, false);
            this.parentForm = parentForm;
            setAddable(true);
        }

        @Override
        public VistaBoxFolderItemDecorator<MessageDTO> createItemDecorator() {
            VistaBoxFolderItemDecorator<MessageDTO> decor = super.createItemDecorator();
            decor.setCaptionFormatter(new IFormatter<MessageDTO, SafeHtml>() {
                @Override
                public SafeHtml format(MessageDTO value) {

                    Label messageField = new Label(HtmlUtils.removeHtmlTags(value.text().getValue("")));
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
            return new MessageFolderItem(parentForm, this);
        }

    }

    public static class MessageFolderItem extends CForm<MessageDTO> {
        private Anchor btnSend;

        private Anchor btnCancel;

        private Anchor btnMarkAsUnread;

        private Anchor btnReply;

        Image starImage;

        Image highImportnaceImage;

        Toolbar statusToolBar;

        Widget attachmentCaption;

        MessageAttachmentFolder attachemnts;

        private final CommunicationEndpointSelector communicationEndpointSelector = createCommunicationEndpointSelector();

        private final CrmEntityForm<? extends IEntity> parentForm;

        private final MessageFolder parentFolder;

        public MessageFolderItem(CrmEntityForm<? extends IEntity> parentForm, MessageFolder parentFolder) {
            super(MessageDTO.class, new VistaViewersComponentFactory());
            this.parentForm = parentForm;
            this.parentFolder = parentFolder;
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
                    ((MessageViewerActivity) parentForm.getParentView().getPresenter()).saveMessage(m, null, false);
                }

            });

            highImportnaceImage = new Image(CrmImages.INSTANCE.messageImportant());

            statusToolBar.addItem(highImportnaceImage);
            statusToolBar.addItem(starImage);

            formPanel.append(Location.Dual, proto().header()).decorate().labelWidth(0).customLabel("").useLabelSemicolon(false).assistantWidget(statusToolBar);
            formPanel.br();

            formPanel.append(Location.Dual, proto().to(), communicationEndpointSelector).decorate().labelWidth(20);
            formPanel.br();

            formPanel.append(Location.Left, proto().onBehalf(), new TenantSelector()).decorate();
            formPanel.append(Location.Right, proto().onBehalfVisible()).decorate().customLabel(i18n.tr("Is Visible For Tenant"));
            formPanel.br();

            formPanel.append(Location.Dual, proto().highImportance(), new CCheckBox()).decorate();

            formPanel.append(Location.Dual, proto().text(), new CRichTextArea()).decorate().labelWidth(0).customLabel("").useLabelSemicolon(false);

            attachmentCaption = formPanel.h3("Attachments");
            formPanel.append(Location.Dual, proto().attachments(), attachemnts = new MessageAttachmentFolder());
            formPanel.br();

            formPanel.append(Location.Dual, createLowerToolbar());

            return formPanel;
        }

        private void updateSelector(CommunicationEndpointSelector selector, MessageDTO value) {
            selector.setValue(value.to());
            //selector.refresh(true);
        }

        private MessageDTO getCurrent() {
            CFolderItem<MessageDTO> current = (CFolderItem<MessageDTO>) getParent();
            return current.getValue();
        }

        @Override
        protected MessageDTO preprocessValue(MessageDTO value, boolean fireEvent, boolean populate) {
            if (value != null && value.getPrimaryKey() != null && !value.getPrimaryKey().isDraft()) {
                if (!value.isRead().getValue(true)
                        && value.isInRecipients().getValue(false)
                        && (value.deliveryMethod() == null || value.deliveryMethod().isNull() || !DeliveryMethod.Notification.equals(value.deliveryMethod()
                                .getValue()))) {
                    value.isRead().setValue(true);
                    BoxFolderItemDecorator<MessageDTO> d = (BoxFolderItemDecorator<MessageDTO>) getParent().getDecorator();
                    d.setExpended(true);
                    saveMessage(value, false);
                }

            } else {
                value.isRead().setValue(false);

                if (!value.thread().hasValues()) {
                    value.thread().set(((MessageDTO) parentForm.getValue()).thread());
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
                        parentFolder.addItem();
                        CFolderItem<MessageDTO> newItem = parentFolder.getItem(parentFolder.getItemCount() - 1);
                        newItem.getValue().text().setValue(currentMessage == null ? null : "\nRe:\n" + currentMessage.text().getValue(""));

                        if (!ClientContext.getUserVisit().getName().equals(currentMessage.header().sender().getValue())) {
                            newItem.getValue().to().add(currentMessage.senderDTO());
                        }

                        for (int i = 0; i < parentFolder.getItemCount(); i++) {
                            ((MessageFolderItem) parentFolder.getItem(i).getEntityForm()).setCanReply(false);
                        }
                        newItem.refresh(false);

                        CForm<MessageDTO> form = newItem.getEntityForm();
                        if (form != null && form instanceof MessageFolderItem) {
                            MessageFolderItem folderItemForm = (MessageFolderItem) form;
                            folderItemForm.setFocusForEditingText();
                            updateSelector(folderItemForm.communicationEndpointSelector, newItem.getValue());
                        }

                        newItem.asWidget().getElement().scrollIntoView();

                    }
                }

            });

            btnCancel = new Anchor(i18n.tr("Cancel"), new Command() {
                @Override
                public void execute() {
                    ((MessageFolder) getParent().getParent()).removeItem((CFolderItem<MessageDTO>) getParent());
                    for (int i = 0; i < parentFolder.getItemCount(); i++) {
                        ((MessageFolderItem) parentFolder.getItem(i).getEntityForm()).setCanReply(true);
                    }
                }
            });

            btnMarkAsUnread = new Anchor("Mark as unread", new Command() {
                @Override
                public void execute() {
                    MessageDTO m = getValue();

                    m.isRead().setValue(false);
                    saveMessage(m, false);
                    CrudAppPlace place = new CrmSiteMap.Communication.Message(m.category().categoryType().getValue());
                    place.setType(Type.lister);
                    AppSite.getPlaceController().goTo(place);
                }
            });

            tb.addItem(btnSend);
            tb.addItem(btnReply);
            tb.addItem(btnCancel);
            tb.addItem(btnMarkAsUnread);
            return tb;
        }

        public void saveMessage(MessageDTO m, boolean refresh) {
            com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView.IPrimePanePresenter p = parentForm.getParentView().getPresenter();
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

        protected void setCanReply(boolean canReply) {
            boolean isNew = getValue().isPrototype() || getValue().date() == null || getValue().date().isNull();
            btnReply.setVisible(canReply && getValue().allowedReply().getValue(true) && !isNew);
            btnMarkAsUnread.setVisible(canReply && !isNew && getValue().isInRecipients().getValue(false));
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);
            if (getValue().isPrototype() || getValue().date() == null || getValue().date().isNull()) {
                BoxFolderItemDecorator<DeliveryHandle> d = (BoxFolderItemDecorator<DeliveryHandle>) getParent().getDecorator();
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
                get(proto().header()).setVisible(false);

                attachmentCaption.setVisible(true);
                attachemnts.setVisible(true);
                highImportnaceImage.setVisible(false);
                get(proto().highImportance()).setVisible(true);
                statusToolBar.asWidget().setVisible(false);
                get(proto().onBehalf()).setVisible(true);
                get(proto().onBehalfVisible()).setVisible(true);
            } else {
                setViewable(true);
                setEditable(false);
                setEnabled(false);
                btnSend.setVisible(false);
                btnCancel.setVisible(false);
                btnReply.setVisible(getValue().allowedReply().getValue(true));
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
                get(proto().onBehalf()).setVisible(false);
                get(proto().onBehalfVisible()).setVisible(false);
            }
        }

        private CommunicationEndpointSelector createCommunicationEndpointSelector() {
            return new CommunicationEndpointSelector();
        }

        class TenantSelector extends CEntitySelectorHyperlink<Tenant> {
            @Override
            protected AppPlace getTargetPlace() {
                return AppPlaceEntityMapper.resolvePlace(Tenant.class, getValue().getPrimaryKey());
            }

            @Override
            protected TenantSelectionDialog getSelectorDialog() {
                return new TenantSelectionDialog() {

                    @Override
                    public boolean onClickOk() {
                        setValue(getSelectedItem());
                        return true;
                    }
                };
            }
        }
    }
}
