package com.budwk.code.ui;

import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import java.awt.event.*;

public class CodeGeneratorUIDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField tf_author;
    private JTextField tf_basePackage;
    private JTextField tf_baseUri;
    private JTextField tf_webPath;
    private JTextField tf_servicePath;
    private JTextField tf_modelPath;
    private JCheckBox swaggerCheckBox;
    private JCheckBox replaceCheckBox;
    private DialogCallBack dialogCallBack;

    public CodeGeneratorUIDialog(String fileName, DialogCallBack dialogCallBack) {
        this.dialogCallBack = dialogCallBack;
        setTitle("Cenerate Model: " + fileName);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        if ("".equals(tf_basePackage.getText().trim())) {
            Messages.showErrorDialog("Base Package cannot be empty", "Error");
            return;
        }
        if ("".equals(tf_modelPath.getText().trim())) {
            Messages.showErrorDialog("Model Path cannot be empty", "Error");
            return;
        }
        if ("".equals(tf_servicePath.getText().trim())) {
            Messages.showErrorDialog("Service Path cannot be empty", "Error");
            return;
        }
        if ("".equals(tf_webPath.getText().trim())) {
            Messages.showErrorDialog("Web Path cannot be empty", "Error");
            return;
        }
        if ("".equals(tf_baseUri.getText().trim())) {
            Messages.showErrorDialog("Base URI cannot be empty", "Error");
            return;
        }
        dialogCallBack.ok(tf_author.getText().trim(), tf_basePackage.getText().trim(),
                tf_modelPath.getText().trim(),
                tf_servicePath.getText().trim(), tf_webPath.getText().trim(), tf_baseUri.getText().trim(), swaggerCheckBox.isSelected(), replaceCheckBox.isSelected());
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public interface DialogCallBack {
        void ok(String author, String basePackage, String modelPath, String servicePath,
                String webPath, String baseUri, boolean swagger, boolean replace);
    }
}
