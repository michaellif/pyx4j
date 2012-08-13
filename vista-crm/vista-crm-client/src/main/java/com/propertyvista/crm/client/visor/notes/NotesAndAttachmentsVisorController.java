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
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
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
        view.populate();
        IsWidget visor = getView();
        parentView.showVisor(visor, visor.asWidget().getTitle());
    }

    public Key getParentId() {
        return parentId;
    }

    public Class<? extends IEntity> getParentClass() {
        return parentClass;
    }

    /*
     * the methods below have been mainly copied from com.pyx4j.site.client.activity.crud.EditorActivityBase
     */
    public void populate(DefaultAsyncCallback<EntitySearchResult<NotesAndAttachments>> callback) {
        EntityListCriteria<NotesAndAttachments> crit = new EntityListCriteria<NotesAndAttachments>(NotesAndAttachments.class);
        IEntity parent = EntityFactory.create(getParentClass());
        parent.setPrimaryKey(getParentId());
        crit.add(PropertyCriterion.eq(crit.proto().parent(), parent));

        getService().list(callback, crit);
    }

    @Override
    public IsWidget getView() {
        return view;
    }

    @SuppressWarnings("unchecked")
    public AbstractCrudService<NotesAndAttachments> getService() {
        return (AbstractCrudService<NotesAndAttachments>) GWT.create(NotesAndAttachmentsCrudService.class);
    }

    public void save(NotesAndAttachments item, DefaultAsyncCallback<Key> callback) {
        if (item.parent().isNull()) {
            IEntity parent = EntityFactory.create(parentClass);
            parent.setPrimaryKey(parentId);
            item.parent().set(parent);
        }
        item.parent().setPrimaryKey(parentId);
        if (item.getPrimaryKey() == null) {
            getService().create(callback, item);
        } else {
            getService().save(callback, item);
        }
    }
}
