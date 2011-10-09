/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 14, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.property;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Reference;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.marketing.PublicVisibilityType;
import com.propertyvista.domain.ref.PhoneProvider;
import com.propertyvista.domain.ref.PhoneProviderReferenceAdapter;

@ToStringFormat("{0}{1,choice,null#|!null# ex.{1}}")
public interface PropertyPhone extends IEntity {

    @I18n
    @XmlType(name = "PropertyPhoneType")
    public enum Type {

        landLine,

        mobile,

        fax,

        pager,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @I18n
    @XmlType(name = "PhoneDesignation")
    public enum DesignationType {

        pool,

        elevator,

        intercom,

        office,

        laundry,

        pointOfSale,

        fireMonitoring;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @MemberColumn(name = "phoneType")
    IPrimitive<Type> type();

    IPrimitive<DesignationType> designation();

    IPrimitive<PublicVisibilityType> visibility();

    /**
     * in format 123-4567 123-456-7890 )
     */
    @ToString(index = 0)
    @MemberColumn(name = "phoneNumber")
    @Editor(type = EditorType.phone)
    IPrimitive<String> number();

    /**
     * (max 5 integers)
     */
    @ToString(index = 1)
    IPrimitive<Integer> extension();

    IPrimitive<String> description();

    @Editor(type = EditorType.suggest)
    @Reference(adapter = PhoneProviderReferenceAdapter.class)
    PhoneProvider provider();

}
