/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 22, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.common.domain.ref.City;
import com.propertyvista.common.domain.ref.Province;

@Transient
public interface PropertySearchCriteria extends IEntity {

    enum BedroomType {

        all,

        oneBedroom,

        twoBedroom,

        threeBedroom,

        fourBedroom,

        fiveBedroomAndMore;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    enum BathroomType {

        all,

        oneBath,

        twoBath,

        threeBathAndMore;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    enum PriceRangeType {
        all,

        under600,

        between600and699,

        between700and799,

        between800and899,

        between900and999,

        over1000;
    }

    @Caption(name = "Province")
    Province province();

    @Caption(name = "City")
    City city();

    @Caption(name = "Beds")
    IPrimitive<BedroomType> numOfBeds();

    @Caption(name = "Baths")
    IPrimitive<BathroomType> numOfBath();

    @Caption(name = "Price")
    IPrimitive<PriceRangeType> price();
}
