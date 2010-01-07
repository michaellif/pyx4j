/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rpc;

import java.util.Vector;

import com.pyx4j.entity.shared.EntityCriteria;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.rpc.shared.Service;

public interface EntityServices {

    public interface Save extends Service<IEntity<?>, IEntity<?>> {

    };

    public interface Query extends Service<EntityCriteria, Vector> {

    };

}
