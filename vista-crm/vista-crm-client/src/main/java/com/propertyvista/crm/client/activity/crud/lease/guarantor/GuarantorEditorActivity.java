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
package com.propertyvista.crm.client.activity.crud.lease.guarantor;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.ui.crud.lease.guarantor.GuarantorEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.ClientViewFactory;
import com.propertyvista.crm.rpc.services.lease.participants.GuarantorCrudService;
import com.propertyvista.dto.GuarantorDTO;

public class GuarantorEditorActivity extends EditorActivityBase<GuarantorDTO> implements GuarantorEditorView.Presenter {

    @SuppressWarnings("unchecked")
    public GuarantorEditorActivity(CrudAppPlace place) {
        super(place, ClientViewFactory.instance(GuarantorEditorView.class), (AbstractCrudService<GuarantorDTO>) GWT.create(GuarantorCrudService.class),
                GuarantorDTO.class);
    }
}
