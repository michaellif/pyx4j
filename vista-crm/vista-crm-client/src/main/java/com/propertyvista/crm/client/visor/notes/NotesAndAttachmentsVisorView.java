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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CContainer;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.BoxFolderDecorator;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.ItemActionsBar.ActionType;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.ui.visor.AbstractVisorPane;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Toolbar;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.rpc.services.NoteAttachmentUploadService;
import com.propertyvista.domain.note.HasNotesAndAttachments;
import com.propertyvista.domain.note.NoteAttachment;
import com.propertyvista.domain.note.NotesAndAttachments;
import com.propertyvista.domain.note.NotesAndAttachmentsDTO;
import com.propertyvista.domain.security.CrmUser;

public class NotesAndAttachmentsVisorView extends AbstractVisorPane {

    private static final I18n i18n = I18n.get(NotesAndAttachmentsVisorView.class);

    private final NotesAndAttachmentsForm form;

    public NotesAndAttachmentsVisorView(NotesAndAttachmentsVisorController controller) {
        super(controller);

        setCaption(i18n.tr("Notes & Attachments"));

        form = new NotesAndAttachmentsForm();
        form.init();

        SimplePanel contentPane = new SimplePanel();
        contentPane.getElement().getStyle().setMargin(6, Unit.PX);
        contentPane.setWidget(form.asWidget());
        setContentPane(new ScrollPanel(contentPane));
    }

    public void populate(final Command onPopulate) {
        getController().populate(new DefaultAsyncCallback<EntitySearchResult<NotesAndAttachments>>() {
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

    @Override
    public NotesAndAttachmentsVisorController getController() {
        return (NotesAndAttachmentsVisorController) super.getController();
    }

    public class NotesAndAttachmentsForm extends CForm<NotesAndAttachmentsDTO> {

        public NotesAndAttachmentsForm() {
            super(NotesAndAttachmentsDTO.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);
            formPanel.append(Location.Dual, proto().notes(), new NotesAndAttachmentsFolder());
            return formPanel;
        }

        private class NotesAndAttachmentsFolder extends VistaBoxFolder<NotesAndAttachments> {

            public NotesAndAttachmentsFolder() {
                super(NotesAndAttachments.class);
                setOrderable(false);
                inheritEditable(false);
                // TODO  this BAD  change this
                {
                    setEditable(SecurityController.check(DataModelPermission.permissionUpdate(HasNotesAndAttachments.class)));
                    setAddable(SecurityController.check(DataModelPermission.permissionCreate(HasNotesAndAttachments.class)));
                }
            }

            @Override
            protected CForm<NotesAndAttachments> createItemForm(IObject<?> member) {
                return new NoteEditor(true);
            }

            @Override
            public IFolderItemDecorator<NotesAndAttachments> createItemDecorator() {
                return new VistaBoxFolderItemDecorator<NotesAndAttachments>(this);
            }

            @Override
            protected CFolderItem<NotesAndAttachments> createItem(boolean first) {
                final CFolderItem<NotesAndAttachments> item = super.createItem(first);

                if (SecurityController.check(DataModelPermission.permissionUpdate(HasNotesAndAttachments.class))) {
                    item.addAction(ActionType.Cust1, i18n.tr("Edit Note"), CrmImages.INSTANCE.editButton(), new Command() {
                        @SuppressWarnings("rawtypes")
                        @Override
                        public void execute() {
                            item.setViewable(false);
                            ((BoxFolderItemDecorator) item.getDecorator()).setExpended(true);
                            ((NoteEditor) item.getComponents().toArray()[0]).setViewableMode(false);
                        }
                    });
                }

                return item;
            }

            @Override
            protected void removeItem(final CFolderItem<NotesAndAttachments> item) {
                Dialog confirm = new OkCancelDialog(i18n.tr("Delete Note")) {
                    @Override
                    public boolean onClickOk() {
                        getController().remove(item.getValue(), new DefaultAsyncCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                NotesAndAttachmentsFolder.super.removeItem(item);
                            }
                        });
                        return true;
                    }
                };
                confirm.setBody(new HTML(i18n.tr("This Note will be permanently deleted!")));
                confirm.show();
            }

            private class NoteEditor extends CForm<NotesAndAttachments> {

                private Button btnSave;

                private Anchor btnCancel;

                private AttachmentsEditorFolder attachmentsFolder;

                public NoteEditor(boolean viewable) {
                    super(NotesAndAttachments.class);

                    inheritViewable(false);
                    setViewable(viewable);
                }

                @Override
                protected IsWidget createContent() {
                    FormPanel formPanel = new FormPanel(this);

                    formPanel.append(Location.Dual, proto().subject()).decorate();
                    formPanel.append(Location.Dual, proto().note()).decorate();

                    formPanel.append(Location.Left, proto().created()).decorate().componentWidth(120);
                    formPanel.append(Location.Right, proto().updated()).decorate().componentWidth(120);

                    formPanel.append(Location.Left, proto().user(), new CEntityLabel<CrmUser>()).decorate().componentWidth(200);

                    formPanel.h3(i18n.tr("Attachments"));
                    formPanel.append(Location.Dual, proto().attachments(), attachmentsFolder = new AttachmentsEditorFolder());

                    formPanel.append(Location.Right, createLowerToolbar());

                    return formPanel;
                }

                protected Toolbar createLowerToolbar() {
                    Toolbar tb = new Toolbar();

                    btnSave = new Button(i18n.tr("Save"), new Command() {
                        @Override
                        public void execute() {
                            setVisitedRecursive();
                            if (!isValid()) {
                                MessageDialog.error(i18n.tr("Error"), i18n.tr("There has been an error. Please check your data and try again."));
                            } else {
                                if (getValue().created().getValue() == null) {
                                    CrmUser user = EntityFactory.create(CrmUser.class);
                                    user.setPrimaryKey(ClientContext.getUserVisit().getPrincipalPrimaryKey());
                                    user.name().setValue(ClientContext.getUserVisit().getName());
                                    getValue().user().set(user);
                                    getValue().created().setValue(new LogicalDate(ClientContext.getServerDate()));
                                } else {
                                    getValue().updated().setValue(new LogicalDate(ClientContext.getServerDate()));
                                }
                                getController().save(getValue(), new DefaultAsyncCallback<Key>() {
                                    @Override
                                    public void onSuccess(Key result) {
                                        getValue().setPrimaryKey(result);
                                        setViewableMode(true);
                                        refresh(false);
                                    }
                                });
                            }
                        }
                    });

                    btnSave.setVisible(false);
                    tb.addItem(btnSave);

                    btnCancel = new Anchor(i18n.tr("Cancel"), new Command() {
                        @Override
                        public void execute() {
                            if (getValue().getPrimaryKey() == null) {
                                ((NotesAndAttachmentsFolder) getParent().getParent()).removeItem((CFolderItem<NotesAndAttachments>) getParent());
                            } else {
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

                    return tb;
                }

                @Override
                public void onAdopt(final CContainer<?, ?, ?> parent) {
                    super.onAdopt(parent);
                    addPropertyChangeHandler(new PropertyChangeHandler() {

                        @Override
                        public void onPropertyChange(PropertyChangeEvent event) {
                            if (event.getPropertyName() == PropertyName.viewable) {
                                ((CFolderItem<NotesAndAttachments>) parent).getItemActionsBar().setVisible(isViewable());
                            }
                        }
                    });
                }

                @Override
                protected void onValueSet(boolean populate) {
                    super.onValueSet(populate);
                    if (getValue().isEmpty()) {
                        setViewableMode(false);
                    }

                    if (!isOwner()) {
                        @SuppressWarnings("unchecked")
                        CFolderItem<NotesAndAttachments> item = (CFolderItem<NotesAndAttachments>) getParent();
                        item.setRemovable(false);
                        item.getItemActionsBar().setButtonVisible(ActionType.Cust1, false);
                    }

                    get(proto().created()).setVisible(!getValue().created().isNull());
                    get(proto().updated()).setVisible(!getValue().updated().isNull());
                    get(proto().user()).setVisible(!getValue().user().isNull());
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
                    attachmentsFolder.setViewableMode(isViewable);
                }
            }

            private class AttachmentsEditorFolder extends VistaBoxFolder<NoteAttachment> {

                public AttachmentsEditorFolder() {
                    super(NoteAttachment.class);
                    setViewableMode(true);
                }

                public void setViewableMode(boolean isViewable) {
                    setViewable(isViewable);
                    setOrderable(!isViewable);
                    setAddable(!isViewable);
                }

                @Override
                protected CForm<NoteAttachment> createItemForm(IObject<?> member) {
                    return new AttachmentEditor();
                }

                @Override
                protected IFolderDecorator<NoteAttachment> createFolderDecorator() {
                    BoxFolderDecorator<NoteAttachment> decorator = (BoxFolderDecorator<NoteAttachment>) super.createFolderDecorator();
                    decorator.setTitle(i18n.tr("Add Attachment"));
                    return decorator;
                }

                private class AttachmentEditor extends CForm<NoteAttachment> {

                    public AttachmentEditor() {
                        super(NoteAttachment.class);
                    }

                    @Override
                    protected IsWidget createContent() {
                        FormPanel formPanel = new FormPanel(this);

                        CFile cfile = new CFile(GWT.<UploadService<?, ?>> create(NoteAttachmentUploadService.class), new VistaFileURLBuilder(
                                NoteAttachment.class));
                        formPanel.append(Location.Dual, proto().file(), cfile).decorate();

                        return formPanel;
                    }
                }
            }
        }
    }
}
