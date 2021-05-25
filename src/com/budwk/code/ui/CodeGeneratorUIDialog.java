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
    private JTextField tf_commonPath;
    private JTextField tf_serverPath;
    private JCheckBox oepnapiCheckBox;
    private JCheckBox replaceCheckBox;
    private JTextField tf_projectName;
    private DialogCallBack dialogCallBack;
    private String projectName = "wk";
    private String modelName = "";

    public CodeGeneratorUIDialog(String fileName, String basePackage, DialogCallBack dialogCallBack) {
        this.dialogCallBack = dialogCallBack;
        this.tf_basePackage.setText(basePackage);
        modelName = basePackage.substring(basePackage.lastIndexOf(".") + 1);
        this.projectName = "wk-" + modelName;
        this.tf_projectName.setText(projectName);
        this.setText();
        this.setViews(fileName);
    }

    private void setText() {
        this.tf_commonPath.setText(projectName + "-common");
        this.tf_serverPath.setText(projectName + "-server");
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
        if ("".equals(tf_commonPath.getText().trim())) {
            Messages.showErrorDialog("Common Path cannot be empty", "Error");
            return;
        }
        if ("".equals(tf_serverPath.getText().trim())) {
            Messages.showErrorDialog("Server Path cannot be empty", "Error");
            return;
        }
        dialogCallBack.ok(tf_author.getText().trim(), tf_projectName.getText().trim(), tf_basePackage.getText().trim(),
                tf_commonPath.getText().trim(),
                tf_serverPath.getText().trim(), oepnapiCheckBox.isSelected(), replaceCheckBox.isSelected());
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public interface DialogCallBack {
        void ok(String author, String projectName, String basePackage, String commonPath, String serverPath,
                boolean openapi, boolean replace);
    }
}
