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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
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

    private final NotesAndAttachmentsCrudService service;

    private final NotesAndAttachmentsVisorView view;

    private final Class<? extends IEntity> parentClass;

    private final Key parentKey;

    public NotesAndAttachmentsVisorController(Class<? extends IEntity> parentClass, Key parentKey) {
        this.parentClass = parentClass;
        this.parentKey = parentKey;

        service = GWT.<NotesAndAttachmentsCrudService> create(NotesAndAttachmentsCrudService.class);
        view = new NotesAndAttachmentsVisorView(this);
    }

    @Override
    public void show(final IView parentView) {
        view.populate(new Command() {
            @Override
            public void execute() {
                IsWidget visor = getView();
                parentView.showVisor(visor, "Notes & Attachments");
            }
        });
    }

    /*
     * the methods below have been mainly copied from com.pyx4j.site.client.activity.crud.EditorActivityBase
     */
    public void populate(DefaultAsyncCallback<EntitySearchResult<NotesAndAttachments>> callback) {
        EntityListCriteria<NotesAndAttachments> crit = new EntityListCriteria<NotesAndAttachments>(NotesAndAttachments.class);
        crit.add(PropertyCriterion.eq(crit.proto().parent(), EntityFactory.createIdentityStub(parentClass, parentKey)));
        service.list(callback, crit);
    }

    @Override
    public IsWidget getView() {
        return view;
    }

    public void save(NotesAndAttachments item, DefaultAsyncCallback<Key> callback) {
        if (item.parent().isNull()) {
            IEntity parent = EntityFactory.create(parentClass);
            parent.setPrimaryKey(parentKey);
            item.parent().set(parent);
        }
        item.parent().setPrimaryKey(parentKey);
        if (item.getPrimaryKey() == null) {
            service.create(callback, item);
        } else {
            service.save(callback, item);
        }
    }

    public void remove(NotesAndAttachments item, DefaultAsyncCallback<Boolean> callback) {
        if (item.isNull() || item.getPrimaryKey() == null) {
            callback.onSuccess(true);
        } else {
            service.delete(callback, item.getPrimaryKey());
        }
    }
}
