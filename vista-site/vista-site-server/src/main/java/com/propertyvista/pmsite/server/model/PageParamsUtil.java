/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 15, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.pmsite.server.model;

import java.util.List;
import java.util.Map;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.utils.EntityArgsConverter;

public class PageParamsUtil {

    public static PageParameters convertToPageParameters(IEntity entity) {
        Map<String, List<String>> args = EntityArgsConverter.convertToArgs(entity);
        PageParameters pageParameters = new PageParameters();

        for (String key : args.keySet()) {
            for (int i = 0; i < args.get(key).size(); i++) {
                pageParameters.add(key, args.get(key).get(i));
            }
        }
        return pageParameters;
    }

}
