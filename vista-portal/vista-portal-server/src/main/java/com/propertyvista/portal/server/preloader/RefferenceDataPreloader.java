/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 16, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;

import com.propertyvista.domain.ref.PhoneProvider;

public class RefferenceDataPreloader extends AbstractDataPreloader {

    public RefferenceDataPreloader() {

    }

    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(PhoneProvider.class);
        } else {
            return "This is production";
        }
    }

    @Override
    public String create() {
        createNamed(PhoneProvider.class, "Rogers", "Bell", "Telus", "Fido", "Mobilicity", "Primus", "Télébec", "Virgin Mobile", "Wind Mobile");
        return null;
    }
}
