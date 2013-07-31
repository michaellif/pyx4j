/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.scheduler.trigger;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.operations.domain.scheduler.TriggerNotification;

public class NotificationFolder extends VistaBoxFolder<TriggerNotification> {

    private static final I18n i18n = I18n.get(NotificationFolder.class);

    public NotificationFolder(boolean modifyable) {
        super(TriggerNotification.class, modifyable);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
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
            TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));
            int row = -1;

            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().event()), 10).build());
            content.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().user())).build());

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