package pl.mmakos.mortgage.view;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ActionCheckBox extends JCheckBox {
  public void fireActionPerformed() {
    super.fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
  }
}
