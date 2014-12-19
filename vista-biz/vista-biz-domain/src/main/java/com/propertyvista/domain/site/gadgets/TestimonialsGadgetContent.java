/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 3, 2012
 * @author stanp
 */
package com.propertyvista.domain.site.gadgets;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.core.IList;

import com.propertyvista.domain.site.Testimonial;

@DiscriminatorValue("Testimonials")
public interface TestimonialsGadgetContent extends GadgetContent {

    @Owned
    IList<Testimonial> testimonials();
}
