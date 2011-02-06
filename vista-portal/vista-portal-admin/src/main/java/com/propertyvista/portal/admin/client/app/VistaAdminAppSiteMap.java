/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.admin.client.app;

import com.pyx4j.site.shared.meta.NavigNode;
import com.pyx4j.site.shared.meta.SiteMap;

public class VistaAdminAppSiteMap implements SiteMap {

    public interface App extends NavigNode {

        public interface Dashboard extends NavigNode {
        }

        public interface ProcessTemplate extends NavigNode {

            public interface Edit extends NavigNode {
            }
        }

        public interface Users extends NavigNode {

            public interface Edit extends NavigNode {
            }
        }

        public interface Home extends NavigNode {

            public interface TechnicalSupport extends NavigNode {
            }

            public interface ContactUs extends NavigNode {
            }
        }
    }
}
