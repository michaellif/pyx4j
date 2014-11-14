/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.simulator.pad;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.backoffice.activity.prime.AbstractListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.simulator.pad.file.PadSimFileListerView;
import com.propertyvista.operations.domain.eft.caledoneft.simulator.PadSimFile;
import com.propertyvista.operations.rpc.services.simulator.PadSimFileCrudService;

public class PadSimFileListerActivity extends AbstractListerActivity<PadSimFile> implements PadSimFileListerView.Presenter {

    public PadSimFileListerActivity(AppPlace place) {
        super(PadSimFile.class, place, OperationsSite.getViewFactory().getView(PadSimFileListerView.class));
    }

    @Override
    public void loadPadFile() {
        ((PadSimFileCrudService) GWT.<AbstractCrudService<PadSimFile>> create(PadSimFileCrudService.class)).loadPadFile(new DefaultAsyncCallback<PadSimFile>() {
            @Override
            public void onSuccess(PadSimFile result) {
                populate();
            }
        });
    }
}
