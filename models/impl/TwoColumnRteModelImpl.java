package com.kallista.core.models.impl;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.kallista.core.models.TwoColumnRteModel;

/**
 * Sling Model implementation for the Two Column RTE component.
 *
 * <p>The model is adapted from a {@link Resource} and reads authored dialog
 * values through {@link ValueMapValue} injection. The implementation is immutable
 * from a consumer perspective because it exposes read-only getters and no mutators.</p>
 */
@Model(
    adaptables = Resource.class,
    adapters = TwoColumnRteModel.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public final class TwoColumnRteModelImpl implements TwoColumnRteModel {

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String firstColumnText;

    @ValueMapValue
    private String secondColumnText;

    @ValueMapValue
    private Boolean enableSingleColumn;

    @ValueMapValue
    private String ctaLabel;

    @ValueMapValue
    private String ctaLink;

    @ValueMapValue
    private String ctaAlignment;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFirstColumnText() {
        return firstColumnText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSecondColumnText() {
        return secondColumnText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnableSingleColumn() {
        return Boolean.TRUE.equals(enableSingleColumn);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCtaLabel() {
        return ctaLabel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCtaLink() {
        return ctaLink;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCtaAlignment() {
        return ctaAlignment;
    }
}
