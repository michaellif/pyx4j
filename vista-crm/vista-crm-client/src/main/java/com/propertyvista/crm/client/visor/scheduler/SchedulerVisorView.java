/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 22, 2015
 * @author arminea
 */
package com.propertyvista.crm.client.visor.scheduler;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.SimpleMessageFormat;
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
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.domain.communication.BroadcastTemplateSchedules;
import com.propertyvista.domain.communication.Schedule;
import com.propertyvista.domain.communication.Schedule.Frequency;

public class SchedulerVisorView extends AbstractVisorPaneView {
    private static final I18n i18n = I18n.get(SchedulerVisorView.class);

    private final SchedulerForm form;

    private enum WeekdayNames {
        Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday
    }

    public SchedulerVisorView(SchedulerVisorController controller) {
        super(controller);
        setCaption(i18n.tr("Template Schedule"));

        form = new SchedulerForm();
        form.init();

        SimplePanel contentPane = new SimplePanel();
        contentPane.getElement().getStyle().setMargin(6, Unit.PX);
        contentPane.setWidget(form.asWidget());
        setContentPane(new ScrollPanel(contentPane));
    }

    public void populate(final Command onPopulate) {
        getController().populate(new DefaultAsyncCallback<EntitySearchResult<Schedule>>() {
            @Override
            public void onSuccess(EntitySearchResult<Schedule> result) {
                BroadcastTemplateSchedules dto = EntityFactory.create(BroadcastTemplateSchedules.class);
                for (Schedule e : result.getData()) {
                    dto.schedules().add(e);
                }
                form.populate(dto);
                onPopulate.execute();
            }
        });
    }

    @Override
    public SchedulerVisorController getController() {
        return (SchedulerVisorController) super.getController();
    }

    public class SchedulerForm extends CForm<BroadcastTemplateSchedules> {

        public SchedulerForm() {
            super(BroadcastTemplateSchedules.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);
            formPanel.append(Location.Dual, proto().schedules(), new ScheduleFolder());
            return formPanel;
        }

        private class ScheduleFolder extends VistaBoxFolder<Schedule> {

            public ScheduleFolder() {
                super(Schedule.class);
                setOrderable(false);
                inheritEditable(false);
                setEditable(true);

            }

            @Override
            protected CForm<Schedule> createItemForm(IObject<?> member) {
                return new ScheduleEditor(true);
            }

            @Override
            protected CFolderItem<Schedule> createItem(boolean first) {
                final CFolderItem<Schedule> item = super.createItem(first);
                item.addAction(ActionType.Cust1, i18n.tr("Edit Schedule"), CrmImages.INSTANCE.editButton(), new Command() {

                    @SuppressWarnings({ "rawtypes" })
                    @Override
                    public void execute() {
                        item.setViewable(false);
                        ((BoxFolderItemDecorator) item.getDecorator()).setExpended(true);
                        ((ScheduleEditor) item.getComponents().toArray()[0]).setViewableMode(false);
                    }
                });

                return item;
            }

            @Override
            public VistaBoxFolderItemDecorator<Schedule> createItemDecorator() {
                VistaBoxFolderItemDecorator<Schedule> decor = super.createItemDecorator();
                decor.setCaptionFormatter(new IFormatter<Schedule, SafeHtml>() {
                    @Override
                    public SafeHtml format(final Schedule value) {
                        StringBuilder stringBuilder = new StringBuilder();

                        final Frequency frequency = value.frequency().getValue();
                        if (frequency != null) {

                            stringBuilder.append(frequency.toString());

                            if (frequency.equals(Frequency.Monthly) && value.onDate().getValue() != null) {
                                stringBuilder.append(" on every " + value.onDate().getValue().getDate());
                            } else if (frequency.equals(Frequency.Weekly) && value.onDate().getValue() != null) {
                                stringBuilder.append(" on every " + WeekdayNames.values()[value.onDate().getValue().getDay()]);
                            }
                            if (value.startDate().getValue() != null) {
                                stringBuilder.append(" starting from " + value.startDate().getValue().toString());
                            }
                            if (value.endDate().getValue() != null) {
                                stringBuilder.append(" to " + value.endDate().getValue().toString());
                            }
                        }
                        final SafeHtmlBuilder builder = new SafeHtmlBuilder();
                        return builder.appendHtmlConstant(SimpleMessageFormat.format("<div>{0}</div>", stringBuilder.toString())).toSafeHtml();
                    }

                });

                decor.setExpended(false);
                return decor;
            }

            @Override
            protected void removeItem(final CFolderItem<Schedule> item) {
                MessageDialog.confirm(i18n.tr("Delete Schedule"), i18n.tr("This Schedule will be permanently deleted!"), new Command() {

                    @Override
                    public void execute() {
                        getController().remove(item.getValue(), new DefaultAsyncCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                ScheduleFolder.super.removeItem(item);
                            }
                        });
                    }
                });
            }

            private class ScheduleEditor extends CForm<Schedule> {

                private Button btnSave;

                private Anchor btnCancel;

                public ScheduleEditor(boolean viewable) {
                    super(Schedule.class);
                    inheritViewable(false);
                    setViewable(viewable);
                }

                @Override
                protected IsWidget createContent() {
                    FormPanel content = new FormPanel(this);
                    content.append(Location.Left, proto().frequency()).decorate();
                    content.append(Location.Right, proto().onDate()).decorate().componentWidth(120);
                    content.append(Location.Left, proto().startDate()).decorate().componentWidth(120);
                    content.append(Location.Right, proto().endDate()).decorate().componentWidth(120);
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
                                ((ScheduleFolder) getParent().getParent()).removeItem((CFolderItem<Schedule>) getParent());
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
                                ((CFolderItem<Schedule>) parent).getItemActionsBar().setVisible(isViewable());
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
