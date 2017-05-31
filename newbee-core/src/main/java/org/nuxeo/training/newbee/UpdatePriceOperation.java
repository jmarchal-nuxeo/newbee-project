package org.nuxeo.training.newbee;

import org.apache.commons.collections.CollectionUtils;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.runtime.api.Framework;

/**
 *
 */
@Operation(id = UpdatePriceOperation.ID, category = Constants.CAT_DOCUMENT, label = "Update Price", description = "Update price for all products")
public class UpdatePriceOperation {

	public static final String ID = "Document.UpdatePriceOperation";

	@Context
	protected OperationContext operationContext;

	@Context
	protected CoreSession session;

	@OperationMethod
	public boolean run() {
		return run((DocumentModel) null);
	}

	@OperationMethod
	public boolean run(DocumentModel product) {
		if (product == null || !"Product".equals(product.getType())) {
			return false;
		}

		ProductService productService = Framework.getService(ProductService.class);
		productService.computePrice(product);
		session.saveDocument(product);
		return true;
	}

	@OperationMethod
	public int run(DocumentModelList products) {
		int result = 0;
		if (CollectionUtils.isEmpty(products)) {
			return result;
		}

		for (DocumentModel product : products) {
			result += run(product) ? 1 : 0;
		}
		return result;
	}
}
