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

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.operations.domain.scheduler.TriggerNotification;

public class NotificationFolder extends VistaBoxFolder<TriggerNotification> {

    private static final I18n i18n = I18n.get(NotificationFolder.class);

    public NotificationFolder(boolean modifyable) {
        super(TriggerNotification.class, modifyable);
    }

    @Override
    protected CForm<TriggerNotification> createItemForm(IObject<?> member) {
        return new NotificationEditor();
    }

    public class NotificationEditor extends CForm<TriggerNotification> {

        public NotificationEditor() {
            super(TriggerNotification.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Left, proto().event()).decorate().componentWidth(120);
            formPanel.append(Location.Right, proto().user()).decorate();

            return formPanel;
        }
    }

    @Override
    public IFolderItemDecorator<TriggerNotification> createItemDecorator() {
        BoxFolderItemDecorator<TriggerNotification> decor = new BoxFolderItemDecorator<TriggerNotification>(VistaImages.INSTANCE);
        decor.setExpended(false);
        return decor;

    }

}