/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 4, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.notesandattachments;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.folder.BoxFolderDecorator;
import com.pyx4j.entity.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.folder.CEntityFolderItem;
import com.pyx4j.entity.client.ui.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.IconButton;
import com.pyx4j.widgets.client.actionbar.Toolbar;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.note.NoteAttachment;
import com.propertyvista.domain.note.NotesAndAttachments;
import com.propertyvista.domain.note.NotesAndAttachmentsDTO;

public class NotesAndAttachmentsForm extends CrmEntityForm<NotesAndAttachmentsDTO> {

    private static final I18n i18n = I18n.get(NotesAndAttachmentsForm.class);

    public NotesAndAttachmentsForm() {
        super(NotesAndAttachmentsDTO.class);
    }

    @Override
    public IsWidget createContent() {
        return inject(proto().notes(), new NotesAndAttachmentsFolder());
    }

    public static class NotesAndAttachmentsFolder extends VistaBoxFolder<NotesAndAttachments> {

        protected boolean newItemAdded = false;

        public NotesAndAttachmentsFolder() {
            super(NotesAndAttachments.class);
            setEditable(true);
            inheritEditable(false);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof NotesAndAttachments) {
                return new NoteEditor(false);
            } else {
                return super.create(member);
            }
        }

        @Override
        public IFolderItemDecorator<NotesAndAttachments> createItemDecorator() {
            BoxFolderItemDecorator<NotesAndAttachments> decorator = (BoxFolderItemDecorator<NotesAndAttachments>) super.createItemDecorator();
            decorator.setTitle(i18n.tr("Add Note"));

            return decorator;
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
        protected void addItem(NotesAndAttachments newEntity) {
            newItemAdded = newEntity.isEmpty();

            super.addItem(newEntity);
        }

        private class NoteEditor extends CEntityDecoratableForm<NotesAndAttachments> {

            private Button btnSave;

            private final boolean viewable;

            private AnchorButton btnCancel;

            public NoteEditor(boolean viewable) {
                super(NotesAndAttachments.class);

                this.viewable = viewable;
                setViewable(viewable);
                inheritViewable(false);
            }

            @Override
            public IsWidget createContent() {

                FormFlexPanel content = new FormFlexPanel();
                int row = -1;

                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().subject())).build());
                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().note())).build());

                CComponent<?, ?> comp = inject(proto().created());
                comp.setViewable(true);
                content.setWidget(++row, 0, new DecoratorBuilder(comp).build());
                comp = inject(proto().updated());
                comp.setViewable(true);
                content.setWidget(++row, 0, new DecoratorBuilder(comp).build());
                comp = inject(proto().user().name());
                comp.setViewable(true);
                content.setWidget(++row, 0, new DecoratorBuilder(comp).build());

                content.setH3(++row, 0, 1, i18n.tr("Attachments"));
                content.setWidget(++row, 0, inject(proto().attachments(), new AttachmentsEditorFolder()));

                Toolbar tb = new Toolbar();

                btnSave = new Button(i18n.tr("Save"), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        if (getValue().created().getValue() == null)
                            getValue().created().setValue(new LogicalDate());

                        setViewableMode(true);
                    }
                });

                btnSave.setVisible(false);
                tb.addItem(btnSave);

                btnCancel = new AnchorButton(i18n.tr("Cancel"), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
//                        getPresenter().cancel();

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

            public void setButtonsVisible(boolean visible) {
                btnSave.setVisible(visible);
                btnCancel.setVisible(visible);
            }

            public void setViewableMode(boolean isViewable) {
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
