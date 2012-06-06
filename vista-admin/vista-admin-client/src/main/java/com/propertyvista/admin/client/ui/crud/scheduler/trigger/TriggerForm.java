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
package com.propertyvista.admin.client.ui.crud.scheduler.trigger;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.admin.client.themes.AdminTheme;
import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.client.ui.crud.scheduler.NotificationFolder;
import com.propertyvista.admin.domain.scheduler.Trigger;
import com.propertyvista.admin.domain.scheduler.TriggerPmcSelectionType;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;

public class TriggerForm extends AdminEntityForm<Trigger> {

    private static final I18n i18n = I18n.get(TriggerForm.class);

    protected final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(AdminTheme.defaultTabHeight, Unit.EM);

    public TriggerForm() {
        this(false);
    }

    public TriggerForm(boolean viewMode) {
        super(Trigger.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        tabPanel.add(createDetailsTab(), i18n.tr("Details"));

        tabPanel.add(isEditable() ? new HTML() : ((TriggerViewerView) getParentView()).getRunListerView().asWidget(), i18n.tr("Runs"));
        tabPanel.setLastTabDisabled(isEditable());

        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    private Widget createDetailsTab() {
        FormFlexPanel main = new FormFlexPanel();

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

        return new ScrollPanel(main);
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        get(proto().population()).setVisible(!getValue().populationType().isNull() && getValue().populationType().getValue() != TriggerPmcSelectionType.allPmc);
    }
}