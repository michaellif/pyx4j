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
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.common.domain;

import com.propertyvista.portal.domain.ptapp.PotentialTenant.Relationship;

public class DemoData {

    public final static int PT_GENERATION_SEED = 100;

    public final static int BUILDINGS_GENERATION_SEED = 100;

    public final static int TENANTS_GENERATION_SEED = 100;

    public final static String CRM_ADMIN_USER_PREFIX = "a";

    public final static String CRM_CUSTOMER_USER_PREFIX = "cust";

    public final static String CRM_PROPERTY_MANAGER_USER_PREFIX = "pm";

    public final static String USERS_DOMAIN = "@pyx4j.com";

    public final static String PRELOADED_USERNAME = "cust001";

    public final static String REGISTRATION_DEFAULT_PROPERTY_CODE = "740";

    public final static String REGISTRATION_DEFAULT_FLOORPLAN = "Luxury 2-bedroom";

    public final static Relationship[] RELATIONSHIPS = { Relationship.Spouse, Relationship.Daughter, Relationship.Son, Relationship.Mother,
            Relationship.Father, Relationship.Aunt, Relationship.Uncle, Relationship.GrandFather, Relationship.GrandMother, Relationship.Other };

    public final static String[] PET_BREEDS = { "Colly", "German Sheppard", "Schnautzer", "Labrador" };

    public final static String[] PET_COLORS = { "White", "Brown", "Black", "Yellow" };

    public final static String[] PET_NAMES = { "Rover", "Max", "Buddy", "Rocky", "Bear", "Jack", "Toby", "Lucky", "Shadow", "Tucker" };

    public final static String[] STREETS = { "Yonge St", "Dufferin St", "Bathurst St", "John St", "16th Ave", "Steeles Ave W" };

    public final static String[] NAME_PREFIX = { "Mr", "Mrs", "Ms", "Dr" };

    public final static String[] NAME_SUFFIX = { "Phd", "MSc", "MBA" };

    public final static String[] FIRST_NAMES = { "John", "Jim", "Bob", "Alex", "Chris", "Jack", "Jill", "Anna", "Bob", "Mark", "Jeff", "Peter", "Neil",
            "Joseph" };

    public final static String[] LAST_NAMES = { "Johnson", "Pollson", "Smith", "Woodsmith", "Black", "Smirnov", "Thomson", "Nelson", "McKindle", "Ritchie",
            "Jobs", "Ellison" };

    public final static String[] EMAIL_DOMAINS = { "gmail.com", "gmail.ca", "rogers.com", "yahoo.ca", "yahoo.com", "me.com" };

    public final static String[] CITIES = { "Toronto", "Vancouver", "Montreal", "Quebec", "Richmond Hill", "New Market", "Thornhill", "Scarborough",
            "North York" };

    public final static String[] PROVINCES = { "ON", "QC", "NS", "NB", "MB", "BC", "PE", "SK", "AB", "NL" };

    public final static String[] CAR_MAKES = { "Toyota", "BMW", "Honda", "Ford", "Nissan", "Jaguar", "Lexus" };

    public final static String[] CAR_MODELS = { "Sienna", "LS300", "Protege", "M5", "M3", "Viper" };

    public final static String[] OCCUPATIONS = { "Java Developer", "Tester", "QA", "Manager", "Project Manager", "VP", "Director", "Pilot" };

    public final static String[] COMPANY_ROLES = { "Sales", "Technical", "Help Desk" };

    public final static String[] CONTACT_ROLES = { "Administrator", "Support", "Tech" };

    public final static String[] EMPLOYER_NAMES = { "IBM", "Oracle", "Sun", "Dell", "Apple", "Microsoft", "HP", "TD Bank", "CIBC", "BMO", "RBC", "Manulife",
            "Sunlife" };

    //    public final static IncomeType[] INCOME_SOURCES = { IncomeType.pension, IncomeType.unemployment, IncomeType.retired, IncomeType.odsp, IncomeType.dividends,
    //            IncomeType.other };
    //
    //    public final static EmploymentTypes[] EMPLOYMENT_TYPES = { EmploymentTypes.none, EmploymentTypes.fulltime, EmploymentTypes.parttime,
    //            EmploymentTypes.selfemployed, EmploymentTypes.seasonallyEmployed, EmploymentTypes.socialServices, EmploymentTypes.student };
    //
    //    public final static AssetType[] ASSETS = { AssetType.bankAccounts, AssetType.realEstateProperties, AssetType.insurancePolicies, AssetType.shares,
    //            AssetType.unitTrusts, AssetType.businesses, AssetType.cars, AssetType.other };
}
