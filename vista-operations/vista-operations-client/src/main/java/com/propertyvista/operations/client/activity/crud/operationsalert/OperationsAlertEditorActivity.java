/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-06-23
 * @author VladL
 */
package com.propertyvista.operations.client.activity.crud.operationsalert;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeEditorActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.operationsalert.OperationsAlertEditorView;
import com.propertyvista.operations.rpc.dto.OperationsAlertDTO;
import com.propertyvista.operations.rpc.services.OperationsAlertCrudService;

public class OperationsAlertEditorActivity extends AbstractPrimeEditorActivity<OperationsAlertDTO> {

    public OperationsAlertEditorActivity(CrudAppPlace place) {
        super(OperationsAlertDTO.class, place, OperationsSite.getViewFactory().getView(OperationsAlertEditorView.class), GWT
                        .<OperationsAlertCrudService> create(OperationsAlertCrudService.class));
    }
}
