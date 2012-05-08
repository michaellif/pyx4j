/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 7, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.scheduler.trigger;

import com.pyx4j.entity.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.admin.domain.scheduler.TriggerSchedule;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;

public class TriggerScheduleFolder extends VistaBoxFolder<TriggerSchedule> {

    private static final I18n i18n = I18n.get(TriggerScheduleFolder.class);

    public TriggerScheduleFolder(boolean modifyable) {
        super(TriggerSchedule.class, modifyable);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof TriggerSchedule) {
            return new TriggerScheduleForm();
        }
        return super.create(member);
    }

    @Override
    public IFolderItemDecorator<TriggerSchedule> createItemDecorator() {
        BoxFolderItemDecorator<TriggerSchedule> decor = (BoxFolderItemDecorator<TriggerSchedule>) super.createItemDecorator();
        decor.setExpended(false);
        return decor;
    }

}