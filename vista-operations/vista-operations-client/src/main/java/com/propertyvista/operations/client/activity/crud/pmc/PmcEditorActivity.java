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
package com.propertyvista.operations.client.activity.crud.pmc;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.client.activity.AbstractEditorActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.pmc.PmcEditorView;
import com.propertyvista.operations.rpc.dto.PmcDTO;
import com.propertyvista.operations.rpc.services.PmcCrudService;

public class PmcEditorActivity extends AbstractEditorActivity<PmcDTO> {

    public PmcEditorActivity(CrudAppPlace place) {
        super(place, OperationsSite.getViewFactory().instantiate(PmcEditorView.class), GWT.<PmcCrudService> create(PmcCrudService.class), PmcDTO.class);
    }
}
