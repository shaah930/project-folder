package dsm.foundation.core.models.impl.v1;

import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

@Model(adaptables = org.apache.sling.api.resource.Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Contact {
	@ValueMapValue
	private String title;

	@ValueMapValue
	private String phone;

	@ValueMapValue(name = "class")
	private String cssClass;

	@ChildResource(name = "ctalinks")
	private List<CtaLinks> ctalinks;

	public String getTitle() {
		return title;
	}

	public String getPhone() {
		return phone;
	}

	@JsonProperty("class")
	public String getCssClass() {
		return cssClass;
	}

	public List<CtaLinks> getCtalinks() {
		return ctalinks;
	}
}
