/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Oct 30, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared;

import java.util.Map;
import java.util.Set;

public interface ISet<TYPE extends IObject<?, ?>> extends IObject<ISet<TYPE>, Set<Map<String, ?>>>, Set<TYPE> {

}
