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
 */
package com.propertyvista.crm.client.ui.crud.maintenance;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CNumberField;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.AbstractValidationError;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button.SecureMenuItem;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.PrintUtils;
import com.propertyvista.common.client.ui.validators.FutureDateIncludeTodayValidator;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.services.maintenance.ac.Cancel;
import com.propertyvista.crm.rpc.services.maintenance.ac.Resolve;
import com.propertyvista.crm.rpc.services.maintenance.ac.Schedule;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.domain.policy.policies.MaintenanceRequestPolicy;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.dto.MaintenanceRequestWorkOrderDTO;

public class MaintenanceRequestViewerViewImpl extends CrmViewerViewImplBase<MaintenanceRequestDTO> implements MaintenanceRequestViewerView {

    private static final I18n i18n = I18n.get(MaintenanceRequestViewerViewImpl.class);

    private final Map<StatusPhase, MenuItem> transitionActions;

    private final MenuItem rateAction;

    public MaintenanceRequestViewerViewImpl() {
        setForm(new MaintenanceRequestForm(this));

        MenuItem btnPrint = new MenuItem(i18n.tr("Print"), new Command() {
            @Override
            public void execute() {
                PrintUtils.print(MaintenanceRequestViewerViewImpl.this.getForm().getPrintableElement());
            }
        });
        addAction(btnPrint);

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
        transitionActions.put(StatusPhase.Scheduled, new SecureMenuItem(i18n.tr("Schedule..."), new Command() {
            @Override
            public void execute() {
                new ScheduleBox(getForm().getValue().policy()) {
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
        }, Schedule.class));
        transitionActions.put(StatusPhase.Resolved, new SecureMenuItem(i18n.tr("Resolve"), new Command() {
            @Override
            public void execute() {
                new ResolutionBox(getForm().getValue()) {
                    @Override
                    public boolean onClickOk() {
                        if (validate()) {
                            ((MaintenanceRequestViewerView.Presenter) getPresenter()).resolveAction(getValue());
                            return true;
                        } else {
                            return false;
                        }
                    }
                }.show();
            }
        }, Resolve.class));
        transitionActions.put(StatusPhase.Cancelled, new SecureMenuItem(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                MessageDialog.confirm(i18n.tr("Cancel"), i18n.tr("Do you really want to cancel the request?"), new Command() {
                    @Override
                    public void execute() {
                        ((MaintenanceRequestViewerView.Presenter) getPresenter()).cancelAction();
                    }
                });
            }
        }, Cancel.class));

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

    static abstract class ScheduleBox extends OkCancelDialog {

        private CForm<MaintenanceRequestWorkOrderDTO> content;

        private final MaintenanceRequestPolicy policy;

        public ScheduleBox(MaintenanceRequestPolicy policy) {
            super(i18n.tr("Schedule"));
            this.policy = policy;
            setBody(createBody());
        }

        protected Widget createBody() {
            getOkButton().setEnabled(true);

            content = new CForm<MaintenanceRequestWorkOrderDTO>(MaintenanceRequestWorkOrderDTO.class) {
                @Override
                protected IsWidget createContent() {
                    FormPanel main = new FormPanel(this);

                    main.append(Location.Dual, inject(proto().isEmergencyWork())).decorate();
                    main.append(Location.Dual, inject(proto().scheduledDate())).decorate().componentWidth(120);
                    main.append(Location.Dual, inject(proto().scheduledTime().timeFrom())).decorate().componentWidth(120);
                    main.append(Location.Dual, inject(proto().scheduledTime().timeTo())).decorate().componentWidth(120);
                    main.append(Location.Dual, inject(proto().workDescription())).decorate().componentWidth(200);

                    get(proto().isEmergencyWork()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {

                        @Override
                        public void onValueChange(ValueChangeEvent<Boolean> event) {
                            get(proto().scheduledTime().timeFrom()).revalidate();
                            get(proto().scheduledTime().timeTo()).revalidate();
                        }
                    });

                    get(proto().scheduledDate()).addComponentValidator(new FutureDateIncludeTodayValidator());
                    if (!policy.minAdvanceNoticeHours().isNull()) {
                        get(proto().scheduledDate()).setTooltip(
                                i18n.tr("Work must be scheduled at least {0} hour in advance", policy.minAdvanceNoticeHours().getValue()));
                        get(proto().scheduledDate()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {

                            @Override
                            public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                                get(proto().scheduledTime().timeFrom()).revalidate();
                            }
                        });
                        // add Advance Notice validator
                        get(proto().scheduledTime().timeFrom()).addComponentValidator(new AbstractComponentValidator<Time>() {

                            @Override
                            public AbstractValidationError isValid() {
                                MaintenanceRequestWorkOrderDTO wo = (MaintenanceRequestWorkOrderDTO) getCComponent().getParent().getValue();
                                // if not an emergency - ensure advanced notice
                                if (!wo.isEmergencyWork().getValue(false)) {
                                    Time from = getCComponent().getValue();
                                    if (from != null) {
                                        Date fromDate = get(proto().scheduledDate()).getValue();
                                        if (fromDate == null) {
                                            fromDate = TimeUtils.today();
                                        }
                                        long timeOffsetMills = from.getTime() - new Time(0, 0, 0).getTime(); // offset regardless of TZ
                                        long fromMills = fromDate.getTime() + timeOffsetMills;
                                        long noticeOffsetMills = 3600000 * policy.minAdvanceNoticeHours().getValue();
                                        if (fromMills < System.currentTimeMillis() + noticeOffsetMills) {
                                            return new BasicValidationError(getCComponent(), i18n.tr("Work must be scheduled at least {0} hour in advance",
                                                    policy.minAdvanceNoticeHours().getValue()));
                                        }
                                    }
                                }
                                return null;
                            }
                        });
                    }

                    if (!policy.allow24HourSchedule().getValue(false) && !policy.schedulingWindow().isEmpty()) {
                        get(proto().scheduledTime().timeFrom()).setTooltip(policy.schedulingWindow().getStringView());
                        // add "From within window" validator
                        get(proto().scheduledTime().timeFrom()).addComponentValidator(new AbstractComponentValidator<Time>() {

                            @Override
                            public AbstractValidationError isValid() {
                                MaintenanceRequestWorkOrderDTO wo = (MaintenanceRequestWorkOrderDTO) getCComponent().getParent().getValue();
                                // if not an emergency - enforce policy restrictions
                                if (!wo.isEmergencyWork().getValue(false)) {
                                    Time from = getCComponent().getValue();
                                    if (from != null && (from.before(policy.schedulingWindow().timeFrom().getValue()) || //
                                            from.after(policy.schedulingWindow().timeTo().getValue()))) {
                                        return new BasicValidationError(getCComponent(), i18n.tr("Time outside of allowed window {0}", policy
                                                .schedulingWindow().getStringView()));
                                    }
                                }
                                return null;
                            }
                        });
                        // add "To after From" validator
                        get(proto().scheduledTime().timeTo()).addComponentValidator(new AbstractComponentValidator<Time>() {

                            @Override
                            public AbstractValidationError isValid() {
                                MaintenanceRequestWorkOrderDTO wo = (MaintenanceRequestWorkOrderDTO) getCComponent().getParent().getValue();
                                Time to = getCComponent().getValue();
                                Time from = get(proto().scheduledTime().timeFrom()).getValue();
                                if (to != null && from != null && to.before(from)) {
                                    return new BasicValidationError(getCComponent(), i18n.tr("Time should be after 'From' time"));
                                }
                                return null;
                            }
                        });
                        if (!policy.maxAllowedWindowHours().isNull()) {
                            // - add "To within window" validator
                            get(proto().scheduledTime().timeTo()).setTooltip(i18n.tr("Limit window to {0} hours", policy.maxAllowedWindowHours().getValue()));
                            get(proto().scheduledTime().timeTo()).addComponentValidator(new AbstractComponentValidator<Time>() {

                                @Override
                                public AbstractValidationError isValid() {
                                    MaintenanceRequestWorkOrderDTO wo = (MaintenanceRequestWorkOrderDTO) getCComponent().getParent().getValue();
                                    // if not an emergency - enforce policy restrictions
                                    if (!wo.isEmergencyWork().getValue(false)) {
                                        Time to = getCComponent().getValue();
                                        Time from = get(proto().scheduledTime().timeFrom()).getValue();
                                        int deltaMills = policy.maxAllowedWindowHours().getValue() * 3600 * 1000;
                                        if (to != null && from != null && to.getTime() > from.getTime() + deltaMills) {
                                            return new BasicValidationError(getCComponent(), i18n.tr("Time window should not exceed {0} hours", policy
                                                    .maxAllowedWindowHours().getValue()));
                                        }
                                    }
                                    return null;
                                }
                            });
                        }
                    }

                    return main;
                }
            };

            content.init();
            content.populate(EntityFactory.create(MaintenanceRequestWorkOrderDTO.class));
            return content.asWidget();
        }

        public boolean validate() {
            content.setVisitedRecursive();
            if (content.isValid()) {
                return true;
            } else {
                MessageDialog.error(i18n.tr("Error"), content.getValidationResults().getValidationMessage(true));
                return false;
            }
        }

        public MaintenanceRequestWorkOrderDTO getValue() {
            return content.getValue();
        }
    }

    static abstract class ResolutionBox extends OkCancelDialog {

        private CForm<MaintenanceRequestDTO> content;

        public ResolutionBox(MaintenanceRequestDTO mr) {
            super(i18n.tr("Resolve"));
            setBody(createBody(mr));
        }

        protected Widget createBody(final MaintenanceRequestDTO mr) {
            getOkButton().setEnabled(true);

            content = new CForm<MaintenanceRequestDTO>(MaintenanceRequestDTO.class) {
                @Override
                protected IsWidget createContent() {
                    FormPanel main = new FormPanel(this);

                    main.append(Location.Dual, inject(proto().resolvedDate())).decorate().componentWidth(100);
                    main.append(Location.Dual, inject(proto().resolution())).decorate().componentWidth(200);

                    CComponent<?, LogicalDate, ?, ?> datePicker = get(proto().resolvedDate());
                    datePicker.setMandatory(true);
                    datePicker.addComponentValidator(new AbstractComponentValidator<LogicalDate>() {
                        @Override
                        public BasicValidationError isValid() {
                            return (getCComponent().getValue().before(new LogicalDate(mr.submitted().getValue())) ? new BasicValidationError(getCComponent(),
                                    i18n.tr("Request cannot be Resolved before it was Submitted")) : null);
                        }
                    });

                    return main;
                }
            };

            content.init();
            // default date is today
            if (mr != null && mr.resolvedDate().isNull()) {
                mr.resolvedDate().setValue(new LogicalDate());
            }
            content.populate(mr);
            return content.asWidget();
        }

        public boolean validate() {
            content.setVisitedRecursive();
            if (content.isValid()) {
                return true;
            } else {
                MessageDialog.error(i18n.tr("Error"), content.getValidationResults().getValidationMessage(true));
                return false;
            }
        }

        public MaintenanceRequestDTO getValue() {
            return content.getValue();
        }
    }

    static abstract class RateBox extends OkCancelDialog {

        private CForm<SurveyResponse> content;

        public RateBox(SurveyResponse currentRate) {
            super(i18n.tr("Rate"));
            setBody(createBody(currentRate));
        }

        protected Widget createBody(SurveyResponse currentRate) {
            getOkButton().setEnabled(true);

            content = new CForm<SurveyResponse>(SurveyResponse.class) {
                @Override
                protected IsWidget createContent() {
                    FormPanel main = new FormPanel(this);

                    main.append(Location.Dual, inject(proto().rating())).decorate().componentWidth(30);
                    main.append(Location.Dual, inject(proto().description())).decorate().componentWidth(200);

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

            content.init();
            content.populate(currentRate);
            return content.asWidget();
        }

        public SurveyResponse getValue() {
            return content.getValue();
        }
    }
}
