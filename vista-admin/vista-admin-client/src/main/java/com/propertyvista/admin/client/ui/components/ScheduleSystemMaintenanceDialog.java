/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jul 16, 2009
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.components;

import java.util.Date;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Consts;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.essentials.rpc.admin.SystemMaintenanceState;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CGroupBoxPanel;
import com.pyx4j.forms.client.ui.CIntegerField;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.widgets.client.dialog.CloseOption;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkOption;

import com.propertyvista.admin.rpc.services.VistaAdminService;

public class ScheduleSystemMaintenanceDialog extends VerticalPanel implements CloseOption, OkOption {

    private static I18n i18n = I18nFactory.getI18n(ScheduleSystemMaintenanceDialog.class);

    private final CEntityForm<SystemMaintenanceState> form;

    private final CCheckBox maintenanceScheduled;

    private final CDatePicker dueDate;

    private final CTextField dueTime;

    private final CIntegerField duration;

    private final CIntegerField gracePeriod;

    private final CTextField message;

    private boolean maintenanceinEffect;

    private final Dialog dialog;

    public ScheduleSystemMaintenanceDialog() {
        dialog = new Dialog(i18n.tr("System Maintenance Administration"), this);

        maintenanceScheduled = new CCheckBox("Maintenance Scheduled");
        maintenanceScheduled.setValue(false);
        maintenanceScheduled.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                applyApplicability();
            }
        });

        dueDate = new CDatePicker("Start Date");
        dueDate.setPastDateSelectionAllowed(false);

        dueTime = new CTextField("Start Time");
        dueTime.addRegexValidator("^(20|21|22|23|[01]\\d|\\d)[:]([0-5]\\d)$", "24 hours time format expected");

        gracePeriod = new CIntegerField("Grace Period (min)");

        duration = new CIntegerField("Duration (min)");
        duration.setRange(5, 24 * 60);

        message = new CTextField("Message");

        CComponent<?>[][] components = new CComponent[][] {

        { maintenanceScheduled },

        { dueDate },

        { dueTime },

        { gracePeriod },

        { duration },

        { message },

        };

        form = new CEntityForm<SystemMaintenanceState>(SystemMaintenanceState.class);
        form.setComponents(components);

        CGroupBoxPanel boxPanel = new CGroupBoxPanel("Schedule", false);
        boxPanel.addComponent(form);

        Widget nativePanel = (Widget) boxPanel.initNativeComponent();
        nativePanel.setWidth("450px");
        this.addWidget(nativePanel);

        RPCManager.executeService(ConfigServices.GetSystemMaintenanceSchedule.class, new VoidDO(), new AsyncCallback<SystemMaintenanceDO>() {

            @Override
            public void onFailure(Throwable t) {
                MessageDialog.error("Can't load system state", t.getMessage(), t);
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onSuccess(SystemMaintenanceDO result) {
                Date now = new Date();
                if ((result == null) || (result.getStartTime() == null)) {
                    maintenanceScheduled.populate(false);
                    dueDate.populate(now);
                    dueTime.populate("23:00");
                    duration.populate(60);
                    gracePeriod.populate(5);
                } else {

                    if (result.getStartTime().after(now)) {
                        dueDate.populate(result.getStartTime());
                    } else {
                        dueDate.populate(now);
                    }
                    dueTime.populate(CommonsStringUtils.d00(result.getStartTime().getHours()) + ":"
                            + CommonsStringUtils.d00(result.getStartTime().getMinutes()));
                    message.populate(result.getMessage());
                    duration.populate(result.getDuration());
                    gracePeriod.populate((int) (result.getGracePeriod() / Consts.MIN2MSEC));

                    maintenanceScheduled.populate(result.getStartTime().after(now));

                    maintenanceinEffect = result.isInEffect();
                }
                applyApplicability();
            }
        });
    }

    private void applyApplicability() {
        boolean enabled = maintenanceScheduled.getValue();
        if (enabled) {
            okButton.setText("Schedule maintenance");
        } else if (maintenanceinEffect) {
            okButton.setText("Stop maintenance");
        } else {
            okButton.setText("Cancel Schedule");
        }
        dueDate.setEnabled(enabled);
        dueTime.setEnabled(enabled);
        gracePeriod.setEnabled(enabled);
        duration.setEnabled(enabled);
        message.setEnabled(enabled);
    }

    @Override
    protected boolean hideOnOptionButtonClick() {
        return false;
    }

    @Override
    public boolean onClickClose() {
        hide();
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClickOk() {
        if (!form.isValid()) {
            new Dialog("Schedule validation", form.getValidationResults().getMessagesText(), Dialog.ERROR).show();
            return;
        }
        final SystemMaintenanceDO request = new SystemMaintenanceDO();
        if (maintenanceScheduled.getValue()) {
            request.setDuration(duration.getValue());
            request.setGracePeriod(gracePeriod.getValue() * Consts.MIN2MSEC);
            request.setMessage(message.getValue());
            if (request.getMessage() == null) {
                request.setMessage("");
            }

            String t = dueTime.getValue();
            int s = t.indexOf(':');
            int hours = Integer.valueOf(t.substring(0, s));
            int minutes = Integer.valueOf(t.substring(s + 1));
            Date startTime = new Date(dueDate.getValue().getTime());
            startTime.setHours(hours);
            startTime.setMinutes(minutes);
            if (startTime.before(new Date())) {
                MessageDialog.error("Schedule validation", "Should schedule for future");
                return;
            }

            request.setStartTime(startTime);
        }

        RPCManager.executeService(VistaAdminService.ScheduleSystemMaintenance.class, request, new AsyncCallback<VoidDO>() {

            @Override
            public void onFailure(Throwable t) {
                MessageDialog.error("Can't change system state", t.getMessage(), t);
            }

            @Override
            public void onSuccess(VoidDO result) {
                if (request.getStartTime() != null) {
                    Message.info("Maintenance scheduled for " + request.getStartTime());
                } else {
                    Message.info("Maintenance schedule cancelled");
                }
                hide();

            }
        });
    }

}