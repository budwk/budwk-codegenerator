package com.budwk.code.ui;

import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
    private JTextField tf_projectName;
    private DialogCallBack dialogCallBack;
    private String projectName = "wk";
    private String modelName = "";

    public CodeGeneratorUIDialog(String fileName, String basePackage, DialogCallBack dialogCallBack) {
        this.dialogCallBack = dialogCallBack;
        this.tf_basePackage.setText(basePackage);
        modelName = basePackage.substring(basePackage.lastIndexOf(".") + 1);
        this.tf_baseUri.setText("/platform/" + modelName);
        this.tf_projectName.setText(projectName);
        this.setText();
        this.setViews(fileName);
    }

    private void setText() {
        this.tf_modelPath.setText(projectName + "-module");
        this.tf_servicePath.setText(projectName + "-nb-service-" + modelName);
        this.tf_webPath.setText(projectName + "-nb-web-admin");
    }

    private void setViews(String fileName) {
        setTitle("Cenerate Model: " + fileName);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        this.tf_projectName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                projectName = tf_projectName.getText().trim();
                setText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                projectName = tf_projectName.getText().trim();
                setText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                projectName = tf_projectName.getText().trim();
                setText();
            }
        });

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
        if ("".equals(tf_projectName.getText().trim())) {
            Messages.showErrorDialog("Project Name cannot be empty", "Error");
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
        dialogCallBack.ok(tf_author.getText().trim(), tf_projectName.getText().trim(), tf_basePackage.getText().trim(),
                tf_modelPath.getText().trim(),
                tf_servicePath.getText().trim(), tf_webPath.getText().trim(), tf_baseUri.getText().trim(), swaggerCheckBox.isSelected(), replaceCheckBox.isSelected());
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public interface DialogCallBack {
        void ok(String author, String projectName, String basePackage, String modelPath, String servicePath,
                String webPath, String baseUri, boolean swagger, boolean replace);
    }
}
