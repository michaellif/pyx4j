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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.portal.domain.Address;
import com.propertyvista.portal.domain.Building;
import com.propertyvista.portal.domain.Complex;
import com.propertyvista.portal.domain.Email;
import com.propertyvista.portal.domain.Phone;
import com.propertyvista.portal.domain.Unit;
import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.domain.Address.AddressType;
import com.propertyvista.portal.domain.Building.BuildingType;
import com.propertyvista.portal.domain.Email.EmailType;
import com.propertyvista.portal.domain.Phone.PhoneType;
import com.propertyvista.server.domain.UserCredential;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

public class PreloadBuildings extends AbstractDataPreloader {
    private final static Logger log = LoggerFactory.getLogger(PreloadBuildings.class);

    public static int NUM_RESIDENTIAL_BUILDINGS = 10;
    public static int NUM_UNITS = 300;
    
	private int buildingCount;
	private int unitCount;
	
	private Email createEmail(String emailAddress)
	{
		Email email = EntityFactory.create(Email.class);
		
		email.emailType().setValue(EmailType.work);
		email.emailAddress().setValue(emailAddress);
		
		return email;
	}
	
	private Phone createPhone()
	{
		Phone phone = EntityFactory.create(Phone.class);
		
		phone.phoneType().setValue(PhoneType.work);
		phone.phoneNumber().setValue("(416) 555-1812");
		
		return phone;
	}
	
	private Address createAddress(String line1)
	{
		Address address = EntityFactory.create(Address.class);
		
		address.addressType().setValue(AddressType.property);
		address.addressLine1().setValue(line1);
		address.city().setValue("Toronto");
		address.state().setValue("ON");
		address.country().name().setValue("Canada"); // not sure if this will crash or not, will check later
		
		return address;
	}
	
	private Complex createComplex(int numBuildings)
	{
		if (numBuildings == 0)
			return null;
		
		Complex complex = EntityFactory.create(Complex.class);
		
		for (int i=0; i<numBuildings; i++)
		{
//			Building building 
		}
		
		return complex;
	}

	private Building createBuilding(BuildingType buildingType, Complex complex, String website, Address address, List<Phone> phones, Email email) {
		Building building = EntityFactory.create(Building.class);
		
		building.buildingType().setValue(buildingType);
//		building.complex().
		building.webSite().setValue(website);
		
		PersistenceServicesFactory.getPersistenceService().persist(building);
		
//		email.setPrimaryKey(building.getPrimaryKey());
		building.email().set(email);
		PersistenceServicesFactory.getPersistenceService().persist(email);

//		address.setPrimaryKey(building.getPrimaryKey());
		building.address().set(address);
		PersistenceServicesFactory.getPersistenceService().persist(address);

		for (Phone phone : phones)
		{
//			phone.setPrimaryKey(building.getPrimaryKey());
			building.phoneList().add(phone);
			PersistenceServicesFactory.getPersistenceService().persist(phone);
		}
//		building.phoneList().addAll(phones);

		
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

//		credential.setPrimaryKey(user.getPrimaryKey());
//		PersistenceServicesFactory.getPersistenceService().persist(credential);

		buildingCount++;
		return building;
	}
	
	public Unit createUnit(Building building, int floor, int area, float bedrooms, float bathrooms)
	{
		Unit unit = EntityFactory.create(Unit.class);
		
		unit.building().set(building);
		unit.floor().setValue(floor);
		unit.unitType().setValue("Unknown");
		unit.area().setValue(area);
		unit.bedrooms().setValue(bedrooms);
		unit.bathrooms().setValue(bathrooms);

		PersistenceServicesFactory.getPersistenceService().persist(unit);
		
		unitCount++;
		return unit;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String delete() {
        if (ApplicationMode.isDevelopment()) {
    		return deleteAll(Building.class, Unit.class);
        } else {
            return "This is production";
        }
	}

	@Override
	public String create() {
		
		
		for (int i=0; i<NUM_RESIDENTIAL_BUILDINGS; i++)
		{
			Complex complex = null;
			if (i % 3 == 0)
			{
				complex = createComplex(2);
			}
			
			String website = "www.property" + (i+1) + ".com";
			
			// address
			Address address = createAddress((i + 1) + (10 * i) + (100 * i) + " Yonge St");
			
			// phones
			List<Phone> phones = new ArrayList<Phone>();
			phones.add(createPhone());
			
			// email
			String emailAddress = "building" + (i+1) + "@propertyvista.com";
			Email email = createEmail(emailAddress);
			
			// organization contacts - not many fields there at the moment, will do this later
			
			Building building = createBuilding(BuildingType.residential, complex, website, address, phones, email);
//			log.info("Created: " + building);
			
			// now create units for the building
			for (int j=0; j<NUM_UNITS; j++)
			{
				int floor = (j + 1) / 10;
				
				int area = (j + 1) * 500;
				
				float bedrooms = 2.0f;
				float bathrooms = 2.0f;
				
				Unit unit = createUnit(building, floor, area, bedrooms, bathrooms);
//				log.info("Created: " + unit);
			}
		}
		
		StringBuilder b = new StringBuilder();
		b.append("Created " + buildingCount + " buildings, " + unitCount + " units");
		return b.toString();
	}
}
