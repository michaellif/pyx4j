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
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.scheduler.trigger;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.IFormView;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.client.ui.crud.scheduler.NotificationFolder;
import com.propertyvista.operations.domain.scheduler.TriggerPmcSelectionType;
import com.propertyvista.operations.rpc.TriggerDTO;

public class TriggerForm extends OperationsEntityForm<TriggerDTO> {

    private static final I18n i18n = I18n.get(TriggerForm.class);

    public TriggerForm(IFormView<TriggerDTO> view) {
        super(TriggerDTO.class, view);

        Tab tab = addTab(createDetailsTab());
        selectTab(tab);

        tab = addTab(isEditable() ? new HTML() : ((TriggerViewerView) getParentView()).getRunListerView().asWidget(), i18n.tr("Runs"));
        setTabEnabled(tab, !isEditable());

    }

    private FormFlexPanel createDetailsTab() {
        FormFlexPanel main = new FormFlexPanel(i18n.tr("Details"));

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().triggerType()), 15).build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().populationType()), 10).build());
        main.setWidget(++row, 0, inject(proto().population(), new PopulationFolder(isEditable())));

        main.setH2(++row, 0, 2, i18n.tr("Schedules"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().nextScheduledFireTime()), 10).build());
        main.setWidget(++row, 0, inject(proto().schedules(), new TriggerScheduleFolder(isEditable())));

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().sleepRetry()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().nextSleepRetryFireTime()), 10).build());

        main.setH2(++row, 0, 2, i18n.tr("Notifications"));
        main.setWidget(++row, 0, inject(proto().notifications(), new NotificationFolder(isEditable())));

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

        return main;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().population()).setVisible(!getValue().populationType().isNull() && getValue().populationType().getValue() != TriggerPmcSelectionType.allPmc);
    }
}