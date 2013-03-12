/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.financial.offering.ProductItemType;

public class ProductItemTypeAdapter {

    /** @return product item type or <code>null</code> if a product item type for the given chargeCode haven't been found */
    public ProductItemType findProductItemType(String chargeCode) {
        EntityQueryCriteria<ProductItemType> criteria = EntityQueryCriteria.create(ProductItemType.class);
        criteria.eq(criteria.proto().yardiChargeCodes().$().yardiChargeCode(), chargeCode);
        return Persistence.service().retrieve(criteria);
    }

}
