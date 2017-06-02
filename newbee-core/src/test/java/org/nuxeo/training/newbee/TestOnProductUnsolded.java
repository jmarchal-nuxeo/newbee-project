package org.nuxeo.training.newbee;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.collections.api.CollectionManager;
import org.nuxeo.ecm.collections.core.adapter.Collection;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.event.impl.EventListenerDescriptor;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class })
@Deploy({ "org.nuxeo.training.newbee.newbee-core", "studio.extensions.jmarchal-SANDBOX",
		"org.nuxeo.ecm.platform.collections.core" })
public class TestOnProductUnsolded {

	protected final List<String> events = Arrays.asList("documentModified", "documentRemoved");

	@Inject
	protected CoreSession session;

	@Inject
	protected EventService s;

	@Inject
	protected CollectionManager collectionManager;

	@Test
	public void listenerRegistration() {
		EventListenerDescriptor listener = s.getEventListener("onproductunsolded");
		assertNotNull(listener);
		assertTrue(events.stream().allMatch(listener::acceptEvent));
	}

	@Test
	public void test() {

		// Create a domain for visual
		DocumentModel visualsFolder = session.createDocumentModel("Domain");
		visualsFolder = session.createDocument(visualsFolder);
		session.saveDocument(visualsFolder);
		// Create a domain for orphan visuals
		DocumentModel trash = session.createDocumentModel("Domain");
		trash = session.createDocument(trash);
		session.saveDocument(trash);
		// Create a product
		DocumentModel product = session.createDocumentModel("Product");
		product = session.createDocument(product);
		session.saveDocument(product);
		// Create a visual in the domain
		DocumentModel visual = session.createDocumentModel(visualsFolder.getPathAsString(), "My visual", "Visual");
		visual = session.createDocument(visual);
		session.saveDocument(visual);

		Collection collectionAdapter = product.getAdapter(Collection.class);

		Assert.assertEquals(0, collectionAdapter.getCollectedDocumentIds().size());
		Assert.assertEquals(1, session.getChildren(new IdRef(visualsFolder.getId())).size());
		Assert.assertEquals(0, session.getChildren(new IdRef(trash.getId())).size());

		// Add visual to collection
		collectionManager.addToCollection(product, visual, session);
		session.save();

		Assert.assertEquals(1, collectionAdapter.getCollectedDocumentIds().size());

		EventProducer eventProducer = Framework.getService(EventProducer.class);
		DocumentEventContext ctx = new DocumentEventContext(session, session.getPrincipal(), product);
		Event event = ctx.newEvent("productUnsolded");
		ctx.setProperty("trashId", trash.getId());
		eventProducer.fireEvent(event);

		product = session.getDocument(new IdRef(product.getId()));
		collectionAdapter = product.getAdapter(Collection.class);
		Assert.assertEquals(0, collectionAdapter.getCollectedDocumentIds().size());

		Assert.assertEquals(0, session.getChildren(new IdRef(visualsFolder.getId())).size());
		Assert.assertEquals(1, session.getChildren(new IdRef(trash.getId())).size());

	}
}
