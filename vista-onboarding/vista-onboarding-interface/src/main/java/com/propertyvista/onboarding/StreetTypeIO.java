/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.onboarding;

import javax.xml.bind.annotation.XmlType;

/**
 * @see com.propertyvista.domain.contact.AddressStructured.StreetType
 */
@XmlType(name = "StreetType")
public enum StreetTypeIO {

    alley, approach, arcade, avenue, boulevard, brow, bypass, causeway, circuit, circle, circus, close, copse, corner, cove, court, crescent, drive, end, esplanande, flat, freeway, frontage, gardens, glade, glen, green, grove, heights, highway, lane, line, link, loop, mall, mews, packet, parade, park, parkway, place, promenade, reserve, ridge, rise, road, row, square, street, strip, tarn, terrace, thoroughfaree, track, trunkway, view, vista, walk, way, walkway, yard,

    other
}
