	/*
	 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
	 *
	 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
	 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
	 * you entered into with Property Vista Software Inc.
	 *
	 * This notice and attribution to Property Vista Software Inc. may not be removed.
	 *
	 * Created on Feb 12, 2011
	 * @author dmitry
	 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
	 */
package com.propertyvista.portal.domain.pt;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

	public interface TenantGuarantor extends IEntity {
		
	    public enum Relationship {
	        Mother, Father, Grandfather, Grandmother, Uncle, Aunt, Other
	    }

	    @Caption(name = "First Name")
	    @ToString(index = 1)
	    IPrimitive<String> firstName();

	    @Caption(name = "Middle")
	    IPrimitive<String> middleName();

	    @Caption(name = "Last Name")
	    @ToString(index = 2)
	    IPrimitive<String> lastName();

	    @Caption(name = "Birth Date")
	    IPrimitive<Date> birthDate();

	    @Caption(name = "Home")
	    IPrimitive<String> homePhone();

	    @Caption(name = "Mobile")
	    IPrimitive<String> mobilePhone();

	    @Caption(name = "Email")
	    IPrimitive<String> email();

	    @ToString(index = 0)
	    IPrimitive<Relationship> relationship();
	}
