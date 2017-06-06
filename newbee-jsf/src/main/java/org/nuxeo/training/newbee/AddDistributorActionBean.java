package org.nuxeo.training.newbee;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;

/**
 *
 * Code skeleton for a Seam bean that will manage a simple action. This can be
 * used to : - do a navigation - do some modification on the currentDocument (or
 * other docs) - create new documents - send/retrive info from an external
 * service - ...
 */
@Name("adddistributor")
@Scope(ScopeType.EVENT)
public class AddDistributorActionBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(AddDistributorActionBean.class);

	@In(create = true, required = false)
	protected transient CoreSession documentManager;

	@In(create = true)
	protected NavigationContext navigationContext;

	@In(create = true, required = false)
	protected transient FacesMessages facesMessages;

	@In(create = true, required = false)
	protected NuxeoPrincipal currentNuxeoPrincipal;

	@In(create = true)
	protected DocumentsListsManager documentsListsManager;

	protected String sellLocation;

	protected List<DocumentModel> getCurrentlySelectedDocuments() {

		if (navigationContext.getCurrentDocument().isFolder()) {
			return documentsListsManager.getWorkingList(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION);
		} else {
			return null;
		}
	}

	public String submit() {
		List<DocumentModel> selectedDocs = getCurrentlySelectedDocuments();

		if (CollectionUtils.isEmpty(selectedDocs)) {
			return null;
		}

		int updated = 0;
		for (DocumentModel selectedDoc : selectedDocs) {
			if (!"Product".equals(selectedDoc.getType())) {
				continue;
			}

			ProductAdapter adapter = selectedDoc.getAdapter(ProductAdapter.class);
			adapter.setDistributorName("Distributor " + new Date().getTime());
			adapter.setDistributorSellLocation(getSellLocation() + " " + new Date().getTime());
			adapter.save();
			updated++;
		}

		facesMessages.add(StatusMessage.Severity.INFO, updated + " documents updated");

		return null;
	}

	public boolean canAddDistributor() {
		return !getCurrentlySelectedDocuments().isEmpty();
	}

	public boolean accept() {
		return true;
	}

	public String getSellLocation() {
		return sellLocation;
	}

	public void setSellLocation(String sellLocation) {
		this.sellLocation = sellLocation;
	}
}
