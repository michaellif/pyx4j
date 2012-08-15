/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 3, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.client.visor.notes;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.folder.BoxFolderDecorator;
import com.pyx4j.entity.client.ui.folder.CEntityFolderItem;
import com.pyx4j.entity.client.ui.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.IconButton;
import com.pyx4j.widgets.client.actionbar.Toolbar;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.note.NoteAttachment;
import com.propertyvista.domain.note.NotesAndAttachments;
import com.propertyvista.domain.note.NotesAndAttachmentsDTO;
import com.propertyvista.domain.security.CrmUser;

public class NotesAndAttachmentsVisorView extends ScrollPanel {

    private static final I18n i18n = I18n.get(NotesAndAttachmentsVisorView.class);

    private final NotesAndAttachmentsVisorController controller;

    private final NotesAndAttachmentsForm form;

    public NotesAndAttachmentsVisorView(NotesAndAttachmentsVisorController controller) {
        super();
        this.controller = controller;
        form = new NotesAndAttachmentsForm();
        form.initContent();
        setWidget(form.asWidget());
        getElement().getStyle().setProperty("padding", "6px");
    }

    public void populate(final Command onPopulate) {
        controller.populate(new DefaultAsyncCallback<EntitySearchResult<NotesAndAttachments>>() {
            @Override
            public void onSuccess(EntitySearchResult<NotesAndAttachments> result) {
                NotesAndAttachmentsDTO dto = EntityFactory.create(NotesAndAttachmentsDTO.class);
                for (NotesAndAttachments na : result.getData()) {
                    dto.notes().add(na);
                }
                form.populate(dto);
                onPopulate.execute();
            }
        });
    }

    public NotesAndAttachmentsVisorController getController() {
        return controller;
    }

    public class NotesAndAttachmentsForm extends CrmEntityForm<NotesAndAttachmentsDTO> {

        public NotesAndAttachmentsForm() {
            super(NotesAndAttachmentsDTO.class);

        }

        @Override
        public IsWidget createContent() {
            return inject(proto().notes(), new NotesAndAttachmentsFolder());
        }

        public class NotesAndAttachmentsFolder extends VistaBoxFolder<NotesAndAttachments> {

            public NotesAndAttachmentsFolder() {
                super(NotesAndAttachments.class);
                setOrderable(false);
                inheritEditable(false);
                setEditable(true);

            }

            @Override
            public CComponent<?, ?> create(IObject<?> member) {
                if (member instanceof NotesAndAttachments) {
                    return new NoteEditor(true);
                } else {
                    return super.create(member);
                }
            }

            @Override
            public IFolderItemDecorator<NotesAndAttachments> createItemDecorator() {
                return new VistaBoxFolderItemDecorator<NotesAndAttachments>(this) {
                    @Override
                    public void setComponent(final CEntityFolderItem<NotesAndAttachments> folderItem) {
                        super.setComponent(folderItem);
                        final NoteEditor editor = (NoteEditor) getContent();
                        editor.addPropertyChangeHandler(new PropertyChangeHandler() {

                            @Override
                            public void onPropertyChange(PropertyChangeEvent event) {
                                if (event.getPropertyName() == PropertyName.viewable) {
                                    folderItem.getItemActionsBar().setVisible(editor.isViewable());
                                }
                            }
                        });
                    }
                };
            }

            @Override
            protected CEntityFolderItem<NotesAndAttachments> createItem(boolean first) {
                final CEntityFolderItem<NotesAndAttachments> item = super.createItem(first);
                IconButton button = new IconButton(i18n.tr("Edit Note"), CrmImages.INSTANCE.editButton());
                button.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        item.setViewable(false);
                        ((NoteEditor) item.getComponents().toArray()[0]).setViewableMode(false);
                    }
                });

                item.addCustomButton(button);

                return item;
            }

            @Override
            protected void removeItem(final CEntityFolderItem<NotesAndAttachments> item) {
                Dialog confirm = new OkCancelDialog(i18n.tr("Delete Note")) {
                    @Override
                    public boolean onClickOk() {
                        if (item.getValue().isNull() || item.getValue().getPrimaryKey() == null) {
                            NotesAndAttachmentsFolder.super.removeItem(item);
                        } else {
                            controller.remove(item.getValue(), new DefaultAsyncCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean result) {
                                    NotesAndAttachmentsFolder.super.removeItem(item);
                                }
                            });
                        }
                        return true;
                    }
                };
                confirm.setBody(new HTML(i18n.tr("This Note will be permanently deleted!")));
                confirm.show();
            }

            private class NoteEditor extends CEntityDecoratableForm<NotesAndAttachments> {

                private Button btnSave;

                private final boolean viewable;

                private AnchorButton btnCancel;

                public NoteEditor(boolean viewable) {
                    super(NotesAndAttachments.class);

                    this.viewable = viewable;
                    inheritViewable(false);
                    setViewable(viewable);
                }

                @Override
                public IsWidget createContent() {

                    FormFlexPanel content = new FormFlexPanel();
                    int row = -1;

                    content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().subject())).build());
                    content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().note())).build());

                    CComponent<?, ?> comp = inject(proto().created());
                    comp.inheritViewable(false);
                    comp.setViewable(true);
                    content.setWidget(++row, 0, new DecoratorBuilder(comp).build());
                    comp = inject(proto().updated());
                    comp.inheritViewable(false);
                    comp.setViewable(true);
                    content.setWidget(++row, 0, new DecoratorBuilder(comp).build());
                    comp = inject(proto().user().name());
                    comp.inheritViewable(false);
                    comp.setViewable(true);
                    content.setWidget(++row, 0, new DecoratorBuilder(comp).build());

                    content.setH3(++row, 0, 1, i18n.tr("Attachments"));
                    content.setWidget(++row, 0, inject(proto().attachments(), new AttachmentsEditorFolder()));

                    Toolbar tb = new Toolbar();

                    btnSave = new Button(i18n.tr("Save"), new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            if (getValue().created().getValue() == null) {
                                CrmUser user = EntityFactory.create(CrmUser.class);
                                user.setPrimaryKey(ClientContext.getUserVisit().getPrincipalPrimaryKey());
                                user.name().setValue(ClientContext.getUserVisit().getName());
                                getValue().user().set(user);
                                getValue().created().setValue(new LogicalDate());
                            } else {
                                getValue().updated().setValue(new LogicalDate());
                            }
                            getController().save(getValue(), new DefaultAsyncCallback<Key>() {
                                @Override
                                public void onSuccess(Key result) {
                                    getValue().setPrimaryKey(result);
                                    setViewableMode(true);
                                    //TODO do we really need that?
                                    refresh(false);
                                }
                            });
                        }
                    });

                    btnSave.setVisible(false);
                    tb.addItem(btnSave);

                    btnCancel = new AnchorButton(i18n.tr("Cancel"), new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
//                            getPresenter().cancel();

                            if (getValue().isEmpty())
                                ((NotesAndAttachmentsFolder) getParent().getParent()).removeItem((CEntityFolderItem<NotesAndAttachments>) getParent());
                            else {
                                MessageDialog.confirm(i18n.tr("Confirm"),
                                        i18n.tr("Are you sure you want to cancel your changes?\n\nPress Yes to continue, or No to stay on the current page."),
                                        new Command() {

                                            @Override
                                            public void execute() {
                                                setViewableMode(true);
                                            }
                                        });
                            }
                        }
                    });

                    btnCancel.setVisible(false);
                    tb.addItem(btnCancel);

                    content.setWidget(++row, 0, tb);

                    setViewableMode(viewable);

                    return content;
                }

                @Override
                protected void onValueSet(boolean populate) {
                    super.onValueSet(populate);
                    if (getValue().isEmpty()) {
                        setViewableMode(false);
                    }
                    if (!isOwner()) {
                        @SuppressWarnings("unchecked")
                        CEntityFolderItem<NotesAndAttachments> item = (CEntityFolderItem<NotesAndAttachments>) getParent();
                        item.setRemovable(false);
                    }
                }

                private void setButtonsVisible(boolean visible) {
                    btnSave.setVisible(visible);
                    btnCancel.setVisible(visible);
                }

                public boolean isOwner() {
                    Key ownerKey = (getValue().user() != null ? getValue().user().getPrimaryKey() : null);
                    return (ownerKey == null || ownerKey.equals(ClientContext.getUserVisit().getPrincipalPrimaryKey()));
                }

                public void setViewableMode(boolean isViewable) {
                    // to allow editing first check ownership
                    if (!isViewable && !isOwner()) {
                        isViewable = true;
                    }
                    setButtonsVisible(!isViewable);
                    setViewable(isViewable);
                }

            }

            private class AttachmentsEditorFolder extends VistaBoxFolder<NoteAttachment> {

                public AttachmentsEditorFolder() {
                    super(NoteAttachment.class);

                }

                @Override
                public CComponent<?, ?> create(IObject<?> member) {
                    if (member instanceof NoteAttachment) {
                        return new AttachmentEditor();
                    } else {
                        return super.create(member);
                    }
                }

                @Override
                protected IFolderDecorator<NoteAttachment> createDecorator() {
                    BoxFolderDecorator<NoteAttachment> decorator = (BoxFolderDecorator<NoteAttachment>) super.createDecorator();
                    decorator.setTitle(i18n.tr("Add Attachment"));

                    return decorator;

                }

                private class AttachmentEditor extends CEntityDecoratableForm<NoteAttachment> {

                    public AttachmentEditor() {
                        super(NoteAttachment.class);
                    }

                    @Override
                    public IsWidget createContent() {

                        FormFlexPanel content = new FormFlexPanel();
                        int row = -1;

                        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description())).build());
                        //content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().file())).build());

                        return content;
                    }
                }
            }
        }

        @Override
        protected void createTabs() {
            // TODO Auto-generated method stub
        }

    }

}
