package com.kallista.core.models;

/**
 * Sling Model contract for the Two Column RTE component.
 *
 * <p>This model provides authored values for title and rich text content in
 * first/second columns, as well as CTA configuration and a toggle for
 * single-column rendering.</p>
 */
public interface TwoColumnRteModel {

    /**
     * Gets the authored title.
     *
     * @return component title
     */
    String getTitle();

    /**
     * Gets the first column rich text content.
     *
     * @return first column rich text markup
     */
    String getFirstColumnText();

    /**
     * Gets the second column rich text content.
     *
     * @return second column rich text markup
     */
    String getSecondColumnText();

    /**
     * Gets the CTA label.
     *
     * @return CTA label text
     */
    String getCtaLabel();

    /**
     * Gets the CTA link.
     *
     * @return CTA link path or URL
     */
    String getCtaLink();

    /**
     * Gets the CTA alignment.
     *
     * @return CTA alignment (e.g. left, center, right)
     */
    String getCtaAlignment();

    /**
     * Indicates whether the component should render in single-column mode.
     *
     * @return {@code true} when single-column mode is enabled; otherwise {@code false}
     */
    boolean isEnableSingleColumn();
}