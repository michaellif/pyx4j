/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 26, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.landlord;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.landlord.LandlordEditorView;
import com.propertyvista.crm.rpc.services.building.LandlordCrudService;
import com.propertyvista.dto.LandlordDTO;

public class LandlordEditorActivity extends CrmEditorActivity<LandlordDTO> implements LandlordEditorView.Presenter {

    public LandlordEditorActivity(CrudAppPlace place) {
        super(place, CrmSite.getViewFactory().getView(LandlordEditorView.class), GWT.<AbstractCrudService<LandlordDTO>> create(LandlordCrudService.class),
                LandlordDTO.class);

    }
}
