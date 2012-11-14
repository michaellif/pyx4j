/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 27, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.server.openapi.model;

public class AddressRS {

    public String addressType;

    //mandatory for portal
    public String streetNumber;

    //mandatory for portal
    public String streetName;

    public String streetNumberSuffix;

    public String streetDirection;

    //mandatory for portal
    public String streetType;

    public String numberOfUnits;

    //mandatory for portal
    public String city;

    //mandatory for portal
    public String province;

    public String provinceCode;

    public String postalCode;

    //mandatory for portal
    public String country;

    public String county;

    //mandatory for portal
    public GeoLocation location;
}
