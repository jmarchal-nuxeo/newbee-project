package org.nuxeo.training.newbee;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

public class FileAdapterFactory implements DocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> itf) {
        if ("File".equals(doc.getType()) && doc.hasSchema("dublincore")){
            return new FileAdapter(doc);
        }else{
            return null;
        }
    }
}
