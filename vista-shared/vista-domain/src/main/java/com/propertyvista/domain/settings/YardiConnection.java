/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-15
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.settings;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface YardiConnection extends IEntity {

    public enum Platform {
        SQL, Oracle
    }

    @Caption(name = "Web service URL")
    IPrimitive<String> serveviceURL();

    @Caption(name = "Web service User")
    IPrimitive<String> username();

    @Caption(name = "Password")
    IPrimitive<String> credential();

    IPrimitive<String> serverName();

    @MemberColumn(name = "db")
    IPrimitive<String> database();

    IPrimitive<Platform> platform();

}
