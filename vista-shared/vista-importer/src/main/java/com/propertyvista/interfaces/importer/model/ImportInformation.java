/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.model;

import javax.xml.bind.annotation.XmlTransient;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.essentials.rpc.ImportColumn;
import com.pyx4j.i18n.annotations.I18n;

/**
 * Operations information for data derived form XLS files
 */
@Transient
@XmlTransient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface ImportInformation extends IEntity {

    @ImportColumn(ignore = true)
    IPrimitive<String> sheet();

    @ImportColumn(ignore = true)
    IPrimitive<Integer> row();

    @ImportColumn(ignore = true)
    IPrimitive<Boolean> invalid();

    @ImportColumn(ignore = true)
    IPrimitive<String> message();
}
