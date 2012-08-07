/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 4, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.notesandattachments;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.client.ui.crud.form.EditorViewImplBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.rpc.services.notes.NotesAndAttachmentsCrudService;
import com.propertyvista.domain.note.NotesAndAttachmentsDTO;

public class NotesAndAttachmentsViewImpl extends EditorViewImplBase<NotesAndAttachmentsDTO> implements NotesAndAttachmentsView {
    public NotesAndAttachmentsViewImpl(Class<? extends CrudAppPlace> placeClass) {
        super();

        CrudAppPlace place = AppSite.getHistoryMapper().createPlace(placeClass);
        AbstractCrudService<NotesAndAttachmentsDTO> service = (AbstractCrudService<NotesAndAttachmentsDTO>) GWT.create(NotesAndAttachmentsCrudService.class);
        EditorActivityBase<NotesAndAttachmentsDTO> activity = new EditorActivityBase<NotesAndAttachmentsDTO>(place, this, service, NotesAndAttachmentsDTO.class);
        setPresenter(activity);

        setForm(new NotesAndAttachmentsForm());
    }
}
