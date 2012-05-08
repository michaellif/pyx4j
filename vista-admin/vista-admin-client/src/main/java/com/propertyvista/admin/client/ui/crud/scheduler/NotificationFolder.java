/*
 * MCO 2
 * Copyright (C) 2008-2011 Amdocs Canada.
 *
 * Created on Nov 10, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.scheduler;

import com.pyx4j.entity.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.admin.domain.scheduler.Notification;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;

public class NotificationFolder extends VistaBoxFolder<Notification> {

    private static final I18n i18n = I18n.get(NotificationFolder.class);

    public NotificationFolder(boolean modifyable) {
        super(Notification.class, modifyable);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof Notification) {
            return new NotificationEditor();
        } else {
            return super.create(member);
        }
    }

    @Override
    public IFolderItemDecorator<Notification> createItemDecorator() {
        BoxFolderItemDecorator<Notification> decor = new BoxFolderItemDecorator<Notification>(VistaImages.INSTANCE);
        decor.setExpended(false);
        return decor;

    }

}