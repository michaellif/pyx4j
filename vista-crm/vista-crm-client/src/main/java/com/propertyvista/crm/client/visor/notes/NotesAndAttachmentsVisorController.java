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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.IView;

import com.propertyvista.crm.client.visor.IVisorController;
import com.propertyvista.crm.rpc.services.notes.NotesAndAttachmentsCrudService;
import com.propertyvista.domain.note.NotesAndAttachments;
import com.propertyvista.shared.NotesParentId;

public class NotesAndAttachmentsVisorController implements IVisorController {

    private static final I18n i18n = I18n.get(NotesAndAttachmentsVisorController.class);

    private final NotesAndAttachmentsCrudService service;

    private final NotesAndAttachmentsVisorView visor;

    private final NotesParentId notesParentId;

    public NotesAndAttachmentsVisorController(NotesParentId notesParentId) {
        service = GWT.<NotesAndAttachmentsCrudService> create(NotesAndAttachmentsCrudService.class);
        visor = new NotesAndAttachmentsVisorView(this);
        this.notesParentId = notesParentId;
    }

    @Override
    public void show(final IView parentView) {
        visor.populate(new Command() {
            @Override
            public void execute() {
                parentView.showVisor(visor, i18n.tr("Notes & Attachments"));
            }
        });
    }

    public void populate(DefaultAsyncCallback<EntitySearchResult<NotesAndAttachments>> callback) {
        EntityListCriteria<NotesAndAttachments> criteria = new EntityListCriteria<NotesAndAttachments>(NotesAndAttachments.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().ownerClass(), notesParentId.getOwnerClass()));
        criteria.add(PropertyCriterion.eq(criteria.proto().ownerId(), notesParentId.getOwnerId()));
        service.list(callback, criteria);
    }

    public void save(NotesAndAttachments item, DefaultAsyncCallback<Key> callback) {
        item.ownerClass().setValue(notesParentId.getOwnerClass());
        item.ownerId().setValue(notesParentId.getOwnerId());
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
