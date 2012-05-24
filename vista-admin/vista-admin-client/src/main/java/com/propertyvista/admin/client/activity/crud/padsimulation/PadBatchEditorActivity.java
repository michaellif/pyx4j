/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.admin.client.activity.crud.padsimulation;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.admin.client.ui.crud.padsimulation.batch.PadBatchEditorView;
import com.propertyvista.admin.client.viewfactories.crud.AdministrationVeiwFactory;
import com.propertyvista.admin.domain.payment.pad.sim.PadSimBatch;
import com.propertyvista.admin.rpc.services.sim.PadSimBatchCrudService;

public class PadBatchEditorActivity extends EditorActivityBase<PadSimBatch> {

    @SuppressWarnings("unchecked")
    public PadBatchEditorActivity(CrudAppPlace place) {
        super(place, AdministrationVeiwFactory.instance(PadBatchEditorView.class), (AbstractCrudService<PadSimBatch>) GWT.create(PadSimBatchCrudService.class),
                PadSimBatch.class);
    }
}
