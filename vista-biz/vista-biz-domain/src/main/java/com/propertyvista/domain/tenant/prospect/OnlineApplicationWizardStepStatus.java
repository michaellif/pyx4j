/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 14, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.tenant.prospect;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

public interface OnlineApplicationWizardStepStatus extends IEntity {

    @Owner
    @JoinColumn
    @Detached
    OnlineApplication onlineApplication();

    IPrimitive<OnlineApplicationWizardStepMeta> step();

    IPrimitive<Boolean> complete();

    IPrimitive<Boolean> visited();

}
