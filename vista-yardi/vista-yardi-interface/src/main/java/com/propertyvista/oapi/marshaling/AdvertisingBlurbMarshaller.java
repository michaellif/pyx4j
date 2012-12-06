/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 24, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.marshaling;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.marketing.AdvertisingBlurb;
import com.propertyvista.oapi.model.AdvertisingBlurbIO;
import com.propertyvista.oapi.xml.StringIO;

public class AdvertisingBlurbMarshaller implements Marshaller<AdvertisingBlurb, AdvertisingBlurbIO> {

    private static class SingletonHolder {
        public static final AdvertisingBlurbMarshaller INSTANCE = new AdvertisingBlurbMarshaller();
    }

    private AdvertisingBlurbMarshaller() {
    }

    public static AdvertisingBlurbMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public AdvertisingBlurbIO marshal(AdvertisingBlurb adBlurb) {
        AdvertisingBlurbIO adBlurbIO = new AdvertisingBlurbIO();
        adBlurbIO.content = new StringIO(adBlurb.content().getValue());
        return adBlurbIO;
    }

    @Override
    public AdvertisingBlurb unmarshal(AdvertisingBlurbIO adBlurbIO) throws Exception {
        AdvertisingBlurb adBlurb = EntityFactory.create(AdvertisingBlurb.class);
        MarshallerUtils.ioToEntity(adBlurb.content(), adBlurbIO.content);
        return adBlurb;
    }
}
