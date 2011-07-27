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

import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

public interface Warranty extends Contract {

    @Translatable
    public enum Type {

        parts,

        labour,

        partsAndLabour,

        full,

        partial,

        conditional,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    // ----------- Infromation -------------

    IPrimitive<String> title();

    @MemberColumn(name = "warrantyType")
    IPrimitive<Type> type();

    @Owned
    IList<WarrantyItem> items();
}
