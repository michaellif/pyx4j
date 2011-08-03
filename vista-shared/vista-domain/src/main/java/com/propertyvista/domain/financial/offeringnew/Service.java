/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 22, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial.offeringnew;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

public interface Service extends IEntity {

    @Translatable
    public enum Type {

        residentialUnit,

        commercialUnit,

        residentialShortTermUnit,

        roof,

        sundry,

        garage;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

// ----------------------------------------------

    @Owner
    @Detached
    ServiceCatalog catalog();

// ----------------------------------------------

    @MemberColumn(name = "serviceType")
    IPrimitive<Type> type();

    IPrimitive<String> name();

    IPrimitive<String> description();

    // Double link - main dependency in item entity
    IList<ServiceItem> items();

    IPrimitive<DepositType> depositType();

    IList<Feature> features();

    IList<Concession> concessions();
}
