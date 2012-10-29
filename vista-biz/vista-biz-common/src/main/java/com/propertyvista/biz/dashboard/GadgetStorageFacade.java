/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-29
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.dashboard;

import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public interface GadgetStorageFacade {

    GadgetMetadata load(String gadgetId);

    void save(GadgetMetadata gadgetMetadata, boolean allowOverwrite);

    void delete(String gadgetId);
}
