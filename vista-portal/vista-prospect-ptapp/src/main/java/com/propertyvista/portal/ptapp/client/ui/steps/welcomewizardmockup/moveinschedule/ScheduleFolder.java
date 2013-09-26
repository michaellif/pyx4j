/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 2, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup.moveinschedule;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.dom.client.Style;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.moveinwizardmockup.TimeSegmentDTO;

@Deprecated
public class ScheduleFolder extends VistaTableFolder<TimeSegmentDTO> {

    private static enum StyleName implements IStyleName {

        ScheduleFolderBusyTime, ScheduleFolderFreeTime, ScheduleFolderBookedTime
    }

    private static final List<EntityFolderColumnDescriptor> COLUMNS;

    static {
        TimeSegmentDTO proto = EntityFactory.getEntityPrototype(TimeSegmentDTO.class);

        COLUMNS = Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto.start(), "5em"),
                new EntityFolderColumnDescriptor(proto.end(), "5em")                                
        );//@formatter:on
    }

    public ScheduleFolder() {
        super(TimeSegmentDTO.class);
        setAddable(false);
        setEditable(false);
        setViewable(true);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return COLUMNS;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof TimeSegmentDTO) {
            return new TimeSegmentEditor();
        } else {
            return super.create(member);
        }
    }

    public class TimeSegmentEditor extends CEntityFolderRowEditor<TimeSegmentDTO> {

        public TimeSegmentEditor() {
            super(TimeSegmentDTO.class, COLUMNS);
        }

        @Override
        protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
            return super.createCell(column);
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            if (false) {
                // TODO add special theme
                if (!getValue().status().isNull()) {
                    String style = "";
                    switch (getValue().status().getValue()) {
                    case free:
                        style = StyleName.ScheduleFolderFreeTime.name();
                        break;
                    case busy:
                        style = StyleName.ScheduleFolderBusyTime.name();
                        break;
                    case booked:
                        style = StyleName.ScheduleFolderBookedTime.name();
                        break;
                    }
                    asWidget().setStyleName(style);
                } else {
                    asWidget().setVisible(false);
                }
            } else {
                if (!getValue().status().isNull()) {
                    Style style = asWidget().getElement().getStyle();
                    switch (getValue().status().getValue()) {
                    case free:
                        style.setBackgroundColor("#AAFFAA");
                        break;
                    case busy:
                        style.setBackgroundColor("#FFAAAA");
                        break;
                    case booked:
                        style.setBackgroundColor("#FFAAAA");
                        break;
                    }
                } else {
                    asWidget().setVisible(false);
                }

            }
        }
    }
}
