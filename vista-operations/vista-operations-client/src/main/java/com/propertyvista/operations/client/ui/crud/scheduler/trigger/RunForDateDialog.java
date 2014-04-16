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
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.scheduler.trigger;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.FormDecoratorBuilder;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.crm.rpc.dto.ScheduleDataDTO;

// Internals:
public abstract class RunForDateDialog extends OkCancelDialog {

    private CEntityForm<ScheduleDataDTO> content;

    public RunForDateDialog() {
        super(TriggerViewerViewImpl.i18n.tr("Run for Date"));
        setBody(createBody());
    }

    protected Widget createBody() {
        getOkButton().setEnabled(true);

        content = new CEntityForm<ScheduleDataDTO>(ScheduleDataDTO.class) {
            @Override
            protected IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                main.setWidget(1, 0, inject(proto().date(), new FormDecoratorBuilder(10).labelWidth(7).build()));
//                    main.setWidget(2, 0, inject(proto().time(), new DecoratorBuilder(10).labelWidth(7).build());

                return main;
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