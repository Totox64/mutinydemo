package com.demo;

import javax.swing.JTextField;

public class IntegerTextField extends JTextField {

    public long getLongValue() {
        try {
            return Long.parseLong(getText());
        } catch (Exception e) {
            return 0;
        }
    }

    public void setValue(long value) {
        setText(String.valueOf(value));
    }

}
