/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2011
 * @author dmitry
 */
package com.propertyvista.portal.server.preloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.portal.domain.Building;
import com.propertyvista.portal.domain.Unit;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;

public class PreloadBuildings extends AbstractDataPreloader {
//    private final static Logger log = LoggerFactory.getLogger(PreloadBuildings.class);
	
	private int buildingCount;

	private Building createBuilding(String name, String email) {
		Building building = EntityFactory.create(Building.class);
//		UserCredential credential = EntityFactory.create(UserCredential.class);
//
//		user.email().setValue(email);
//		user.name().setValue(name);
//
//		credential.user().set(user);
//		credential.credential().setValue(email);
//
//		credential.enabled().setValue(Boolean.TRUE);
//		credential.behavior().setValue(behavior);

		PersistenceServicesFactory.getPersistenceService().persist(building);
//		credential.setPrimaryKey(user.getPrimaryKey());
//		PersistenceServicesFactory.getPersistenceService().persist(credential);

		buildingCount++;
		return building;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String delete() {
		return deleteAll(Building.class, Unit.class);
	}

	@Override
	public String create() {
		createBuilding("a", "b");
//		createUser("CRM Admin", DemoData.CRM_ADMIN_USER_PREFIX + "001"
//				+ DemoData.USERS_DOMAIN, ExamplesBehavior.CRM_ADMIN);
//
//		createUser("Misha", "michael.lifschitz@gmail.com",
//				ExamplesBehavior.CRM_ADMIN);
//		createUser("Vlad", "skarzhevskyy@gmail.com", ExamplesBehavior.CRM_ADMIN);
//		createUser("Tester", "test1@pyx4j.com", ExamplesBehavior.CRM_ADMIN);
//		if (isGAEDevelopment()) {
//			createUser("Developer", "test@example.com",
//					ExamplesBehavior.CRM_ADMIN);
//		}
//
//		for (int i = 1; i < DemoData.maxCustomers; i++) {
//			createUser(
//					"Customer No" + CommonsStringUtils.d000(i),
//					DemoData.CRM_CUSTOMER_USER_PREFIX
//							+ CommonsStringUtils.d000(i)
//							+ DemoData.USERS_DOMAIN,
//					ExamplesBehavior.CRM_CUSTOMER);
//		}
//
//		for (int i = 1; i < DemoData.maxEmployee; i++) {
//			createUser(
//					"Emp No" + CommonsStringUtils.d000(i),
//					DemoData.CRM_EMPLOYEE_USER_PREFIX
//							+ CommonsStringUtils.d000(i)
//							+ DemoData.USERS_DOMAIN,
//					ExamplesBehavior.CRM_EMPLOYEE);
//		}

		StringBuilder b = new StringBuilder();
		b.append("Created " + buildingCount + " Users");
		return b.toString();
	}
	
	public static void main(String[] args)
	{
		System.out.println("Hey there");
//		log.info("Testing preloader of builders");
	}
}
