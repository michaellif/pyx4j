/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 14, 2012
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeViewerActivity;
import com.pyx4j.site.client.backoffice.ui.prime.form.IViewerView;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.event.CrudNavigateEvent;
import com.propertyvista.crm.client.visor.notes.NotesAndAttachmentsVisorController;
import com.propertyvista.domain.note.HasNotesAndAttachments;

public class CrmViewerActivity<E extends IEntity> extends AbstractPrimeViewerActivity<E> {

    private final CrudAppPlace place;

    private NotesAndAttachmentsVisorController notesAndAttachmentsController;

    public CrmViewerActivity(Class<E> entityClass, CrudAppPlace place, IViewerView<E> view, AbstractCrudService<E> service) {
        super(entityClass, place, view, service);
        assert (place instanceof CrudAppPlace);
        this.place = place;
    }

    @Override
    protected void onPopulateSuccess(E result) {
        super.onPopulateSuccess(result);
        AppSite.getEventBus().fireEvent(new CrudNavigateEvent(place, result));
    }

    public NotesAndAttachmentsVisorController getNotesAndAttachmentsController() {
        if (notesAndAttachmentsController == null) {
            HasNotesAndAttachments parentId = createNotesParentId();
            if (parentId != null) {
                notesAndAttachmentsController = new NotesAndAttachmentsVisorController(getView(), parentId);
            }
            assert notesAndAttachmentsController != null : SimpleMessageFormat.format("Inapplicable for the entity of {0}", entityClass.getName());
        }
        return notesAndAttachmentsController;
    }

    protected HasNotesAndAttachments createNotesParentId() {
        IEntity reflectionCapableInstance = EntityFactory.createIdentityStub(getValue().getEntityMeta().getBOClass(), getEntityId());
        if (reflectionCapableInstance.isInstanceOf(HasNotesAndAttachments.class)) {
            return (HasNotesAndAttachments) reflectionCapableInstance;
        } else {
            return null;
        }
    }
}
