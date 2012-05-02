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
package com.propertyvista.portal.server.ptapp.services.steps.welcomewizard;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.MoveInScheduleDTO;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.TimeSegmentDTO;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.TimeSegmentDTO.Status;
import com.propertyvista.portal.rpc.ptapp.services.steps.welcomewizard.MoveInScheduleService;

public class MoveInScheduleServiceImpl implements MoveInScheduleService {

    private static final Long HALF_HOUR = 1000L * 60L * 30L;

    @Override
    public void retrieve(AsyncCallback<MoveInScheduleDTO> callback, Key tenantId) {
        MoveInScheduleDTO moveInSchedule = EntityFactory.create(MoveInScheduleDTO.class);

        moveInSchedule.moveInDay().setValue(WelcomeWizardDemoData.leaseStart());
        moveInSchedule.elevatorDay().setValue(WelcomeWizardDemoData.leaseStart());
        moveInSchedule.elevatorSchedule().addAll(createRandomElevatorSchedule(moveInSchedule.elevatorDay().getValue()));
        callback.onSuccess(moveInSchedule);
    }

    @Override
    public void save(AsyncCallback<MoveInScheduleDTO> callback, MoveInScheduleDTO editableEntity) {
        callback.onSuccess(editableEntity);
    }

    private static List<? extends TimeSegmentDTO> createRandomElevatorSchedule(LogicalDate day) {
        List<TimeSegmentDTO> randomSchedule = new ArrayList<TimeSegmentDTO>(48);

        Time startTime = new Time(day.getTime());
        for (int i = 0; i < 48; ++i) {
            Status randomStatus = Status.busy;
            if (i < 12) {
                randomStatus = Status.busy;
            } else if (i < 48 - 12) {
                randomStatus = WelcomeWizardDemoData.rnd().nextBoolean() ? Status.busy : Status.free;
            } else {
                randomStatus = Status.busy;
            }
            TimeSegmentDTO segment = createHalfHourTimeSegment(startTime, randomStatus);
            randomSchedule.add(segment);
            startTime = segment.end().getValue();
        }
        return randomSchedule;
    }

    private static TimeSegmentDTO createHalfHourTimeSegment(Time start, TimeSegmentDTO.Status status) {
        TimeSegmentDTO segment = EntityFactory.create(TimeSegmentDTO.class);

        segment.start().setValue(start);
        segment.end().setValue(new Time(start.getTime() + HALF_HOUR));
        segment.status().setValue(status);
        return segment;
    }

}
