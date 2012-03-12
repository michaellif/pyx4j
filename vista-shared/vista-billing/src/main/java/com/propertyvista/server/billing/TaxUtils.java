/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 4, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.server.billing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.billing.BillChargeTax;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.financial.tax.Tax;
import com.propertyvista.domain.policy.policies.ProductTaxPolicy;
import com.propertyvista.domain.policy.policies.domain.ProductTaxPolicyItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;
import com.propertyvista.server.common.policy.PolicyManager;

public class TaxUtils {

    public static List<BillChargeTax> calculateTaxes(final BigDecimal baseAmount, ProductItemType productItemType, Building building) {
        List<Tax> taxes = retrieveTaxes(productItemType, building);
        return calculateTaxes(baseAmount, taxes);
    }

    public static Collection<? extends BillChargeTax> calculateTaxes(BigDecimal value, LeaseAdjustmentReason reason, Building building) {
        // TODO Auto-generated method stub
        return null;
    }

    static List<BillChargeTax> calculateTaxes(final BigDecimal baseAmount, List<Tax> taxes) {

        List<BillChargeTax> chargeTaxes = new ArrayList<BillChargeTax>();
        if (taxes != null) {
            BigDecimal interimAmount = baseAmount;
            for (Tax tax : taxes) {
                if (!tax.compound().getValue()) {
                    BillChargeTax chargeTax = EntityFactory.create(BillChargeTax.class);
                    chargeTax.tax().set(tax);
                    chargeTax.amount().setValue(baseAmount.multiply(tax.rate().getValue()).setScale(2, RoundingMode.HALF_UP));
                    chargeTaxes.add(chargeTax);
                    interimAmount = interimAmount.add(chargeTax.amount().getValue());
                }
            }
            for (Tax tax : taxes) {
                if (tax.compound().getValue()) {
                    BillChargeTax chargeTax = EntityFactory.create(BillChargeTax.class);
                    chargeTax.tax().set(tax);
                    chargeTax.amount().setValue(interimAmount.multiply(tax.rate().getValue()).setScale(2, RoundingMode.HALF_UP));
                    chargeTaxes.add(chargeTax);
                    interimAmount = interimAmount.add(chargeTax.amount().getValue());
                }
            }
        }
        return chargeTaxes;
    }

    private static List<Tax> retrieveTaxes(ProductItemType productItemType, Building building) {

        ProductTaxPolicy productTaxPolicy = PolicyManager.obtainEffectivePolicy(building, ProductTaxPolicy.class);

        ProductTaxPolicyItem productTaxPolicyItem = null;
        {
            EntityQueryCriteria<ProductTaxPolicyItem> criteria = new EntityQueryCriteria<ProductTaxPolicyItem>(ProductTaxPolicyItem.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().taxPolicy(), productTaxPolicy));
            productTaxPolicyItem = Persistence.service().retrieve(criteria);
        }
        return productTaxPolicyItem.taxes();
    }

}
