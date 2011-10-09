/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 27, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.domain.contact;

import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

@EmbeddedEntity
public interface Address extends IEntity, IAddressFull {

    @I18n
    public enum AddressType {

        property,

        mailing,

        shipping,

        billing,

        current,

        previous,

        forwarding, // on move out this is the address

        legal_notice,

        termination_notice,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    IPrimitive<GeoPoint> location();

    IPrimitive<AddressType> addressType();
}
