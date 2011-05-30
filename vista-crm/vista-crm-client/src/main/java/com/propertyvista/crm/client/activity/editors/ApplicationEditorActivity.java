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
package com.propertyvista.crm.client.activity.editors;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;

import com.propertyvista.crm.client.ui.editors.ApplicationEditorView;
import com.propertyvista.crm.rpc.services.AbstractCrudService;
import com.propertyvista.crm.rpc.services.ApplicationCrudService;
import com.propertyvista.dto.ApplicationDTO;

public class ApplicationEditorActivity extends EditorActivityBase<ApplicationDTO> {

    @Inject
    @SuppressWarnings("unchecked")
    public ApplicationEditorActivity(ApplicationEditorView view) {
        super(view, (AbstractCrudService<ApplicationDTO>) GWT.create(ApplicationCrudService.class), ApplicationDTO.class);
    }
}
