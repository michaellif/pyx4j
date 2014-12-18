/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-03
 * @author ArtyomB
 */
package com.propertyvista.operations.client.activity.crud.systemdefaults;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeEditorActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.systemdefaults.VistaSystemDefaultsEditorView;
import com.propertyvista.operations.rpc.dto.VistaSystemDefaultsDTO;
import com.propertyvista.operations.rpc.services.Vista2PmcService;

public class VistaSystemDefaultsEditorActivity extends AbstractPrimeEditorActivity<VistaSystemDefaultsDTO> implements VistaSystemDefaultsEditorView.Presenter {

    public VistaSystemDefaultsEditorActivity(CrudAppPlace place) {
        super(VistaSystemDefaultsDTO.class, place, OperationsSite.getViewFactory().getView(VistaSystemDefaultsEditorView.class), GWT
                        .<AbstractCrudService<VistaSystemDefaultsDTO>> create(Vista2PmcService.class));
    }

}
