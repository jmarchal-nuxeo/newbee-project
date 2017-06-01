package org.nuxeo.training.newbee;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@Deploy({ "org.nuxeo.training.newbee.newbee-core", "studio.extensions.jmarchal-SANDBOX" })
public class TestProductAdapter {
	@Inject
	CoreSession session;

	@Test
	public void shouldCallTheAdapter() {
		DocumentModel doc = session.createDocumentModel("/", "test-adapter", "Product");
		ProductAdapter adapter = doc.getAdapter(ProductAdapter.class);
		adapter.create(); // to place here otherwise product.sold is not default valued
		adapter.setTitle("My Adapter Title");
		adapter.setPrice(9.99d);
		adapter.setDistributorName("Distributor");
		session.save();

		Assert.assertNotNull(adapter);
		Assert.assertEquals("My Adapter Title", adapter.getTitle());
		Assert.assertEquals(9.99d, adapter.getPrice(), 1e-15);
		Assert.assertTrue(adapter.isSold());
	}
}
