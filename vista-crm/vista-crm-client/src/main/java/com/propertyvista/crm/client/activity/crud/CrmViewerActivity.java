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

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.AbstractViewerActivity;
import com.pyx4j.site.client.ui.prime.form.IViewer;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.event.CrudNavigateEvent;
import com.propertyvista.crm.client.visor.notes.NotesAndAttachmentsVisorController;
import com.propertyvista.shared.NotesParentId;

public class CrmViewerActivity<E extends IEntity> extends AbstractViewerActivity<E> {

    private final CrudAppPlace place;

    private Class<? extends IEntity> entityClass;

    private NotesAndAttachmentsVisorController notesAndAttachmentsController;

    public CrmViewerActivity(CrudAppPlace place, IViewer<E> view, AbstractCrudService<E> service) {
        super(place, view, service);

        assert (place instanceof CrudAppPlace);
        this.place = place;
    }

    @Override
    protected void onPopulateSuccess(E result) {
        super.onPopulateSuccess(result);

        entityClass = result.getEntityMeta().getBOClass();

        AppSite.getEventBus().fireEvent(new CrudNavigateEvent(place, result));
    }

    public NotesAndAttachmentsVisorController getNotesAndAttachmentsController() {
        if (notesAndAttachmentsController == null) {
            notesAndAttachmentsController = new NotesAndAttachmentsVisorController(getView(), createNotesParentId());
        }
        return notesAndAttachmentsController;
    }

    protected NotesParentId createNotesParentId() {
        return new NotesParentId(entityClass, getEntityId());
    }

    // TODO : this algorithm should be revised 
    //TODO use EntityMeta or ClassName.getClassName(klass)
    public final String getEntitySimpleClassName(Class<? extends IEntity> entityClass) {
        return GWTJava5Helper.getSimpleName(entityClass);
    }
}
