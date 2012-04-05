/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.organisation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;

import com.propertyvista.crm.client.ui.crud.organisation.vendor.VendorEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.OrganizationViewFactory;
import com.propertyvista.crm.rpc.services.organization.VendorCrudService;
import com.propertyvista.domain.property.vendor.Vendor;

public class VendorEditorActivity extends EditorActivityBase<Vendor> {

    public VendorEditorActivity(Place place) {
        super(place,

        OrganizationViewFactory.instance(VendorEditorView.class),

        GWT.<AbstractCrudService<Vendor>> create(VendorCrudService.class),

        Vendor.class);
    }

}
