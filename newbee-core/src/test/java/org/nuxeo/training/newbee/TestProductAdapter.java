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
import org.nuxeo.training.newbee.ProductAdapter;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@Deploy({ "org.nuxeo.training.newbee.newbee-core", "studio.extensions.jmarchal-SANDBOX" })
public class TestProductAdapter {
	@Inject
	CoreSession session;

	@Test
	public void shouldCallTheAdapter() {
		String doctype = "Product";
		String testTitle = "My Adapter Title";

		DocumentModel doc = session.createDocumentModel("/", "test-adapter", doctype);
		ProductAdapter adapter = doc.getAdapter(ProductAdapter.class);
		adapter.setTitle(testTitle);
		adapter.setPrice(9.99d);
		adapter.create();
		session.save();

		Assert.assertNotNull(adapter);
		Assert.assertEquals(testTitle, adapter.getTitle());
		Assert.assertEquals(9.99d, adapter.getPrice(), 1e-15);
	}
}
