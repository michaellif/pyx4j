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

import com.pyx4j.entity.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.admin.domain.scheduler.TriggerNotification;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;

public class NotificationFolder extends VistaBoxFolder<TriggerNotification> {

    private static final I18n i18n = I18n.get(NotificationFolder.class);

    public NotificationFolder(boolean modifyable) {
        super(TriggerNotification.class, modifyable);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof TriggerNotification) {
            return new NotificationEditor();
        } else {
            return super.create(member);
        }
    }

    public class NotificationEditor extends CEntityDecoratableForm<TriggerNotification> {

        public NotificationEditor() {
            super(TriggerNotification.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));
            int row = -1;

            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().event()), 10).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().user()), 20).build());

            return content;
        }
    }

    @Override
    public IFolderItemDecorator<TriggerNotification> createItemDecorator() {
        BoxFolderItemDecorator<TriggerNotification> decor = new BoxFolderItemDecorator<TriggerNotification>(VistaImages.INSTANCE);
        decor.setExpended(false);
        return decor;

    }

}