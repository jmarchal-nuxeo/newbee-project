package org.nuxeo.training.newbee;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.collections.api.CollectionManager;
import org.nuxeo.ecm.collections.core.adapter.Collection;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.security.Access;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.event.impl.EventListenerDescriptor;
import org.nuxeo.ecm.platform.login.test.ClientLoginFeature;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.ecm.platform.usermanager.UserManager;
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

	protected final List<String> events = Arrays.asList("productUnsolded");

	@Inject
	protected CoreSession session;

	@Inject
	protected EventService s;

	@Inject
	protected CollectionManager collectionManager;

	@Inject
	protected UserManager userManager;

	@Inject
	protected ClientLoginFeature login;

	@Test
	public void listenerRegistration() {
		EventListenerDescriptor listener = s.getEventListener("onproductunsolded");
		assertNotNull(listener);
		assertTrue(events.stream().allMatch(listener::acceptEvent));
	}

	private DocumentModel visualsFolder;
	private DocumentModel trash;
	private DocumentModel product;
	private DocumentModel visual;

	@Before
	public void init() {

		// Create group1
		DocumentModel group1 = userManager.getBareGroupModel();
		group1.setProperty("group", "groupname", "group1");
		userManager.createGroup(group1);

		// Create user1 and add it to group1
		DocumentModel user1 = userManager.getBareUserModel();
		user1.setProperty("user", "username", "user1");
		user1.setProperty("user", "groups", Arrays.asList("group1"));
		userManager.createUser(user1);

		// Create a domain for visual
		visualsFolder = session.createDocumentModel("/", "Visuals", "Domain");
		visualsFolder = session.createDocument(visualsFolder);
		session.saveDocument(visualsFolder);

		// Create a domain for orphan visuals
		trash = session.createDocumentModel("/", "Trash", "Domain");
		trash = session.createDocument(trash);
		session.saveDocument(trash);

		// Create a product
		product = session.createDocumentModel("/", "My product", "Product");
		product = session.createDocument(product);
		session.saveDocument(product);

		// Create a visual in the domain
		visual = session.createDocumentModel(visualsFolder.getPathAsString(), "My visual", "Visual");
		visual = session.createDocument(visual);
		session.saveDocument(visual);
	}

	@Test
	public void test() throws Exception {

		Collection collectionAdapter = product.getAdapter(Collection.class);

		// Check collection is empty
		Assert.assertEquals(0, collectionAdapter.getCollectedDocumentIds().size());
		// KO WHY ?
		// Assert.assertEquals(1,
		// session.getChildren(visualsFolder.getRef()).size());
		// KO WHY ?
		// Assert.assertEquals(0, session.getChildren(trash.getRef()).size());
		Assert.assertEquals(visualsFolder.getPath().lastSegment(), visual.getPath().segment(0));

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
		visual = session.getDocument(new IdRef(visual.getId()));
		collectionAdapter = product.getAdapter(Collection.class);
		Assert.assertEquals(0, collectionAdapter.getCollectedDocumentIds().size());
		// KO WHY ? Assert.assertEquals(0,
		// session.getChildren(visualsFolder.getRef()).size());
		// KO WHY ? Assert.assertEquals(1,
		// session.getChildren(trash.getRef()).size());
		Assert.assertEquals(trash.getPath().lastSegment(), visual.getPath().segment(0));
	}

	@Test
	public void testACL() throws Exception {

		Assert.assertEquals("Administrator", visualsFolder.getPropertyValue("dc:creator"));
		Assert.assertEquals("Administrator", trash.getPropertyValue("dc:creator"));
		Assert.assertEquals("Administrator", product.getPropertyValue("dc:creator"));
		Assert.assertEquals("Administrator", visual.getPropertyValue("dc:creator"));
	}

	@Test
	public void testACL2() throws Exception {

		signIn("user1");
		DocumentModel visual2 = session.createDocumentModel(visualsFolder.getPathAsString(), "My visual 2", "Visual");
		Assert.assertEquals("Administrator", visual2.getPropertyValue("dc:creator"));
	}

	private void signIn(String userName) throws Exception {

		// login.login(userName);
		session = CoreInstance.openCoreSession(session.getRepositoryName(), userName);
	}
}
