/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-31
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.dto;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.LeaseTerm;

@Transient
@ExtendsBO
public interface LeaseTermDTO extends LeaseTerm {

    // newly created parent (lease/application):
    IPrimitive<Boolean> isNewLease();

    // -----------------------------------------------------
    // temporary runtime data:

    Building building();

    IList<ProductItem> selectedServiceItems();

    IList<ProductItem> selectedFeatureItems();

    IList<Concession> selectedConcessions();

    IPrimitive<String> unitMoveOutNote();

    @NotNull
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    @Caption(name = "Initial Balance")
    IPrimitive<BigDecimal> carryforwardBalance();

    /**
     * this value is passed for validation of age of the tenant
     */
    IPrimitive<Integer> ageOfMajority();
}
