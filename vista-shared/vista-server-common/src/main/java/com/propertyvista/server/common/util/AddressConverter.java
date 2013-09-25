/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 22, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.util;

import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.utils.EntityBinder;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.contact.AddressStructured;

public class AddressConverter {

    private static final I18n i18n = I18n.get(AddressConverter.class);

    public static class StructuredToSimpleAddressConverter extends EntityBinder<AddressStructured, AddressSimple> {

        public StructuredToSimpleAddressConverter() {
            super(AddressStructured.class, AddressSimple.class);
        }

        @Override
        protected void bind() {
            bind(toProto.city(), boProto.city());
            bind(toProto.country(), boProto.country());
            bind(toProto.province(), boProto.province());
            bind(toProto.postalCode(), boProto.postalCode());
        }

        @Override
        public void copyBOtoTO(AddressStructured dbo, AddressSimple dto) {
            super.copyBOtoTO(dbo, dto);

            //@formatter:off
            StringBuilder address = new StringBuilder();            
            address.append(val(dbo.streetNumber())).append(val(dbo.streetNumberSuffix()))
                .append(' ')
                .append(val(dbo.streetName()));
            
            String streetType = valenum(dbo.streetType());
            if (!"".equals(streetType)) {
                address.append(' ').append(streetType);
            }
            String streetDirection = valenum(dbo.streetDirection());
            if (!"".equals(streetDirection)) {
                address.append(' ').append(streetDirection);
            }
            
            address.append(", ").append(i18n.tr("Suite")).append(' ').append(val(dbo.suiteNumber()));
            dto.street1().setValue(address.toString());
            //@formatter:on
        }

        private String val(IPrimitive<String> property) {
            return property.isNull() ? "" : property.getValue();
        }

        private String valenum(IPrimitive<? extends Enum<?>> property) {
            return property.isNull() ? "" : property.getValue().toString();
        }
    }
}
