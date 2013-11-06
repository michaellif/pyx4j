/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 22, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.maintenance;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CNumberField;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.dto.MaintenanceRequestScheduleDTO;

public class MaintenanceRequestViewerViewImpl extends CrmViewerViewImplBase<MaintenanceRequestDTO> implements MaintenanceRequestViewerView {

    private static final I18n i18n = I18n.get(MaintenanceRequestViewerViewImpl.class);

    private final Map<StatusPhase, MenuItem> transitionActions;

    private final MenuItem rateAction;

    public MaintenanceRequestViewerViewImpl() {
        setForm(new MaintenanceRequestForm(this));

        rateAction = new MenuItem(i18n.tr("Rate..."), new Command() {
            @Override
            public void execute() {
                new RateBox(getForm().getValue().surveyResponse()) {
                    @Override
                    public boolean onClickOk() {
                        ((MaintenanceRequestViewerView.Presenter) getPresenter()).rateAction(getValue());
                        return true;
                    }
                }.show();
            }
        });
        addAction(rateAction);

        transitionActions = new HashMap<StatusPhase, MenuItem>();
        transitionActions.put(StatusPhase.Scheduled, new MenuItem(i18n.tr("Schedule..."), new Command() {
            @Override
            public void execute() {
                new ScheduleBox() {
                    @Override
                    public boolean onClickOk() {
                        if (validate()) {
                            ((MaintenanceRequestViewerView.Presenter) getPresenter()).scheduleAction(getValue());
                            return true;
                        } else {
                            return false;
                        }
                    }
                }.show();
            }
        }));
        transitionActions.put(StatusPhase.Resolved, new MenuItem(i18n.tr("Resolve"), new Command() {
            @Override
            public void execute() {
                ((MaintenanceRequestViewerView.Presenter) getPresenter()).resolveAction();
            }
        }));
        transitionActions.put(StatusPhase.Cancelled, new MenuItem(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                MessageDialog.confirm(i18n.tr("Cancel"), i18n.tr("Do you really want to cancel the request?"), new Command() {
                    @Override
                    public void execute() {
                        ((MaintenanceRequestViewerView.Presenter) getPresenter()).cancelAction();
                    }
                });
            }
        }));

        for (MenuItem action : transitionActions.values()) {
            addAction(action);
        }
    }

    @Override
    public void reset() {
        setActionVisible(rateAction, false);
        for (MenuItem action : transitionActions.values()) {
            setActionVisible(action, false);
        }
        super.reset();
    }

    @Override
    public void populate(MaintenanceRequestDTO value) {
        super.populate(value);

        StatusPhase phase = value.status().phase().getValue();
        setEditingVisible(!StatusPhase.closed().contains(phase));
        for (StatusPhase transition : phase.transitions()) {
            setActionVisible(transitionActions.get(transition), true);
        }
        setActionVisible(rateAction, phase == StatusPhase.Resolved);
    }

    // Internals:

    private abstract class ScheduleBox extends OkCancelDialog {

        private CEntityForm<MaintenanceRequestScheduleDTO> content;

        public ScheduleBox() {
            super(i18n.tr("Schedule"));
            setBody(createBody());
            setWidth("55em");
        }

        protected Widget createBody() {
            getOkButton().setEnabled(true);

            content = new CEntityForm<MaintenanceRequestScheduleDTO>(MaintenanceRequestScheduleDTO.class) {
                @Override
                public IsWidget createContent() {
                    BasicFlexFormPanel main = new BasicFlexFormPanel();

                    int row = -1;
                    main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().scheduledDate()), 10).build());
                    main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().scheduledTimeFrom()), 10).build());
                    main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().scheduledTimeTo()), 10).build());
                    main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().workDescription()), 25).build());

                    return main;
                }

                @Override
                public void addValidations() {
                    super.addValidations();
                }
            };

            content.initContent();
            content.populate(EntityFactory.create(MaintenanceRequestScheduleDTO.class));
            return content.asWidget();
        }

        public boolean validate() {
            if (content.isValid()) {
                return true;
            } else {
                content.setUnconditionalValidationErrorRendering(true);
                MessageDialog.error(i18n.tr("Error"), content.getValidationResults().getValidationMessage(true, true, true));
                return false;
            }
        }

        public MaintenanceRequestScheduleDTO getValue() {
            return content.getValue();
        }
    }

    private abstract class RateBox extends OkCancelDialog {

        private CEntityForm<SurveyResponse> content;

        public RateBox(SurveyResponse currentRate) {
            super(i18n.tr("Schedule"));
            setBody(createBody(currentRate));
            setWidth("55em");
        }

        protected Widget createBody(SurveyResponse currentRate) {
            getOkButton().setEnabled(true);

            content = new CEntityForm<SurveyResponse>(SurveyResponse.class) {
                @Override
                public IsWidget createContent() {
                    BasicFlexFormPanel main = new BasicFlexFormPanel();

                    main.setWidget(0, 0, new FormDecoratorBuilder(inject(proto().rating()), 3).build());
                    main.setWidget(1, 0, new FormDecoratorBuilder(inject(proto().description()), 25).build());

                    // tweaking:
                    get(proto().rating()).setTooltip(i18n.tr("Set value in range from 1 to 5..."));
                    if (get(proto().rating()) instanceof CNumberField) {
                        ((CNumberField<Integer>) get(proto().rating())).setRange(1, 5);
                    }

                    return main;
                }

                @Override
                public void addValidations() {
                    super.addValidations();
                }
            };

            content.initContent();
            content.populate(currentRate);
            return content.asWidget();
        }

        public SurveyResponse getValue() {
            return content.getValue();
        }
    }
}
