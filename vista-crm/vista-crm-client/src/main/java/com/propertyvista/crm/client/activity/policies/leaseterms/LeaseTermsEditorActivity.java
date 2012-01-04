/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 3, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.leaseterms;

import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.client.ui.crud.form.IEditorView;

import com.propertyvista.domain.policy.policies.specials.LegalTermsDescriptor;

public class LeaseTermsEditorActivity extends EditorActivityBase<LegalTermsDescriptor> {

    public LeaseTermsEditorActivity(Place place, IEditorView<LegalTermsDescriptor> view, AbstractCrudService<LegalTermsDescriptor> service,
            Class<LegalTermsDescriptor> entityClass) {
        super(place, view, service, entityClass);
        // TODO Auto-generated constructor stub
    }

}
