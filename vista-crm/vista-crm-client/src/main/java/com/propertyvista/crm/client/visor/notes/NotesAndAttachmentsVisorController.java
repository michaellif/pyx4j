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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.crud.IView;

import com.propertyvista.crm.client.visor.IVisorController;
import com.propertyvista.crm.rpc.services.notes.NotesAndAttachmentsCrudService;
import com.propertyvista.domain.note.NotesAndAttachments;

public class NotesAndAttachmentsVisorController implements IVisorController {

    private final NotesAndAttachmentsVisorView view;

    private final Key parentId;

    private final Class<? extends IEntity> parentClass;

    public NotesAndAttachmentsVisorController(Class<? extends IEntity> parentClass, Key parentId) {
        this.parentClass = parentClass;
        this.parentId = parentId;
        view = new NotesAndAttachmentsVisorView(this);
        view.setTitle("Notes & Attachments");
    }

    @Override
    public void show(IView parentView) {
        IsWidget visor = getView();
        parentView.showVisor(visor, visor.asWidget().getTitle());
    }

    /*
     * the methods below have been mainly copied from com.pyx4j.site.client.activity.crud.EditorActivityBase
     */
    public void populate(EntityListCriteria<NotesAndAttachments> criteria, DefaultAsyncCallback<EntitySearchResult<NotesAndAttachments>> callback) {
        getService().list(callback, criteria);
    }

    protected void createNewEntity(AsyncCallback<NotesAndAttachments> callback) {
        assert (parentId != null) : "Notes owner cannot be null";

        NotesAndAttachments item = EntityFactory.create(NotesAndAttachments.class);
        item.parent().setPrimaryKey(parentId);

        callback.onSuccess(item);

    }

    @Override
    public IsWidget getView() {
        return view;
    }

    @SuppressWarnings("unchecked")
    public AbstractCrudService<NotesAndAttachments> getService() {
        return (AbstractCrudService<NotesAndAttachments>) GWT.create(NotesAndAttachmentsCrudService.class);
    }

    protected NotesAndAttachments getNewItem() {
        assert (parentId != null) : "Notes owner cannot be null";

        NotesAndAttachments item = EntityFactory.create(NotesAndAttachments.class);
        item.parent().setPrimaryKey(parentId);
        return item;
    }

}
