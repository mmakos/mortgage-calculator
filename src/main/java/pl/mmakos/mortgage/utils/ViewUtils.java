package pl.mmakos.mortgage.utils;

import com.jgoodies.forms.layout.FormLayout;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ViewUtils {
  public static FormLayout getFormLayout(int columns, int rows) {
    return new FormLayout(getGrowPref(columns), getNoGrowPref(rows));
  }

  public static String getGrowPref(int cells) {
    if (cells <= 1) return "pref:grow";
    return "pref,5px,pref:grow" + ",5px,pref".repeat(Math.max(0, cells - 2));
  }

  public static String getNoGrowPref(int cells) {
    return "pref" + ",5px,pref".repeat(Math.max(0, cells - 1));
  }

  public static Border createTitledBorder(String title) {
    return BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                            title, TitledBorder.CENTER, TitledBorder.TOP),
                    BorderFactory.createEmptyBorder(6, 4, 4, 4)));
  }

  public static GridBagConstraints c(int x, int y, int wx, int leftInset, int topInset) {
    return new GridBagConstraints(x, y, 1, 1, wx, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(topInset, leftInset, 0, 0), 0, 0);
  }
}