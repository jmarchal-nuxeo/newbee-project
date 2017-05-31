package org.nuxeo.training.newbee;

import java.math.BigDecimal;

import org.nuxeo.ecm.core.api.DocumentModel;

public interface ProductService {

	public BigDecimal computePrice(DocumentModel product);
}
