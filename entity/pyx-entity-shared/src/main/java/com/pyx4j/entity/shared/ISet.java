/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Oct 30, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared;

import java.util.Set;

public interface ISet<TYPE extends IObject<?, ?>, PARENT extends IEntity<?>> extends IOwnedMember<PARENT>, IObject<Set<?>, ISet<TYPE, PARENT>>, Set<TYPE> {

}
