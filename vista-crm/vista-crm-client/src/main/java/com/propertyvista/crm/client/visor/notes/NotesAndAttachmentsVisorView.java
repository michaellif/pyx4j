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

import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.crm.client.ui.notesandattachments.NotesAndAttachmentsForm;
import com.propertyvista.domain.note.NotesAndAttachments;
import com.propertyvista.domain.note.NotesAndAttachmentsDTO;

public class NotesAndAttachmentsVisorView extends SimplePanel {

    public NotesAndAttachmentsVisorView(NotesAndAttachmentsVisorControllerImpl controller) {
        super();
        final NotesAndAttachmentsForm form = new NotesAndAttachmentsForm();
        form.initContent();
        setWidget(form.asWidget());
        controller.populate(new EntityListCriteria<NotesAndAttachments>(NotesAndAttachments.class),
                new DefaultAsyncCallback<EntitySearchResult<NotesAndAttachments>>() {

                    @Override
                    public void onSuccess(EntitySearchResult<NotesAndAttachments> result) {
                        NotesAndAttachmentsDTO dto = EntityFactory.create(NotesAndAttachmentsDTO.class);
                        for (NotesAndAttachments na : result.getData()) {
                            dto.notes().add(na);
                        }
                        form.populate(dto);
                    }
                });
    }
}
