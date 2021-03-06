/*
 * Copyright � 2003 Sun Microsystems, Inc.  All rights reserved.  U.S.
 * Government Rights - Commercial software.  Government users are subject
 * to the Sun Microsystems, Inc. standard license agreement and
 * applicable provisions of the FAR and its supplements.  Use is subject
 * to license terms.
 *
 * This distribution may include materials developed by third parties.
 * Sun, Sun Microsystems, the Sun logo, Java and J2EE are trademarks
 * or registered trademarks of Sun Microsystems, Inc. in the U.S. and
 * other countries.
 
 */


package dataregistry;

import java.util.Collection;

import javax.ejb.*;


public interface LocalVendorHome extends EJBLocalHome {

    public LocalVendor findByPrimaryKey(VendorKey aKey) throws FinderException;

    public LocalVendor create(int vendorId, String name, String address,
            String contact, String phone) throws CreateException;

    public Collection findByPartialName(String name) throws FinderException;

    public Collection findByOrder(Integer orderId) throws FinderException;

}
