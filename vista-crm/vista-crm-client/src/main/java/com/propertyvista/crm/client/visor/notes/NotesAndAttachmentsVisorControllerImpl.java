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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.crm.rpc.services.notes.NotesAndAttachmentsCrudService;
import com.propertyvista.domain.note.NotesAndAttachments;

public class NotesAndAttachmentsVisorControllerImpl extends VisorControllerBase<NotesAndAttachments> implements INotesAndAttachmentsVisorController {

    private final NotesAndAttachmentsVisorView view;

    public NotesAndAttachmentsVisorControllerImpl(Class<? extends IEntity> parentClass, Key parentId) {
        super(NotesAndAttachments.class, parentClass, parentId);

        view = new NotesAndAttachmentsVisorView(this);
        view.setTitle("Notes & Attachments");
    }

    @Override
    public IsWidget getView() {
        return view;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractCrudService<NotesAndAttachments> getService() {
        return (AbstractCrudService<NotesAndAttachments>) GWT.create(NotesAndAttachmentsCrudService.class);
    }

    @Override
    protected NotesAndAttachments getNewItem() {
        assert (getParentId() != null) : "Notes owner cannot be null";

        NotesAndAttachments item = EntityFactory.create(NotesAndAttachments.class);
        item.parent().setPrimaryKey(getParentId());
        return item;
    }
}
