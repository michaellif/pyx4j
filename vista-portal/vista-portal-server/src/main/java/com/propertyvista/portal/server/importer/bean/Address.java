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

public class Address {

	private String street;

	private String city;

	private String post;

	private String province;

	private String prv;

	private String country;

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(street);
		sb.append(", ").append(city);
		sb.append(", ").append(prv);
		sb.append(", ").append(country);

		return sb.toString();
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getPrv() {
		return prv;
	}

	public void setPrv(String prv) {
		this.prv = prv;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}
