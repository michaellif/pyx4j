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
package com.propertyvista.operations.client.activity.crud.simulatedpad;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.AbstractEditorActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.ui.crud.simulatedpad.PadFileEditorView;
import com.propertyvista.operations.client.viewfactories.crud.AdministrationVeiwFactory;
import com.propertyvista.operations.domain.payment.pad.sim.PadSimFile;
import com.propertyvista.operations.rpc.services.sim.PadSimFileCrudService;

public class PadFileEditorActivity extends AbstractEditorActivity<PadSimFile> {

    @SuppressWarnings("unchecked")
    public PadFileEditorActivity(CrudAppPlace place) {
        super(place, AdministrationVeiwFactory.instance(PadFileEditorView.class), (AbstractCrudService<PadSimFile>) GWT.create(PadSimFileCrudService.class),
                PadSimFile.class);
    }
}
