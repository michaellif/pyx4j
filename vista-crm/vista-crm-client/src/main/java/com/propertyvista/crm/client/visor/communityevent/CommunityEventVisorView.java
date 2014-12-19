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
 */
package com.propertyvista.crm.client.visor.communityevent;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
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
import com.pyx4j.forms.client.ui.CContainer;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.folder.ItemActionsBar.ActionType;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.backoffice.ui.visor.AbstractVisorPaneView;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Toolbar;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.visor.notes.NotesAndAttachmentsVisorView;
import com.propertyvista.domain.property.asset.CommunityEvent;
import com.propertyvista.dto.CommunityEventsDTO;

public class CommunityEventVisorView extends AbstractVisorPaneView {
    private static final I18n i18n = I18n.get(NotesAndAttachmentsVisorView.class);

    private final CommunityEventForm form;

    public CommunityEventVisorView(CommunityEventVisorController controller) {
        super(controller);

        setCaption(i18n.tr("Community Events"));

        form = new CommunityEventForm();
        form.init();
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

    public class CommunityEventForm extends CForm<CommunityEventsDTO> {

        public CommunityEventForm() {
            super(CommunityEventsDTO.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);
            formPanel.append(Location.Dual, proto().events(), new CommunityEventFolder());
            return formPanel;
        }

        private class CommunityEventFolder extends VistaBoxFolder<CommunityEvent> {

            public CommunityEventFolder() {
                super(CommunityEvent.class);
                setOrderable(false);
                inheritEditable(false);
                setEditable(true);

            }

            @Override
            protected CForm<CommunityEvent> createItemForm(IObject<?> member) {
                return new EventEditor(true);
            }

            @Override
            protected CFolderItem<CommunityEvent> createItem(boolean first) {
                final CFolderItem<CommunityEvent> item = super.createItem(first);
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
            protected void removeItem(final CFolderItem<CommunityEvent> item) {
                MessageDialog.confirm(i18n.tr("Delete Community Event"), i18n.tr("This Event will be permanently deleted!"), new Command() {

                    @Override
                    public void execute() {
                        getController().remove(item.getValue(), new DefaultAsyncCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                CommunityEventFolder.super.removeItem(item);
                            }
                        });
                    }
                });
            }

            private class EventEditor extends CForm<CommunityEvent> {

                private Button btnSave;

                private Anchor btnCancel;

                public EventEditor(boolean viewable) {
                    super(CommunityEvent.class);
                    inheritViewable(false);
                    setViewable(viewable);
                }

                @Override
                protected IsWidget createContent() {
                    FormPanel content = new FormPanel(this);
                    content.append(Location.Dual, proto().caption()).decorate();
                    content.append(Location.Left, proto().date()).decorate().componentWidth(120);
                    content.append(Location.Left, proto().time()).decorate().componentWidth(120);
                    content.append(Location.Dual, proto().description()).decorate();
                    content.append(Location.Dual, createLowerToolbar());
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
                                ((CommunityEventFolder) getParent().getParent()).removeItem((CFolderItem<CommunityEvent>) getParent());
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
                public void onAdopt(final CContainer<?, ?, ?> parent) {
                    super.onAdopt(parent);
                    addPropertyChangeHandler(new PropertyChangeHandler() {

                        @Override
                        public void onPropertyChange(PropertyChangeEvent event) {
                            if (event.getPropertyName() == PropertyName.viewable) {
                                ((CFolderItem<CommunityEvent>) parent).getItemActionsBar().setVisible(isViewable());
                            }
                        }
                    });
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
