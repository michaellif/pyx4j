/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 30, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.financial.offering;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.i18n.annotations.I18n;

@Inheritance
@AbstractEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface ChargeItemExtraData extends IEntity {

}
