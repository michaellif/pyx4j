/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 16, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.converter;

import com.pyx4j.entity.shared.utils.EntityDtoBinder;

import com.propertyvista.domain.marketing.AdvertisingBlurb;
import com.propertyvista.interfaces.importer.model.AdvertisingBlurbIO;

public class AdvertisingBlurbConverter extends EntityDtoBinder<AdvertisingBlurb, AdvertisingBlurbIO> {

    public AdvertisingBlurbConverter() {
        super(AdvertisingBlurb.class, AdvertisingBlurbIO.class);
    }

    @Override
    protected void bind() {
        bind(dtoProto.content(), dboProto.content());
    }

}
