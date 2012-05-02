/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.welcomewizard.moveinschedule;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.MoveInScheduleDTO;

public class MoveInScheduleForm extends CEntityDecoratableEditor<MoveInScheduleDTO> {

    public MoveInScheduleForm() {
        super(MoveInScheduleDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().moveInDay()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().reserveElevator()), 10).build());
        get(proto().reserveElevator()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().elevatorDay()).setVisible(event.getValue());
                get(proto().elevatorSchedule()).setVisible(event.getValue());
            }
        });
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().elevatorDay()), 10).build());
        content.setWidget(++row, 0, inject(proto().elevatorSchedule(), new ScheduleFolder()));

        get(proto().elevatorDay()).setVisible(false);
        get(proto().elevatorSchedule()).setVisible(false);

//        get(proto().elevatorReserveFrom()).setVisible(false);
//        get(proto().elevatorReserveTo()).setVisible(false);

        return content;
    }
}
