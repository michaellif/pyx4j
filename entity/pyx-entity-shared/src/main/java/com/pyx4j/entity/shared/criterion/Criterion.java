/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Jan 7, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.criterion;

import java.io.Serializable;

/**
 * Representation of a query criterion.
 * 
 * Translates to org.hibernate.criterion.Criterion in RDBMS or Query.FilterOperator in GAE
 */
public interface Criterion extends Serializable {

}
