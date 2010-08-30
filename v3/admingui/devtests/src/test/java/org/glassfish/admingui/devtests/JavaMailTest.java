/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package org.glassfish.admingui.devtests;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class JavaMailTest extends BaseSeleniumTestClass {
    private static final String TRIGGER_JAVA_MAIL = "A JavaMail session resource represents a mail session in the JavaMail API, which provides a platform-independent and protocol-independent framework to build mail and messaging applications.";
    private static final String TRIGGER_NEW_JAVAMAIL_SESSION = "New JavaMail Session";
    private static final String TRIGGER_EDIT_JAVAMAIL_SESSION = "Edit JavaMail Session";

    @Test
    public void createMailResource() {
        final String resourceName = generateRandomString();
        final String description = resourceName + " description";
        
        clickAndWait("treeForm:tree:resources:mailResources:mailResources_link", TRIGGER_JAVA_MAIL);
        clickAndWait("propertyForm:resourcesTable:topActionsGroup1:newButton", TRIGGER_NEW_JAVAMAIL_SESSION);

        selenium.type("form:propertySheet:propertSectionTextField:nameNew:name", resourceName);
        selenium.type("form:propertySheet:propertSectionTextField:hostProp:host", "localhost");
        selenium.type("form:propertySheet:propertSectionTextField:userProp:user", "user");
        selenium.type("form:propertySheet:propertSectionTextField:fromProp:from", "return@test.com");
        selenium.type("form:propertySheet:propertSectionTextField:descProp:desc", description);
        int count = addTableRow("form:basicTable", "form:basicTable:topActionsGroup1:addSharedTableButton");

        selenium.type("form:basicTable:rowGroup1:0:col2:col1St", "property"+generateRandomString());
        selenium.type("form:basicTable:rowGroup1:0:col3:col1St", "value");
        selenium.type("form:basicTable:rowGroup1:0:col4:col1St", "description");
        clickAndWait("form:propertyContentPage:topButtons:newButton", TRIGGER_JAVA_MAIL);

        assertTrue(selenium.isTextPresent(resourceName));

        clickAndWait(getLinkIdByLinkText("propertyForm:resourcesTable", resourceName), TRIGGER_EDIT_JAVAMAIL_SESSION);
        assertTableRowCount("propertyForm:basicTable", count);
        clickAndWait("propertyForm:propertyContentPage:topButtons:cancelButton", TRIGGER_JAVA_MAIL);

        testDisableButton(resourceName,
                "propertyForm:resourcesTable",
                "propertyForm:resourcesTable:topActionsGroup1:button3",
                "propertyForm:propertySheet:propertSectionTextField:statusProp:enabled",
                "propertyForm:propertyContentPage:topButtons:cancelButton",
                TRIGGER_JAVA_MAIL,
                TRIGGER_EDIT_JAVAMAIL_SESSION,
                "off");
        testEnableButton(resourceName,
                "propertyForm:resourcesTable",
                "propertyForm:resourcesTable:topActionsGroup1:button2",
                "propertyForm:propertySheet:propertSectionTextField:statusProp:enabled",
                "propertyForm:propertyContentPage:topButtons:cancelButton",
                TRIGGER_JAVA_MAIL,
                TRIGGER_EDIT_JAVAMAIL_SESSION,
                "on");

        deleteRow("propertyForm:resourcesTable:topActionsGroup1:button1", "propertyForm:resourcesTable", resourceName);
    }

    @Test
    public void createMailResourceWithTargets() {
        final String resourceName = generateRandomString();
        final String description = resourceName + " description";
        final String instanceName = generateRandomString();
        final String enableStatus = "Enabled on All Targets";
        final String disableStatus = "Disabled on All Targets";

        StandaloneTest instanceTest = new StandaloneTest();
        instanceTest.createStandAloneInstance(instanceName);

        clickAndWait("treeForm:tree:resources:mailResources:mailResources_link", TRIGGER_JAVA_MAIL);
        clickAndWait("propertyForm:resourcesTable:topActionsGroup1:newButton", TRIGGER_NEW_JAVAMAIL_SESSION);

        selenium.type("form:propertySheet:propertSectionTextField:nameNew:name", resourceName);
        selenium.type("form:propertySheet:propertSectionTextField:hostProp:host", "localhost");
        selenium.type("form:propertySheet:propertSectionTextField:userProp:user", "user");
        selenium.type("form:propertySheet:propertSectionTextField:fromProp:from", "return@test.com");
        selenium.type("form:propertySheet:propertSectionTextField:descProp:desc", description);
        int count = addTableRow("form:basicTable", "form:basicTable:topActionsGroup1:addSharedTableButton");

        selenium.type("form:basicTable:rowGroup1:0:col2:col1St", "property" + generateRandomString());
        selenium.type("form:basicTable:rowGroup1:0:col3:col1St", "value");
        selenium.type("form:basicTable:rowGroup1:0:col4:col1St", "description");

        selenium.addSelection("form:targetSection:targetSectionId:addRemoveProp:commonAddRemove_available", "label=" + instanceName);
        selenium.click("form:targetSection:targetSectionId:addRemoveProp:commonAddRemove:commonAddRemove_addButton");

        clickAndWait("form:propertyContentPage:topButtons:newButton", TRIGGER_JAVA_MAIL);

        assertTrue(selenium.isTextPresent(resourceName));

        clickAndWait(getLinkIdByLinkText("propertyForm:resourcesTable", resourceName), TRIGGER_EDIT_JAVAMAIL_SESSION);
        assertTableRowCount("propertyForm:basicTable", count);
        clickAndWait("propertyForm:propertyContentPage:topButtons:cancelButton", TRIGGER_JAVA_MAIL);

        testDisableButton(resourceName,
                "propertyForm:resourcesTable",
                "propertyForm:resourcesTable:topActionsGroup1:button3",
                "propertyForm:propertySheet:propertSectionTextField:statusProp2:enabledStr",
                "propertyForm:propertyContentPage:topButtons:cancelButton",
                TRIGGER_JAVA_MAIL,
                TRIGGER_EDIT_JAVAMAIL_SESSION,
                disableStatus);
        testEnableButton(resourceName,
                "propertyForm:resourcesTable",
                "propertyForm:resourcesTable:topActionsGroup1:button2",
                "propertyForm:propertySheet:propertSectionTextField:statusProp2:enabledStr",
                "propertyForm:propertyContentPage:topButtons:cancelButton",
                TRIGGER_JAVA_MAIL,
                TRIGGER_EDIT_JAVAMAIL_SESSION,
                enableStatus);
        manageTargets(instanceName, resourceName);

        deleteRow("propertyForm:resourcesTable:topActionsGroup1:button1", "propertyForm:resourcesTable", resourceName);
        //Delete the instance
        clickAndWait("treeForm:tree:standaloneTreeNode:standaloneTreeNode_link", instanceTest.TRIGGER_INSTANCES_PAGE);
        deleteRow("propertyForm:instancesTable:topActionsGroup1:button1", "propertyForm:instancesTable", instanceName);
        assertFalse(selenium.isTextPresent(instanceName));
    }

    private void manageTargets(String instanceName, String jndiName) {
        final String TRIGGER_EDIT_RESOURCE_TARGETS = "Resource Targets";
        final String enableStatus = "Enabled on All Targets";
        final String disableStatus = "Disabled on All Targets";
        final String TRIGGER_MANAGE_TARGETS = "Manage Targets";
        final String TRIGGGER_VALUES_SAVED = "New values successfully saved.";

        clickAndWait("treeForm:tree:resources:mailResources:mailResources_link", TRIGGER_JAVA_MAIL);
        clickAndWait(getLinkIdByLinkText("propertyForm:resourcesTable", jndiName), TRIGGER_EDIT_JAVAMAIL_SESSION);
        //Click on the target tab and verify whether the target is in the target table or not.
        clickAndWait("propertyForm:resEditTabs:targetTab", TRIGGER_EDIT_RESOURCE_TARGETS);
        assertTrue(selenium.isTextPresent(instanceName));

        //Enable all targets
        testEnableOrDisableTarget("propertyForm:targetTable:_tableActionsTop:_selectMultipleButton:_selectMultipleButton_image",
                "propertyForm:targetTable:topActionsGroup1:button2",
                "propertyForm:resEditTabs:general",
                "propertyForm:resEditTabs:targetTab",
                "propertyForm:propertySheet:propertSectionTextField:statusProp2:enabledStr",
                TRIGGER_EDIT_JAVAMAIL_SESSION,
                TRIGGER_EDIT_RESOURCE_TARGETS,
                enableStatus);

        //Disable all targets
        testEnableOrDisableTarget("propertyForm:targetTable:_tableActionsTop:_selectMultipleButton:_selectMultipleButton_image",
                "propertyForm:targetTable:topActionsGroup1:button3",
                "propertyForm:resEditTabs:general",
                "propertyForm:resEditTabs:targetTab",
                "propertyForm:propertySheet:propertSectionTextField:statusProp2:enabledStr",
                TRIGGER_EDIT_JAVAMAIL_SESSION,
                TRIGGER_EDIT_RESOURCE_TARGETS,
                disableStatus);

        //Test the manage targets
        clickAndWait("propertyForm:targetTable:topActionsGroup1:manageTargetButton", TRIGGER_MANAGE_TARGETS);
        //Remove the created instance from the selected targets.
        selenium.addSelection("form:targetSection:targetSectionId:addRemoveProp:commonAddRemove_selected", "label=" + instanceName);
        selenium.click("form:targetSection:targetSectionId:addRemoveProp:commonAddRemove:commonAddRemove_removeButton");
        clickAndWait("form:propertyContentPage:topButtons:saveButton", TRIGGGER_VALUES_SAVED);
        assertFalse(selenium.isTextPresent(jndiName));
        //Go Back to Resources Page
        clickAndWait("treeForm:tree:resources:mailResources:mailResources_link", TRIGGER_JAVA_MAIL);
    }
}
