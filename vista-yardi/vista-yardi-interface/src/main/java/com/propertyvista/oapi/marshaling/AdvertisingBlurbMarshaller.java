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

    @Override
    public AdvertisingBlurbIO unmarshal(AdvertisingBlurb adBlurb) {
        AdvertisingBlurbIO adBlurbIO = new AdvertisingBlurbIO();
        adBlurbIO.content = new StringIO(adBlurb.content().getValue());
        return adBlurbIO;
    }

    @Override
    public AdvertisingBlurb marshal(AdvertisingBlurbIO adBlurbIO) throws Exception {
        AdvertisingBlurb adBlurb = EntityFactory.create(AdvertisingBlurb.class);
        adBlurb.content().setValue(adBlurbIO.content.value);
        return adBlurb;
    }
}
