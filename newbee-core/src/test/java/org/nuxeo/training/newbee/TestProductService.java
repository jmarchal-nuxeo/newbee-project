package org.nuxeo.training.newbee;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features({ AutomationFeature.class })
@Deploy({ "org.nuxeo.training.newbee.newbee-core", "studio.extensions.jmarchal-SANDBOX" })
public class TestProductService {

	@Inject
	protected ProductService productservice;
	@Inject
	private CoreSession session;

	@Test
	public void testService() {
		assertNotNull(productservice);
	}

	@Test
	public void testWithNull() {

		BigDecimal result = productservice.computePrice(null);
		Assert.assertNull(result);
	}

	@Test
	public void testWithBadType() {

		DocumentModel document = session.createDocumentModel("Visual");
		document = session.createDocument(document);

		BigDecimal result = productservice.computePrice(document);
		Assert.assertNull(result);
	}

	@Test
	public void testWithoutInit() {

		DocumentModel document = session.createDocumentModel("Product");
		document = session.createDocument(document);

		BigDecimal result = productservice.computePrice(document);
		Assert.assertEquals(BigDecimal.TEN, result);
	}

	@Test
	public void test() {

		DocumentModel document = session.createDocumentModel("Product");
		document.setPropertyValue("sch_product:price", 5f);
		document = session.createDocument(document);

		BigDecimal result = productservice.computePrice(document);
		Assert.assertEquals(new BigDecimal(15), result);
	}
}
