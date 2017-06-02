package org.nuxeo.training.newbee;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.collections.api.CollectionManager;
import org.nuxeo.ecm.collections.core.adapter.Collection;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

public class OnProductUnsolded implements EventListener {

	@Override
	public void handleEvent(Event event) {
		EventContext ctx = event.getContext();
		if (!(ctx instanceof DocumentEventContext)) {
			return;
		}

		DocumentEventContext docCtx = (DocumentEventContext) ctx;
		DocumentModel doc = docCtx.getSourceDocument();

		if (!"Product".equals(doc.getType())) {
			return;
		}

		if ("productUnsolded".equals(event.getName())) {

			Collection collectionAdapter = doc.getAdapter(Collection.class);

			CollectionManager collectionManager = Framework.getService(CollectionManager.class);

			List<DocumentRef> idRefs = new ArrayList<>();
			DocumentModelList list = new DocumentModelListImpl();
			for (String id : collectionAdapter.getCollectedDocumentIds()) {
				IdRef idRef = new IdRef(id);
				DocumentModel content = ctx.getCoreSession().getDocument(idRef);
				list.add(content);
				idRefs.add(idRef);
			}

			collectionManager.removeAllFromCollection(doc, list, ctx.getCoreSession());

			ctx.getCoreSession().move(idRefs, new IdRef((String) ctx.getProperty("trashId")));
			ctx.getCoreSession().save();
		}
	}
}
