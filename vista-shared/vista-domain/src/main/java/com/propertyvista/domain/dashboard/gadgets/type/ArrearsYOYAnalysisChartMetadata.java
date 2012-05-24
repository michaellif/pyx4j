/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 23, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.type;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;

@Caption(name = "Arrears YOY Analysis Chart", description = "A graph that visually demonstrates the arrear balance each month over the course of multiple years")
@DiscriminatorValue("ArrearsYOYAnalysisChartMetadata")
public interface ArrearsYOYAnalysisChartMetadata extends GadgetMetadata {

    @Caption(description = "Set the number of years ago to compare to the current year")
    @NotNull
    IPrimitive<Integer> yearsToCompare();
}
