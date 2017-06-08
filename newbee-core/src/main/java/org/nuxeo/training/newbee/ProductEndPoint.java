package org.nuxeo.training.newbee;

import java.text.NumberFormat;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentNotFoundException;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.runtime.api.Framework;

@WebObject(type = "Product")
@Path("/product")
public class ProductEndPoint extends ModuleRoot {

	final NumberFormat formatter = NumberFormat.getCurrencyInstance();

	@GET
	@Path("{productId}")
	public String get(@PathParam("productId") String productId) {

		CoreSession session = ctx.getCoreSession();

		DocumentModel doc = null;

		try {
			doc = session.getDocument(new IdRef(productId));
		} catch (DocumentNotFoundException dnfe) {
			return "Document not found";
		}

		if (!"Product".equals(doc.getType())) {
			return "Document is not a product";
		}

		ProductService productService = Framework.getService(ProductService.class);
		productService.computePrice(doc);
		session.saveDocument(doc);

		ProductAdapter product = new ProductAdapter(doc);
		product.create();

		return "Price updated for product " + product.getTitle() + " : " + formatter.format(product.getPrice());
	}

}