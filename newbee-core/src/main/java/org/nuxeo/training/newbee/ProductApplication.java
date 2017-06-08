package org.nuxeo.training.newbee;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class ProductApplication extends Application {
	@Override
	public Set<Class<?>> getClasses() {
		HashSet<Class<?>> result = new HashSet<Class<?>>();
		result.add(ProductEndPoint.class);
		return result;
	}
}
