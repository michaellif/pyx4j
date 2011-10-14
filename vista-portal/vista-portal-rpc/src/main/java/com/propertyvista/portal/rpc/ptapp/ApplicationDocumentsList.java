/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 16, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.ptapp;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.media.ApplicationDocument;

/**
 * This class in created because we don't have instance of IList by itself
 */
@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface ApplicationDocumentsList extends IEntity {

    IList<ApplicationDocument> documents();

}
