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
package com.propertyvista.crm.client.visor;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

public class NotesAndAttachmentsVisorControllerImpl implements INotesAndAttachmentsVisorController {

    public NotesAndAttachmentsVisorControllerImpl(Class<? extends IEntity> entityClass, Key entityId) {

    }

    @Override
    public void populate(DefaultAsyncCallback asyncCallback) {
        asyncCallback.onSuccess(null);
    }

    @Override
    public IsWidget createView() {
        // TODO Auto-generated method stub
        return new Label("Notes And Attachments Visor");
    }

}
