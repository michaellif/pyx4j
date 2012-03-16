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
import com.pyx4j.entity.client.CEntityEditor;
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

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.domain.note.Note;
import com.propertyvista.domain.note.NoteAttachment;
import com.propertyvista.domain.note.NotesAndAttachments;

public class NotesAndAttachmentsEditorForm extends CEntityEditor<NotesAndAttachments> {

    public NotesAndAttachmentsEditorForm() {
        super(NotesAndAttachments.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();
        int row = -1;

        main.setWidget(++row, 0, inject(proto().notes(), new NoteAndAttachmentsEditorFolder()));

        return main;
    }

    private static class NoteAndAttachmentsEditorFolder extends VistaBoxFolder<Note> {

        private static final I18n i18n = I18n.get(NoteAndAttachmentsEditorFolder.class);

        protected boolean newItemAdded = false;

        public NoteAndAttachmentsEditorFolder() {
            super(Note.class);

        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof Note) {
                return new NoteEditor(!newItemAdded);
            } else {
                return super.create(member);
            }
        }

        @Override
        public IFolderItemDecorator<Note> createItemDecorator() {
            BoxFolderItemDecorator<Note> decorator = (BoxFolderItemDecorator<Note>) super.createItemDecorator();
            decorator.setTitle(i18n.tr("Add Note"));

            return decorator;
        }

        @Override
        protected CEntityFolderItem<Note> createItem(boolean first) {
            final CEntityFolderItem<Note> item = super.createItem(first);
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
        protected void addItem(Note newEntity) {
            newItemAdded = newEntity.isEmpty();

            super.addItem(newEntity);
        }

        private static class NoteEditor extends CEntityDecoratableEditor<Note> {

            private static final I18n i18n = I18n.get(NoteEditor.class);

            private Button btnSave;

            private final boolean viewable;

            private AnchorButton btnCancel;

            public NoteEditor(boolean viewable) {
                super(Note.class);

                this.viewable = viewable;
            }

            @Override
            public IsWidget createContent() {

                FormFlexPanel content = new FormFlexPanel();
                int row = -1;

                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description())).build());
                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().note())).build());

                content.setH3(++row, 0, 1, i18n.tr("Attachments"));
                content.setWidget(++row, 0, inject(proto().attachments(), new AttachmentsEditorFolder()));

                Toolbar tb = new Toolbar();

                btnSave = new Button(i18n.tr("Save"), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        if (getValue().created().getValue() == null)
                            getValue().created().setValue(new LogicalDate());

                        setViewable(true);
                        setButtonsVisible(false);
                    }
                });

                btnSave.setVisible(false);
                tb.addItem(btnSave);

                btnCancel = new AnchorButton(i18n.tr("Cancel"), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
//                        getPresenter().cancel();

                        if (getValue().isEmpty())
                            ((NoteAndAttachmentsEditorFolder) getParent().getParent()).removeItem((CEntityFolderItem<Note>) getParent());
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

        private static class AttachmentsEditorFolder extends VistaBoxFolder<NoteAttachment> {

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

            private static class AttachmentEditor extends CEntityDecoratableEditor<NoteAttachment> {

                private static final I18n i18n = I18n.get(NoteAttachment.class);

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

}
