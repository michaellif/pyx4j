/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 22, 2012
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.visor.communityevent;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.ItemActionsBar.ActionType;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.visor.AbstractVisorPane;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Toolbar;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.visor.notes.NotesAndAttachmentsVisorView;
import com.propertyvista.domain.property.asset.CommunityEvent;
import com.propertyvista.dto.CommunityEventsDTO;

public class CommunityEventVisorView extends AbstractVisorPane {
    private static final I18n i18n = I18n.get(NotesAndAttachmentsVisorView.class);

    private final CommunityEventForm form;

    public CommunityEventVisorView(CommunityEventVisorController controller) {
        super(controller);

        form = new CommunityEventForm();
        form.initContent();
        SimplePanel contentPane = new SimplePanel();
        contentPane.getElement().getStyle().setMargin(6, Unit.PX);
        contentPane.setWidget(form.asWidget());
        setContentPane(new ScrollPanel(contentPane));
    }

    public void populate(final Command onPopulate) {
        getController().populate(new DefaultAsyncCallback<EntitySearchResult<CommunityEvent>>() {
            @Override
            public void onSuccess(EntitySearchResult<CommunityEvent> result) {
                CommunityEventsDTO dto = EntityFactory.create(CommunityEventsDTO.class);
                for (CommunityEvent e : result.getData()) {
                    dto.events().add(e);
                }
                form.populate(dto);
                onPopulate.execute();
            }
        });
    }

    @Override
    public CommunityEventVisorController getController() {
        return (CommunityEventVisorController) super.getController();
    }

    public class CommunityEventForm extends CEntityForm<CommunityEventsDTO> {

        public CommunityEventForm() {
            super(CommunityEventsDTO.class);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

            content.setWidget(0, 0, 2, inject(proto().events(), new CommunityEventFolder()));

            return content;
        }

        private class CommunityEventFolder extends VistaBoxFolder<CommunityEvent> {

            public CommunityEventFolder() {
                super(CommunityEvent.class);
                setOrderable(false);
                inheritEditable(false);
                setEditable(true);

            }

            @Override
            public CComponent<?> create(IObject<?> member) {
                if (member instanceof CommunityEvent) {
                    return new EventEditor(true);
                } else {
                    return super.create(member);
                }
            }

            @Override
            public IFolderItemDecorator<CommunityEvent> createItemDecorator() {
                return new VistaBoxFolderItemDecorator<CommunityEvent>(this) {
                    @Override
                    public void setComponent(final CEntityFolderItem<CommunityEvent> folderItem) {
                        super.setComponent(folderItem);
                        final EventEditor editor = (EventEditor) getContent();
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
            protected CEntityFolderItem<CommunityEvent> createItem(boolean first) {
                final CEntityFolderItem<CommunityEvent> item = super.createItem(first);
                item.addAction(ActionType.Cust1, i18n.tr("Edit Community Event"), CrmImages.INSTANCE.editButton(), new Command() {

                    @SuppressWarnings("rawtypes")
                    @Override
                    public void execute() {
                        item.setViewable(false);
                        ((BoxFolderItemDecorator) item.getDecorator()).setExpended(true);
                        ((EventEditor) item.getComponents().toArray()[0]).setViewableMode(false);
                    }
                });

                return item;
            }

            @Override
            protected void removeItem(final CEntityFolderItem<CommunityEvent> item) {
                Dialog confirm = new OkCancelDialog(i18n.tr("Delete Community Event")) {
                    @Override
                    public boolean onClickOk() {
                        getController().remove(item.getValue(), new DefaultAsyncCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                CommunityEventFolder.super.removeItem(item);
                            }
                        });
                        return true;
                    }
                };
                confirm.setBody(new HTML(i18n.tr("This Event will be permanently deleted!")));
                confirm.show();
            }

            private class EventEditor extends CEntityForm<CommunityEvent> {

                private Button btnSave;

                private Anchor btnCancel;

                public EventEditor(boolean viewable) {
                    super(CommunityEvent.class);
                    inheritViewable(false);
                    setViewable(viewable);
                }

                @Override
                public IsWidget createContent() {
                    TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
                    int row = -1;

                    content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().caption()), 20, true).build());
                    content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().date()), 10, true).build());
                    content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().time()), 10, true).build());
                    content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().description()), 50, true).build());
                    content.setWidget(++row, 0, 2, createLowerToolbar());
                    content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);

                    return content;
                }

                protected Toolbar createLowerToolbar() {
                    Toolbar tb = new Toolbar();

                    btnSave = new Button(i18n.tr("Save"), new Command() {
                        @Override
                        public void execute() {
                            setVisitedRecursive();
                            if (!isValid()) {
                                MessageDialog.error(i18n.tr("Error"), getValidationResults().getValidationMessage(true));
                            } else {

                                getController().save(getValue(), new DefaultAsyncCallback<Key>() {
                                    @Override
                                    public void onSuccess(Key result) {
                                        getValue().setPrimaryKey(result);
                                        setViewableMode(true);
                                        refresh(true);
                                    }
                                });
                            }
                        }
                    });

                    tb.addItem(btnSave);
                    btnSave.setVisible(false);

                    btnCancel = new Anchor(i18n.tr("Cancel"), new Command() {
                        @Override
                        public void execute() {
                            if (getValue().getPrimaryKey() == null) {
                                ((CommunityEventFolder) getParent().getParent()).removeItem((CEntityFolderItem<CommunityEvent>) getParent());
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

                    tb.addItem(btnCancel);
                    btnCancel.setVisible(false);
                    return tb;
                }

                @Override
                protected void onValueSet(boolean populate) {
                    if (getValue().isEmpty()) {
                        setViewableMode(false);
                    }
                }

                private void setButtonsVisible(boolean visible) {
                    btnSave.setVisible(visible);
                    btnCancel.setVisible(visible);
                }

                public void setViewableMode(boolean isViewable) {
                    setButtonsVisible(!isViewable);
                    setViewable(isViewable);
                }
            }
        }
    }
}
