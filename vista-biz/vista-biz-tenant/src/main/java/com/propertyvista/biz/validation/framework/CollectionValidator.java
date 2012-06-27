/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 26, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.validation.framework;

import java.util.Collection;
import java.util.Map;

import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;

public interface CollectionValidator<E extends IEntity, C extends Collection<Map<String, Object>>> extends Validator<C, ICollection<E, C>> {

}
