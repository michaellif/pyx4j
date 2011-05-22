/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 21, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.importer.bean;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class Parking {
	private String display;

	private String parkcode;

	private Double rate;

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(display).append(" ").append(parkcode).append(" $")
				.append(rate);

		return sb.toString();
	}

	@XmlAttribute
	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	@XmlAttribute
	public String getParkcode() {
		return parkcode;
	}

	public void setParkcode(String parkcode) {
		this.parkcode = parkcode;
	}

	@XmlValue
	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}
}
