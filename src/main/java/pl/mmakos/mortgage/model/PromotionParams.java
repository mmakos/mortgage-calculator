package pl.mmakos.mortgage.model;

import java.util.Optional;

public record PromotionParams(int period, double margin) {
  static Optional<PromotionParams> promotionFor(int installmentNumber, PromotionParams[] promotions) {
    int currentLength = 0;
    for (PromotionParams promotion : promotions) {
      currentLength += promotion.period;
      if (currentLength >= installmentNumber) {
        return Optional.of(promotion);
      }
    }
    return Optional.empty();
  }
}