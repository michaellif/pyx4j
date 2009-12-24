/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 24, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.gae.domain;

public interface Employee {

    Long employeeNumber();

    String firstName();

    String lastName();

    String phoneNumber();

    Address address();

    Job job();

}
