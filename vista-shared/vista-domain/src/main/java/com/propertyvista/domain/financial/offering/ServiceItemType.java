/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 26, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial.offering;

import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

public interface ServiceItemType extends IEntity {

    @Translatable
    public enum Type {

        service, feature;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @NotNull
    @MemberColumn(name = "itemType")
    IPrimitive<Type> type();

    @ToString
    IPrimitive<String> name();

    IPrimitive<Service.Type> serviceType();

    IPrimitive<Feature.Type> featureType();
}
