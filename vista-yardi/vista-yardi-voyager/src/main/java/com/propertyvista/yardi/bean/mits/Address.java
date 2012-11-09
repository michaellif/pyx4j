/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 29, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi.bean.mits;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * 
 <MITS:Address Type="property">
 * <MITS:Address1>430 S Fairview Avenue</MITS:Address1>
 * <MITS:City>Goleta</MITS:City>
 * <MITS:State>CA</MITS:State>
 * <MITS:PostalCode>93117</MITS:PostalCode>
 * </MITS:Address>
 * 
 */
public class Address {

    private String type;

    private String address1;

    private String address2;

    private String city;

    private String state;

    private String postalCode;

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("(").append(type).append(") ");
        sb.append(address1).append(" ");
        if (address2 != null) {
            sb.append(address2).append(" ");
        }
        sb.append(city).append(", ");
        sb.append(state).append(", ");
        sb.append(postalCode);

        return sb.toString();
    }

    @XmlAttribute(name = "Type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement(name = "Address1")
    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    @XmlElement(name = "Address2")
    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    @XmlElement(name = "City")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @XmlElement(name = "State")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @XmlElement(name = "PostalCode")
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

}
