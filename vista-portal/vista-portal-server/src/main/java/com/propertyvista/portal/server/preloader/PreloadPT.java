/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 12, 2011
 * @author dmitry
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.portal.server.preloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;

public class PreloadPT extends AbstractDataPreloader {
    private final static Logger log = LoggerFactory.getLogger(PreloadPT.class);

    @Override
    public String delete() {
        //        if (ApplicationMode.isDevelopment()) {
        //            return deleteAll(Building.class, Unit.class);
        //        } else {
        //            return "This is production";
        //        }
        return "oops";
    }

    @Override
    public String create() {
        log.info("Creating PT stuff");
        StringBuilder b = new StringBuilder();
        b.append("Created nothing at all");
        return b.toString();
    }
}
