package dsm.foundation.core.models.impl.v1;

import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = org.apache.sling.api.resource.Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CtaLinks {
	@ValueMapValue
	private String text;

	@ValueMapValue
	private String href;

	public String getText() {
		return text;
	}

	public String getHref() {
		return href;
	}
}
