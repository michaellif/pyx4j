/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Oct 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared;

public interface IOwnedMember<PARENT extends IEntity<?>> extends IMember<PARENT> {

    public PARENT getParent();

}
