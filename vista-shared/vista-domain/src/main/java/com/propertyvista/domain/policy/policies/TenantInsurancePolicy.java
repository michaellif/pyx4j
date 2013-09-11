/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-13
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.policy.framework.LowestApplicableNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.property.asset.building.Building;

@DiscriminatorValue("TenantInsurancePolicy")
@LowestApplicableNode(value = Building.class)
public interface TenantInsurancePolicy extends Policy {

    IPrimitive<Boolean> requireMinimumLiability();

    @Editor(type = EditorType.money)
    @Format("#,##.00")
    @NotNull
    IPrimitive<BigDecimal> minimumRequiredLiability();

    @NotNull
    @Owned
    @Length(10 * 1024)
    @Editor(type = Editor.EditorType.textarea)
    @Caption(description = "This text is displayed in Resident Portal on a page that lets a tenant to either provide insurance or purchase TenantSure insurance.")
    IPrimitive<String> tenantInsuranceInvitation();

    @NotNull
    @Owned
    @Length(10 * 1024)
    @Editor(type = Editor.EditorType.textarea)
    @Caption(description = "This text is displayed in Resident Portal's Dashboard when a tenant doesn't have tenant insurance.")
    IPrimitive<String> noInsuranceStatusMessage();

}
