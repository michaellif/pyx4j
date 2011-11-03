/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-13
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.property.vendor;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

public interface Warranty extends IEntity {

    @I18n
    @XmlType(name = "WarrantyType")
    public enum Type {

        parts,

        labor,

        partsAndLabor,

        full,

        partial,

        conditional,

        other;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @EmbeddedEntity
    @Caption(name = "Contract details")
    Contract contract();

    @ToString(index = 1)
    IPrimitive<String> title();

    @ToString(index = 2)
    @MemberColumn(name = "warrantyType")
    IPrimitive<Type> type();

    @Owned
    @Caption(name = "Warranted Items")
    IList<WarrantyItem> items();
}
