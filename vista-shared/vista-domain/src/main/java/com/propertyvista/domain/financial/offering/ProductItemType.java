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

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.GlCode;
import com.propertyvista.domain.financial.tax.Tax;

public interface ProductItemType extends IEntity {

    @I18n
    @XmlType(name = "ProductItemTypeType")
    public enum Type {

        service, feature;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    @MemberColumn(name = "itemType")
    IPrimitive<Type> type();

    @ToString
    IPrimitive<String> name();

    IPrimitive<Service.Type> serviceType();

    IPrimitive<Feature.Type> featureType();

    @NotNull
    GlCode glCode();

    /**
     * Fill in Service from @link ProductTaxPolicy
     */
    @Transient
    IList<Tax> taxes();
}
