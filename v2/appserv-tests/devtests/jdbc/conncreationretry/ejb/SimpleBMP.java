package com.sun.s1asdev.jdbc.connectioncreationretry.ejb;

import javax.ejb.*;
import java.rmi.*;

public interface SimpleBMP
        extends EJBObject {
    public boolean test1() throws RemoteException;
}
