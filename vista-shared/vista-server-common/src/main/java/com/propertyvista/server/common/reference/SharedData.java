/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 26, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.server.common.reference;

import java.util.List;

import com.pyx4j.entity.cache.CacheService;

import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;

/**
 * Please remove this class and every usage of one!! VladS
 */
@Deprecated
public class SharedData {

    public static void registerProvinces(List<Province> p) {
        CacheService.put("provinces", p);
    }

    public static void registerCountries(List<Country> c) {
        CacheService.put("countries", c);
    }

    public static List<Province> getProvinces() {
        return CacheService.get("provinces");
    }

}
