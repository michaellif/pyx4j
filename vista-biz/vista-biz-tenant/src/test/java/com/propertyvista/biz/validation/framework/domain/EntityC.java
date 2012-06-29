/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 29, 2012
 * @author Artyom
 * @version $Id$
 */
package com.propertyvista.biz.validation.framework.domain;

import com.pyx4j.entity.shared.IEntity;

public interface EntityC extends IEntity {

    EntityB memberA();

    EntityB memberB();

}
