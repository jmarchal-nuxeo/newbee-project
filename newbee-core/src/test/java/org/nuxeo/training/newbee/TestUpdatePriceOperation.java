package org.nuxeo.training.newbee;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy({ "org.nuxeo.training.newbee.newbee-core", "studio.extensions.jmarchal-SANDBOX" })
public class TestUpdatePriceOperation {

	@Inject
	protected CoreSession session;

	@Inject
	protected AutomationService automationService;

	@Test
	public void shouldNotCallWithNullParameters() throws OperationException {

		OperationContext ctx = new OperationContext(session);
		boolean updated = (boolean) automationService.run(ctx, UpdatePriceOperation.ID);
		Assert.assertFalse(updated);
	}

	@Test
	public void shouldCallWithBadType() throws OperationException {

		DocumentModel document = session.createDocumentModel("File");
		document = session.createDocument(document);

		OperationContext ctx = new OperationContext(session);
		ctx.setInput(document);
		boolean updated = (boolean) automationService.run(ctx, UpdatePriceOperation.ID);
		Assert.assertFalse(updated);
	}

	@Test
	public void shouldCallWithoutInit() throws OperationException {

		DocumentModel document = session.createDocumentModel("Product");
		document = session.createDocument(document);

		OperationContext ctx = new OperationContext(session);
		ctx.setInput(document);
		boolean updated = (boolean) automationService.run(ctx, UpdatePriceOperation.ID);
		Assert.assertTrue(updated);

		document = session.getDocument(new IdRef(document.getId()));
		Assert.assertEquals(10.0d, (double) document.getPropertyValue("sch_product:price"), 1e-15);
	}

	@Test
	public void shouldCall() throws OperationException {

		DocumentModel document = session.createDocumentModel("Product");
		document.setPropertyValue("sch_product:price", 10f);
		document = session.createDocument(document);

		OperationContext ctx = new OperationContext(session);
		ctx.setInput(document);
		boolean updated = (boolean) automationService.run(ctx, UpdatePriceOperation.ID);
		Assert.assertTrue(updated);

		document = session.getDocument(new IdRef(document.getId()));
		Assert.assertEquals(20.0d, (double) document.getPropertyValue("sch_product:price"), 1e-15);
	}

	@Test
	public void shouldCallMulti() throws OperationException {

		DocumentModelList documents = new DocumentModelListImpl();

		for (int i = 0; i < 5; i++) {
			DocumentModel document = session.createDocumentModel("Product");
			document.setPropertyValue("sch_product:price", 10f * i);
			document = session.createDocument(document);
			documents.add(document);
		}

		DocumentModel document = session.createDocumentModel("Visual");
		document = session.createDocument(document);
		documents.add(document);

		OperationContext ctx = new OperationContext(session);
		ctx.setInput(documents);
		int updated = (int) automationService.run(ctx, UpdatePriceOperation.ID);
		Assert.assertEquals(5, updated);
	}

	@Test
	@LocalDeploy("org.nuxeo.training.newbee.newbee-core:test-contrib.xml")
	public void shouldCallWithContrib() throws OperationException {

		DocumentModel document = session.createDocumentModel("Product");
		document.setPropertyValue("sch_product:price", 10f);
		document = session.createDocument(document);

		OperationContext ctx = new OperationContext(session);
		ctx.setInput(document);
		boolean updated = (boolean) automationService.run(ctx, UpdatePriceOperation.ID);
		Assert.assertTrue(updated);

		document = session.getDocument(new IdRef(document.getId()));
		Assert.assertEquals(110.0d, (double) document.getPropertyValue("sch_product:price"), 1e-15);
	}

}
