/**
 * Copyright (C) 2010-2011 Sebastian Heckmann, Sebastian Laag
 *
 * Contact Email: <sebastian.heckmann@udo.edu>, <sebastian.laag@udo.edu>
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.net.URISyntaxException;

import javax.naming.NamingException;

import occi.config.OcciConfig;
import occi.http.occiApi;
import occi.infrastructure.Compute;
import occi.infrastructure.Compute.Architecture;
import occi.infrastructure.Compute.State;
import occi.libvirt.manager.VmManager;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Class to test the compute interface. The test class starts the occi api and
 * connects to it via client resource.
 * 
 * Test cases are: HTTP POST HTTP GET HTTP DELETE HTTP PUT
 * 
 * @author Sebastian Laag
 * @author Sebastian Heckmann
 */
@Test(enabled = false)
public class OcciLibvirtTest {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(OcciLibvirtTest.class);
	private final ClientResource clientResource = new ClientResource(
			OcciConfig.getInstance().config.getString("occi.server.location"));

	@BeforeMethod
	public void setUp() {
		try {
			// start occi api
			occiApi.main(null);
			VmManager vmManager = new VmManager();
		} catch (Exception ex) {
			LOGGER.error("Failed to start occiApi: " + ex.getMessage());
		}
	}

	@Test(enabled = false)
	public void testCreateComputeAndStart() {
		Compute compute = null;
		try {
			compute = new Compute(Architecture.x64, 2, "TestCase", 200, 20,
					State.active, null);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		// connect to api
		this.clientResource.setReference(OcciConfig.getInstance().config
				.getString("occi.server.location")
				+ "compute/"
				+ compute.getId() + "/?action=start");
		Form form = new Form();
		form.add("X-OCCI-Attribute", "method=start");
		LOGGER.error("web representation: " + form.toString());
		// create new representation
		Representation representation = null;
		try {
			// send post request
			representation = this.clientResource.post(form.toString(),
					new MediaType("text/occi"));
		} catch (Exception ex) {
			LOGGER.error("Failed to execute POST request " + ex.getMessage());
		}
		Assert.assertNotNull(representation);
		// get request and print it in debugger
		Request request = Request.getCurrent();
		LOGGER.debug("Request: " + request.toString() + "\n\n"
				+ form.getMatrixString());
		LOGGER.debug("--------------------------------");
		// get current response
		Response response = Response.getCurrent();
		Assert.assertNotNull(response);
		LOGGER.debug("Response: " + response.toString());
	}

	@AfterMethod
	public void tearDown() {
		System.gc();
	}
}