/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.ob.rpc;

import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.NotificationAppPlace;
import com.pyx4j.site.shared.meta.PublicPlace;
import com.pyx4j.site.shared.meta.SiteMap;

public class OnboardingSiteMap implements SiteMap {

    public static class Landing extends AppPlace {
    }

    public static class RuntimeError extends NotificationAppPlace {
    }

    public static class PmcAccountTerms extends AppPlace implements PublicPlace {

    }

    public static class PmcAccountCreationRequest extends AppPlace {
    }

    public static class PmcAccountCreationProgress extends AppPlace {
    }

    public static class PmcAccountCreationComplete extends AppPlace {
    }

}
