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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pyx4j.entity.core.EntityFactory;

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
        if (adBlurb == null || adBlurb.isNull()) {
            return null;
        }
        AdvertisingBlurbIO adBlurbIO = new AdvertisingBlurbIO();

        adBlurbIO.content = MarshallerUtils.createIo(StringIO.class, adBlurb.content());
        return adBlurbIO;
    }

    public List<AdvertisingBlurbIO> marshal(Collection<AdvertisingBlurb> adblurbs) {
        List<AdvertisingBlurbIO> adblurbIOList = new ArrayList<AdvertisingBlurbIO>();
        for (AdvertisingBlurb adblurb : adblurbs) {
            adblurbIOList.add(marshal(adblurb));
        }
        return adblurbIOList;
    }

    @Override
    public AdvertisingBlurb unmarshal(AdvertisingBlurbIO adBlurbIO) {
        AdvertisingBlurb adBlurb = EntityFactory.create(AdvertisingBlurb.class);

        MarshallerUtils.setValue(adBlurb.content(), adBlurbIO.content);
        return adBlurb;
    }

    public List<AdvertisingBlurb> unmarshal(Collection<AdvertisingBlurbIO> adblurbIOList) {
        List<AdvertisingBlurb> adblurbs = new ArrayList<AdvertisingBlurb>();
        for (AdvertisingBlurbIO adblurbIO : adblurbIOList) {
            AdvertisingBlurb adblurb = EntityFactory.create(AdvertisingBlurb.class);
            MarshallerUtils.set(adblurb, adblurbIO, AdvertisingBlurbMarshaller.getInstance());
            adblurbs.add(adblurb);
        }
        return adblurbs;
    }
}
