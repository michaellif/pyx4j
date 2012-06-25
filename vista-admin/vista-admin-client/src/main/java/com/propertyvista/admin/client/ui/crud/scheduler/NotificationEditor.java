/*
 * MCO 2
 * Copyright (C) 2008-2011 Amdocs Canada.
 *
 * Created on Nov 10, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.scheduler;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.domain.scheduler.TriggerNotification;

public class NotificationEditor extends AdminEntityForm<TriggerNotification> {

    private static final I18n i18n = I18n.get(NotificationEditor.class);

    public NotificationEditor() {
        super(TriggerNotification.class);
    }

    @Override
    public void createTabs() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().event()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().user()), 20).build());

        selectTab(addTab(content, i18n.tr("General")));
    }
}