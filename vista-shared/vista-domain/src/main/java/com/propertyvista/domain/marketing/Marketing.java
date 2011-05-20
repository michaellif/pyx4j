/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-19
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.marketing;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

public interface Marketing extends IEntity {

    IPrimitive<String> name();

    IPrimitive<String> description();

    IList<AdvertisingBlurb> addBlurbs();
}
