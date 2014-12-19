/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-31
 * @author vlads
 */
package com.propertyvista.operations.client.ui.crud.scheduler.trigger;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.crm.rpc.dto.ScheduleDataDTO;

// Internals:
public abstract class RunForDateDialog extends OkCancelDialog {

    private CForm<ScheduleDataDTO> content;

    public RunForDateDialog() {
        super(TriggerViewerViewImpl.i18n.tr("Run for Date"));
        setBody(createBody());
    }

    protected Widget createBody() {
        getOkButton().setEnabled(true);

        content = new CForm<ScheduleDataDTO>(ScheduleDataDTO.class) {
            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().date()).decorate().componentWidth(120).labelWidth(84);
//                    main.setWidget(2, 0, inject(proto().time(), new DecoratorBuilder(10).labelWidth(7).build());

                return formPanel;
            }

            @Override
            public void addValidations() {
                super.addValidations();

            }
        };

        content.init();
        content.populate(EntityFactory.create(ScheduleDataDTO.class));
        return content.asWidget();
    }

    public ScheduleDataDTO getValue() {
        return content.getValue();
    }
}