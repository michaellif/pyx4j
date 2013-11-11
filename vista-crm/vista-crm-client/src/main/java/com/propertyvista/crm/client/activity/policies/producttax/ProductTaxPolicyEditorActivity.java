/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 9, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.producttax;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.policies.common.PolicyEditorActivityBase;
import com.propertyvista.crm.client.ui.crud.policies.producttaxes.ProductTaxPolicyEditorView;
import com.propertyvista.crm.rpc.services.policies.policy.ProductTaxPolicyCrudService;
import com.propertyvista.domain.policy.dto.ProductTaxPolicyDTO;

public class ProductTaxPolicyEditorActivity extends PolicyEditorActivityBase<ProductTaxPolicyDTO> {

    public ProductTaxPolicyEditorActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().getView(ProductTaxPolicyEditorView.class), GWT.<ProductTaxPolicyCrudService> create(ProductTaxPolicyCrudService.class),
                ProductTaxPolicyDTO.class);
    }

}
