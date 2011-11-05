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
package com.propertyvista.crm.client.activity.crud.marketing.inquiry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;

import com.propertyvista.crm.client.ui.crud.marketing.inquiry.InquiryEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.MarketingViewFactory;
import com.propertyvista.crm.rpc.services.InquiryCrudService;
import com.propertyvista.dto.InquiryDTO;

public class InquiryEditorActivity extends EditorActivityBase<InquiryDTO> {

    @SuppressWarnings("unchecked")
    public InquiryEditorActivity(Place place) {
        super((InquiryEditorView) MarketingViewFactory.instance(InquiryEditorView.class), (AbstractCrudService<InquiryDTO>) GWT
                .create(InquiryCrudService.class), InquiryDTO.class);
        setPlace(place);
    }
}
