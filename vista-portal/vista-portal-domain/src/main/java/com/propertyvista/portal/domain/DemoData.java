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
package com.propertyvista.portal.domain;

public class DemoData {

    /*
     * TODO vlads Why do we use two different notations for static members Technically
     * they are of the same kind, just different type
     */

    public static int maxCustomers = 100;

    public static int maxEmployee = 300;

    public static int maxAdmin = 2;

    public static String CRM_ADMIN_USER_PREFIX = "a";

    public static String CRM_CUSTOMER_USER_PREFIX = "cust";

    public static String CRM_EMPLOYEE_USER_PREFIX = "emp";

    public static String USERS_DOMAIN = "@pyx4j.com";

    public static int NUM_RESIDENTIAL_BUILDINGS = 3;

    public static int NUM_POTENTIAL_TENANTS = 3;

    //    public static int NUM_UNITS = 10;

    public final static String[] FIRST_NAMES = { "John", "Jim", "Bob", "Alex", "Chris" };

    public final static String[] LAST_NAMES = { "Johnson", "Pollson", "Smith", "Woodsmith", "Black", "Smirnov", "Thomson", "Nelson", "McKindle" };

    public final static String[] EMAIL_DOMAINS = { "gmail.com", "gmail.ca", "rogers.com", "yahoo.ca", "yahoo.com" };

    public final static String[] RELATIONSHIPS = { "self", "spouse", "child" };

    public final static String[] CITIES = { "Toronto", "Vancouver", "Montreal", "Quebec", "Richmond Hill", "New Market", "Thornhill", "Scarbough" };

    public final static String[] PROVINCES = { "ON", "QC", "NS", "NB", "MB", "BC", "PE", "SK", "AB", "NL" };

    public final static String[] CAR_MAKES = { "Toyota", "BMW", "Honda", "Ford", "Nissan" };

    public final static String[] CAR_MODELS = { "Sienna", "LS300", "Protege", "M5", "M3", "Viper" };

    public final static String[] OCCUPATIONS = { "Java Developer", "Tester", "QA", "Manager", "Project Manager", "VP", "Director", "Pilot" };

    public final static String[] EMPLOYER_NAMES = { "IBM", "Oracle", "Sun", "Dell", "Apple", "Microsoft" };

    public final static String[] INCOME_SOURCES = { "Rent", "Software Development", "Consulting", "Trading" };

    public final static String[] ASSETS = { "House", "Car", "Condo", "TV", "Computer", "Professional Equipment" };
}
