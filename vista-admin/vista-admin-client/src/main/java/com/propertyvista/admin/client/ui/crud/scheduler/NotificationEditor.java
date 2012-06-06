/*
 * MCO 2
 * Copyright (C) 2008-2011 Amdocs Canada.
 *
 * Created on Nov 10, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.scheduler;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.domain.scheduler.TriggerNotification;

public class NotificationEditor extends AdminEntityForm<TriggerNotification> {

    public NotificationEditor() {
        super(TriggerNotification.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();
        int row = -1;

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().event()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().user()), 20).build());

        return main;
    }
}