/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 26, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi.bean.mits;

import javax.xml.bind.annotation.XmlElement;

public class Name {
    private String firstName;

    private String lastName;

    private String middleName;

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(firstName);
        sb.append(" ");
        if (middleName != null && !middleName.isEmpty()) {
            sb.append(middleName).append(" ");
        }
        sb.append(lastName);
        return sb.toString();
    }

    @XmlElement(name = "FirstName")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @XmlElement(name = "LastName")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @XmlElement(name = "MiddleName")
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

}
