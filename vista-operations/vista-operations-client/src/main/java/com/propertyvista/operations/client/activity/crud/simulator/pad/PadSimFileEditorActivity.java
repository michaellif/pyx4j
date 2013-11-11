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
package com.propertyvista.operations.client.activity.crud.simulator.pad;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.AbstractEditorActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.simulator.pad.file.PadSimFileEditorView;
import com.propertyvista.operations.domain.payment.pad.simulator.PadSimFile;
import com.propertyvista.operations.rpc.services.simulator.PadSimFileCrudService;

public class PadSimFileEditorActivity extends AbstractEditorActivity<PadSimFile> {

    @SuppressWarnings("unchecked")
    public PadSimFileEditorActivity(CrudAppPlace place) {
        super(place, OperationsSite.getViewFactory().getView(PadSimFileEditorView.class), (AbstractCrudService<PadSimFile>) GWT
                .create(PadSimFileCrudService.class), PadSimFile.class);
    }
}
