/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 3, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.client.visor.notes;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.crud.IView;

public class NotesAndAttachmentsVisorControllerImpl implements INotesAndAttachmentsVisorController {

    public NotesAndAttachmentsVisorControllerImpl(Class<? extends IEntity> entityClass, Key entityId) {

    }

    @Override
    public void show(IView viewImpl) {
        viewImpl.showVisor(new NotesAndAttachmentsVisorView(this), "Notes & Attachments");
    }
}
