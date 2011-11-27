/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 27, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.ido;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.maintenance.IssueElementType;
import com.propertyvista.domain.maintenance.IssuePriority;

@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Transient
public interface IssueClassificationPreload extends IEntity {

    IPrimitive<IssueElementType> type();

    IPrimitive<String> rooms();

    IPrimitive<String> repairSubject();

    IPrimitive<String> subjectDetails();

    IPrimitive<String> issue();

    IPrimitive<IssuePriority> priority();

}
