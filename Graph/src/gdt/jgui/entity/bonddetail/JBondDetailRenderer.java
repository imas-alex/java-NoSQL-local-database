package gdt.jgui.entity.bonddetail;

import gdt.data.entity.BondDetailHandler;
import gdt.data.entity.EdgeHandler;
import gdt.data.entity.facet.ExtensionHandler;

import gdt.data.store.Entigrator;
import gdt.jgui.entity.edge.JBondsPanel;

public class JBondDetailRenderer extends JBondsPanel{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	@Override
	public String getTitle() {
		return "Details";
	}

	@Override
	public String getCategoryIcon(Entigrator entigrator) {
		return ExtensionHandler.loadIcon(entigrator, EdgeHandler.EXTENSION_KEY,"detail.png");
	}

	@Override
	public String getCategoryTitle() {
		return "Details";

	}
	@Override
	public String getFacetHandler() {
		return BondDetailHandler.class.getName();
	}

	@Override
	public String getFacetIcon() {
		return "detail.png";
	}
	@Override
	public String getEntityType() {
		return "bond.detail";
	}
	@Override
	public String getFacetOpenItem() {
		return JBondDetailFacetOpenItem.class.getName();
	}


}
