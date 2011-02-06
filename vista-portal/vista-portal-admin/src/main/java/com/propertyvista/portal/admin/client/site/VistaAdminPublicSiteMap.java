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
package com.propertyvista.portal.admin.client.site;

import com.pyx4j.site.shared.meta.NavigNode;

public class VistaAdminPublicSiteMap {

    public interface Pub extends NavigNode {

        public interface Home extends NavigNode {

            public interface Landing extends NavigNode {
            }

            public interface E530 extends NavigNode {
            }

            public interface SignOut extends NavigNode {
            }

        }

        public interface About extends NavigNode {
        }

        public interface FAQ extends NavigNode {
        }

        public interface Contact extends NavigNode {
        }
    }

    public enum Widgets {

        AcceptDeclineWidget,

        SignOutWidget

    }
}
