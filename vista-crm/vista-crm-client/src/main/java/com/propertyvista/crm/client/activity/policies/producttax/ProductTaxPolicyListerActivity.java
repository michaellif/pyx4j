/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.producttax;

import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.policies.producttaxes.ProductTaxPolicyListerView;
import com.propertyvista.domain.policy.dto.ProductTaxPolicyDTO;

public class ProductTaxPolicyListerActivity extends AbstractPrimeListerActivity<ProductTaxPolicyDTO> {

    public ProductTaxPolicyListerActivity(AppPlace place) {
        super(ProductTaxPolicyDTO.class, place, CrmSite.getViewFactory().getView(ProductTaxPolicyListerView.class));
    }

}
