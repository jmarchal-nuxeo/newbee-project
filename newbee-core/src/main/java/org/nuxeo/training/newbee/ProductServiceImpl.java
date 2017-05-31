package org.nuxeo.training.newbee;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;
import org.nuxeo.runtime.model.Extension;

public class ProductServiceImpl extends DefaultComponent implements ProductService {

	private static final String SCH_PRODUCT_PRICE = "sch_product:price";

	private static final String PLUGINS = "plugins";

	private static final String PRODUCT = "Product";

	private static final Log log = LogFactory.getLog(ProductServiceImpl.class);

	private Map<String, Integer> values = new HashMap<>();

	/**
	 * Component activated notification. Called when the component is activated.
	 * All component dependencies are resolved at that moment. Use this method
	 * to initialize the component.
	 *
	 * @param context
	 *            the component context.
	 */
	@Override
	public void activate(ComponentContext context) {
		super.activate(context);
	}

	/**
	 * Component deactivated notification. Called before a component is
	 * unregistered. Use this method to do cleanup if any and free any resources
	 * held by the component.
	 *
	 * @param context
	 *            the component context.
	 */
	@Override
	public void deactivate(ComponentContext context) {
		super.deactivate(context);
	}

	/**
	 * Application started notification. Called after the application started.
	 * You can do here any initialization that requires a working application
	 * (all resolved bundles and components are active at that moment)
	 *
	 * @param context
	 *            the component context. Use it to get the current bundle
	 *            context
	 * @throws Exception
	 */
	@Override
	public void applicationStarted(ComponentContext context) {
		// do nothing by default. You can remove this method if not used.
	}

	@Override
	public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
		// Add some logic here to handle contributions
	}

	@Override
	public void unregisterContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
		// Logic to do when unregistering any contribution
	}

	@Override
	public BigDecimal computePrice(DocumentModel product) {

		if (product == null || !PRODUCT.equals(product.getType())) {
			return null;
		}
		Double currentPriceAsDouble = (Double) product.getPropertyValue(SCH_PRODUCT_PRICE);
		BigDecimal price = currentPriceAsDouble == null ? BigDecimal.ZERO : new BigDecimal(currentPriceAsDouble);

		BigDecimal toAdd = values.containsKey(PRODUCT) ? new BigDecimal(values.get(PRODUCT)) : BigDecimal.TEN;
		price = price.add(toAdd);
		product.setPropertyValue(SCH_PRODUCT_PRICE, price);
		return price;
	}

	@Override
	public void registerExtension(Extension extension) {
		if (PLUGINS.equals(extension.getExtensionPoint())) {
			Object[] contribs = extension.getContributions();
			if (contribs != null) {
				for (Object contrib : contribs) {
					if (contrib instanceof ProductExtensionDescriptor) {
						ProductExtensionDescriptor descriptor = (ProductExtensionDescriptor) contrib;
						values.put(descriptor.getDocType(), descriptor.getValue());
					} else {
						log.warn(String.format("Unknown contribution %s: ignored", contrib.getClass().getName()));
					}
				}
			}
		} else {
			log.warn(String.format("Unknown contribution %s: ignored", extension.getExtensionPoint()));
		}
	}

	@Override
	public void unregisterExtension(Extension extension) {
		if (PLUGINS.equals(extension.getExtensionPoint())) {
			Object[] contribs = extension.getContributions();
			if (contribs != null) {
				for (Object contrib : contribs) {
					if (contrib instanceof ProductExtensionDescriptor) {
						ProductExtensionDescriptor descriptor = (ProductExtensionDescriptor) contrib;
						if (values.containsKey(descriptor.getDocType())) {
							values.remove(descriptor.getDocType());
						}
					} else {
						log.warn(String.format("Unknown contribution %s: ignored", contrib.getClass().getName()));
					}
				}
			}
		} else {
			log.warn(String.format("Unknown contribution %s: ignored", extension.getExtensionPoint()));
		}
	}
}
