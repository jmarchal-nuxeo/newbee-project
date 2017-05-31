package org.nuxeo.training.newbee;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("plugin")
public class ProductExtensionDescriptor implements Serializable {

	public static final List<String> DEFAULT_FILTER = new ArrayList<String>();

	private static final long serialVersionUID = 1L;

	@XNode("@name")
	protected String name;

	@XNode("@docType")
	protected String docType;

	@XNode("@value")
	private Integer value;

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

}
