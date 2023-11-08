package pl.mmakos.mortgage.model;

/**
 * If toValue is true, then excess is calculated monthly such that rate will not be less than value
 * Else excess will be taken directly from value and calculated with given period
 */
public record ExcessesParams(int from, TimePeriod period, double value, boolean toValue) {
}
