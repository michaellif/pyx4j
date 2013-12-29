/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-16
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.security.common;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.i18n.annotations.I18n;

@AbstractEntity
@Inheritance(strategy = Inheritance.InheritanceStrategy.TABLE_PER_CLASS)
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface AbstractPmcUser extends AbstractUser {

}
