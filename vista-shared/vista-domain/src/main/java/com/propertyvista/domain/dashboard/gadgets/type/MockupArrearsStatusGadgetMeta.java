/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 1, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.type;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;

@DiscriminatorValue("MockupArrearsStatusGadgetMeta")
public interface MockupArrearsStatusGadgetMeta extends ListerGadgetBaseMetadata {

    public enum Category {
        Rent, Parking, Other, Total;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        };
    }

    @NotNull
    IPrimitive<Category> category();
}
