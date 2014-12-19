/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 */
package com.propertyvista.operations.client.ui.crud.scheduler.trigger;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.scheduler.PmcProcessOptions;
import com.propertyvista.operations.domain.scheduler.TriggerPmcSelectionType;
import com.propertyvista.operations.rpc.dto.TriggerDTO;

public class TriggerForm extends OperationsEntityForm<TriggerDTO> {

    private static final I18n i18n = I18n.get(TriggerForm.class);

    public TriggerForm(IPrimeFormView<TriggerDTO, ?> view) {
        super(TriggerDTO.class, view);

        Tab tab = addTab(createDetailsTab(), i18n.tr("Details"));
        selectTab(tab);

        tab = addTab(isEditable() ? new HTML() : ((TriggerViewerView) getParentView()).getRunListerView().asWidget(), i18n.tr("Runs"));
        setTabEnabled(tab, !isEditable());

    }

    private FormPanel createDetailsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().name()).decorate();
        formPanel.append(Location.Left, proto().triggerType()).decorate();
        formPanel.append(Location.Right, proto().populationType()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().options(), new CLabel<>()).decorate();

        formPanel.append(Location.Left, proto().threads()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().timeout()).decorate();

        formPanel.append(Location.Dual, proto().population(), new PopulationFolder(this));

        formPanel.h2(i18n.tr("Schedules"));
        formPanel.append(Location.Left, proto().scheduleSuspended()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().nextScheduledFireTime()).decorate().componentWidth(120);
        formPanel.append(Location.Dual, proto().schedules(), new TriggerScheduleFolder(isEditable()));

        formPanel.append(Location.Left, proto().sleepRetry()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().nextSleepRetryFireTime()).decorate().componentWidth(120);

        formPanel.h2(i18n.tr("Notifications"));
        formPanel.append(Location.Dual, proto().notifications(), new NotificationFolder(isEditable()));

        // tweaks:
        get(proto().populationType()).addValueChangeHandler(new ValueChangeHandler<TriggerPmcSelectionType>() {
            @Override
            public void onValueChange(ValueChangeEvent<TriggerPmcSelectionType> event) {
                get(proto().population()).setVisible(event.getValue() != TriggerPmcSelectionType.allPmc);
                switch (event.getValue()) {
                case allPmc:
                    break;
                case manual:
                    break;
                }
            }
        });

        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().population()).setVisible(
                !getValue().populationType().isNull() && getValue().populationType().getValue() != TriggerPmcSelectionType.allPmc
                        && getValue().populationType().getValue() != TriggerPmcSelectionType.none);

        get(proto().triggerType()).setEditable(getValue().triggerType().isNull());

        get(proto().populationType()).setEditable(
                getValue().triggerType().isNull() || !getValue().triggerType().getValue().hasOption(PmcProcessOptions.GlobalOnly));
    }
}