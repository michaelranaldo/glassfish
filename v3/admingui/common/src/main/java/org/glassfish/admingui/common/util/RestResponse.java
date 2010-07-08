/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
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
package org.glassfish.admingui.common.util;

import com.sun.jersey.api.client.ClientResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 *  <p>	This class abstracts the response from the admin console code so that
 *	we can use JSON / REST interchangeably.</p>
 *
 *  @author jasonlee
 *  @author Ken Paulsen (ken.paulsen@oracle.com)
 */
public abstract class RestResponse {
    public abstract int getResponseCode();
    public abstract String getResponseBody();

    public static RestResponse getRestResponse(ClientResponse response) {
        return new JerseyRestResponse(response);
    }

    public boolean isSuccess() {
        int status = getResponseCode();
        return (status >= 200) && (status <= 299);
    }

    /**
     *	<p> This method abstracts the physical response to return a consistent
     *	    data structure.  This data structure will look like:</p>
     *
     *	<p>
     *	<code>
     *	    Map&lt;String, Object&gt;
     *	    {
     *		"responseCode" : Integer    // HTTP Response code, ie. 200
     *		"output" : String	    // The Raw Response Body
     *		"description" : String	    // Command Description
     *		// 0 or more messages returned from the command
     *		"messages" : List&lt;Map&lt;String, Object&gt;&gt;
     *		[
     *		    {
     *			"message" : String  // Raw Message String
     *			"..."	  : String  // Additional custom attributes
     *			// List of properties for this message
     *			"properties" : List&lt;Map&lt;String, Object&gt;&gt;
     *			[
     *			    {
     *				"name"  : String    // The Property Name
     *				"value" : String    // The Property Value
     *				"properties" : List // Child Properties
     *			    }, ...
     *			]
     *		    }, ...
     *		]
     *	    }
     *	</code>
     *	</p>
     */
    public abstract Map<String, Object> getResponse();
}


class JerseyRestResponse extends RestResponse {
    protected ClientResponse response;

    public JerseyRestResponse(ClientResponse response) {
        this.response = response;
    }

    @Override
    public String getResponseBody() {
        return response.getEntity(String.class);
    }

    @Override
    public int getResponseCode() {
        return response.getStatus();
    }

    /**
     *	<p> This method abstracts the physical response to return a consistent
     *	    data structure.  This data structure will look like:</p>
     *
     *	<p>
     *	<code>
     *	    Map&lt;String, Object&gt;
     *	    {
     *		"responseCode" : Integer    // HTTP Response code, ie. 200
     *		"responseBody" : String	    // The Raw Response Body
     *		"description"  : String	    // Command Description
     *		"exit-code"    : String	    // Command Exit Code (i.e. SUCCESS)
     *		// 0 or more messages returned from the command
     *		"messages" : List&lt;Map&lt;String, Object&gt;&gt;
     *		[
     *		    {
     *			"message" : String  // Raw Message String
     *			"..."	  : String  // Additional custom attributes
     *			// List of properties for this message
     *			"properties" : List&lt;Map&lt;String, Object&gt;&gt;
     *			[
     *			    {
     *				"name"  : String    // The Property Name
     *				"value" : String    // The Property Value
     *				"properties" : List // Child Properties
     *			    }, ...
     *			]
     *			"messages" : List   // Child messages
     *		    }, ...
     *		]
     *	    }
     *	</code>
     *	</p>
     */
    @Override
    public Map<String, Object> getResponse() {
        Document document = MiscUtil.getDocument(getResponseBody());
        Element root = document.getDocumentElement();
	Map<String, Object> response = new HashMap<String, Object>(5);

	// Add the Response Code
	response.put("responseCode", getResponseCode());
	// Add the Response Body
	response.put("responseBody", getResponseBody());
	// Add the Command Description
	response.put("description", root.getAttribute("description"));
	response.put("exit-code", root.getAttribute("exit-code"));

	// Add the messages
        List<Map<String, Object>> messages = new ArrayList<Map<String, Object>>(2);
	response.put("messages", messages);

	// Iterate over each node looking for message-part
        NodeList nl = root.getChildNodes();
	int len = nl.getLength();
	Node child;
	for (int idx=0; idx < len; idx++) {
	    child = nl.item(idx);
	    if ((child.getNodeType() == Node.ELEMENT_NODE) && (child.getNodeName().equals("message-part"))) {
		messages.add(processMessagePart(child));
	    }
	}

	// Return the populated response data structure
        return response;
    }

    /**
     *	<p> This method returns a fully populated Map<String, Object> for the
     *	    given "message-part" <code>Node</code>.</p>
     */
    private Map<String, Object> processMessagePart(Node messageNode) {
	// Create a Map to hold all the Message info...
	Map<String, Object> message = new HashMap<String, Object>(5);

	// Pull off all the attributes from the message...
	NamedNodeMap attributes = messageNode.getAttributes();
	int attLen = attributes.getLength();
	for (int attIdx=0; attIdx<attLen; attIdx++) {
	    // "message" should be one of them... add them all
	    Node attribute = attributes.item(attIdx);
	    message.put(attribute.getNodeName(), attribute.getNodeValue());
	}

	// Now see if there are any child message-parts or child properties
        NodeList nl = messageNode.getChildNodes();
	int len = nl.getLength();
	Node child;
	boolean hasChildMessages = false;
	boolean hasProperty = false;
	List<Map<String, Object>> properties = null;
	List<Map<String, Object>> messages = null;
	for (int idx=0; idx < len; idx++) {
	    child = nl.item(idx);
	    if ((child.getNodeType() == Node.ELEMENT_NODE)) {
		if (child.getNodeName().equals("message-part")) {
		    // Recursively add this new message-part child
		    if (!hasChildMessages) {
			// Create a List to hold the messages.
			messages = new ArrayList<Map<String, Object>>(2);
			message.put("messages", messages);
			hasChildMessages = true;
		    }
		    // Add the message
		    messages.add(processMessagePart(child));
		} else if (child.getNodeName().equals("property")) {
		    // Add this new property
		    if (!hasProperty) {
			// Create a List to hold the properties.
			properties = new ArrayList<Map<String, Object>>(10);
			message.put("properties", properties);
			hasProperty = true;
		    }
		    // Add the property
		    properties.add(processProperty(child));
		}
	    }
	}

	// Return the populated message
	return message;
    }

    /**
     *	<p> This method returns a fully populated Map<String, Object> for the
     *	    given "property" <code>Node</code>.</p>
     */
    private Map<String, Object> processProperty(Node propertyNode) {
	// Create a Map to hold all the Message info...
	Map<String, Object> property = new HashMap<String, Object>(5);

	// Pull off all the attributes from the property...
	NamedNodeMap attributes = propertyNode.getAttributes();
	int attLen = attributes.getLength();
	for (int attIdx=0; attIdx<attLen; attIdx++) {
	    // "name" and "value" should be the only 2, but add them all...
	    Node attribute = attributes.item(attIdx);
	    property.put(attribute.getNodeName(), attribute.getNodeValue());
	}

	// Now see if there are any child properties
        NodeList nl = propertyNode.getChildNodes();
	int len = nl.getLength();
	Node child;
	boolean hasProperty = false;
	List<Map<String, Object>> properties = null;
	for (int idx=0; idx < len; idx++) {
	    child = nl.item(idx);
	    if ((child.getNodeType() == Node.ELEMENT_NODE)) {
		if (child.getNodeName().equals("property")) {
		    // Add this new property
		    if (!hasProperty) {
			// Create a List to hold the properties.
			properties = new ArrayList<Map<String, Object>>(10);
			property.put("properties", properties);
			hasProperty = true;
		    }
		    // Add the property
		    properties.add(processProperty(child));
		}
	    }
	}

	// Return the populated property data structure
	return property;
    }
}
