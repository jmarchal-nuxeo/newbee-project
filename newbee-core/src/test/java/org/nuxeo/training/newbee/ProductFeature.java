package org.nuxeo.training.newbee;

import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.directory.sql.SQLDirectoryFeature;
import org.nuxeo.ecm.platform.login.test.ClientLoginFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.SimpleFeature;

@Deploy({ "org.nuxeo.ecm.platform.api", "org.nuxeo.ecm.platform.content.template", "org.nuxeo.ecm.platform.dublincore",
    "org.nuxeo.ecm.platform.usermanager.api", "org.nuxeo.ecm.platform.usermanager", "org.nuxeo.ecm.core.io",
    "org.nuxeo.ecm.platform.query.api", "org.nuxeo.ecm.platform.test:test-usermanagerimpl/directory-config.xml", "org.nuxeo.training.newbee.newbee-core", "studio.extensions.jmarchal-SANDBOX",
	"org.nuxeo.ecm.platform.collections.core" })
@Features({ CoreFeature.class, ClientLoginFeature.class, SQLDirectoryFeature.class })
public class ProductFeature extends SimpleFeature {

}
