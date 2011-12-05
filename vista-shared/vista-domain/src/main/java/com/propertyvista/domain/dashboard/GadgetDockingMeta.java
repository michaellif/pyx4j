/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 5, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard;

import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface GadgetDockingMeta extends IEntity {

    /**
     * Define in which column this gadget is docked.<br/>
     * Valid values: <br/>
     * <li>Dashboard: 0, 1, 2</li> <br/>
     * <li>Report : -1, 0, 1</li>
     */
    @MemberColumn(name = "dockingColumn")
    IPrimitive<Integer> column();
}
