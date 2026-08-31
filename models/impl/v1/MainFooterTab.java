package dsm.foundation.core.models.impl.v1;

import java.util.List;

public class MainFooterTab {
	private final List<MainFooterQuadrant> quadrants;

	public MainFooterTab(List<MainFooterQuadrant> quadrants) {
		this.quadrants = quadrants;
	}

	public List<MainFooterQuadrant> getQuadrants() {
		return quadrants;
	}
}
