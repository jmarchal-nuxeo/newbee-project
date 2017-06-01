package org.nuxeo.training.newbee;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;

/**
 *
 */
public class ProductAdapter {
	private static final String NAME = "name";

	protected final DocumentModel doc;

	protected String titleXpath = "dc:title";
	protected String descriptionXpath = "dc:description";
	protected String priceXpath = "sch_product:price";
	protected String distributorXpath = "product:distributor";
	protected String soldXpath = "product:sold";

	public ProductAdapter(DocumentModel doc) {
		this.doc = doc;
	}

	public void create() {
		CoreSession session = doc.getCoreSession();
		session.createDocument(doc);
	}

	public void save() {
		CoreSession session = doc.getCoreSession();
		session.saveDocument(doc);
	}

	public DocumentRef getParentRef() {
		return doc.getParentRef();
	}

	// Technical properties retrieval
	public String getId() {
		return doc.getId();
	}

	public String getName() {
		return doc.getName();
	}

	public String getPath() {
		return doc.getPathAsString();
	}

	public String getState() {
		return doc.getCurrentLifeCycleState();
	}

	// Metadata get / set
	public String getTitle() {
		return doc.getTitle();
	}

	public void setTitle(String value) {
		doc.setPropertyValue(titleXpath, value);
	}

	public String getDescription() {
		return (String) doc.getPropertyValue(descriptionXpath);
	}

	public void setDescription(String value) {
		doc.setPropertyValue(descriptionXpath, value);
	}

	public Double getPrice() {
		return (Double) doc.getPropertyValue(priceXpath);
	}
	
	public String getTest() {
		return (String) doc.getPropertyValue("product:test");
	}

	public void setPrice(Double price) {
		doc.setPropertyValue(priceXpath, price);
	}

	@SuppressWarnings("unchecked")
	public String getDistributorName() {
		return (String) ((HashMap<String, Object>) doc.getPropertyValue(distributorXpath)).get(NAME);
	}

	public void setDistributorName(String name) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(NAME, name);
		doc.setPropertyValue(distributorXpath, map);
	}

	public boolean isSold() {
		return doc.getPropertyValue(soldXpath) == null ? false : (boolean) doc.getPropertyValue(soldXpath);
	}

	public void setSold(boolean sold) {
		doc.setPropertyValue(soldXpath, sold);
	}

}
