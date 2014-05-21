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

import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.shared.utils.EntityBinder;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.contact.AddressStructured.StreetType;
import com.propertyvista.domain.contact.InternationalAddress;

public class AddressConverter {

    public static class StructuredToInternationalAddressConverter extends EntityBinder<AddressStructured, InternationalAddress> {

        public StructuredToInternationalAddressConverter() {
            super(AddressStructured.class, InternationalAddress.class);
        }

        @Override
        protected void bind() {
            bind(toProto.city(), boProto.city());
            bind(toProto.country(), boProto.country());
            bind(toProto.province(), boProto.province().name());
            bind(toProto.postalCode(), boProto.postalCode());
        }

        @Override
        public void copyBOtoTO(AddressStructured dbo, InternationalAddress dto) {
            super.copyBOtoTO(dbo, dto);

            dto.streetNumber().setValue(val(dbo.streetNumber()) + val(dbo.streetNumberSuffix()));
            dto.streetName().setValue(getStreetName(dbo));
            dto.suiteNumber().setValue(val(dbo.suiteNumber()));
        }

        public String getStreetName(AddressStructured as) {
            StringBuilder streetName = new StringBuilder().append(val(as.streetName()));
            String streetType = valenum(as.streetType());
            if (!"".equals(streetType) && as.streetType().getValue() != StreetType.other) {
                streetName.append(' ').append(streetType);
            }
            String streetDirection = valenum(as.streetDirection());
            if (!"".equals(streetDirection)) {
                streetName.append(' ').append(streetDirection);
            }
            return streetName.toString();
        }

        public String getStreetAddress(AddressStructured addressToConvert) {
            StringBuilder address = new StringBuilder();
            address.append(val(addressToConvert.streetNumber())).append(val(addressToConvert.streetNumberSuffix())).append(' ')
                    .append(getStreetName(addressToConvert));
            return address.toString();
        }

        private String val(IPrimitive<String> property) {
            return property.isNull() ? "" : property.getValue();
        }

        private String valenum(IPrimitive<? extends Enum<?>> property) {
            return property.isNull() ? "" : property.getValue().toString();
        }
    }
}
