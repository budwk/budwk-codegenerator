package com.budwk.code;

import com.budwk.code.ui.CodeGeneratorUIDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;

public class CodeGeneratorAction extends AnAction {

    private Project project;
    private String basePath;

    @Override
    public void actionPerformed(AnActionEvent e) {
        project = e.getData(PlatformDataKeys.PROJECT);
        basePath = project.getBasePath();
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        if (null == file) {
            Messages.showErrorDialog(project, "Please right-click on the POJO class", "Error");
            return;
        }
        String fileName = file.getName();
        CodeGeneratorUIDialog dialog = new CodeGeneratorUIDialog(fileName, new CodeGeneratorUIDialog.DialogCallBack() {
            @Override
            public void ok(String author, String basePackage, String modelPath, String servicePath,
                           String webPath, String baseUri, boolean swagger, boolean replace) {
                generator(fileName, author, basePackage, modelPath, servicePath,
                        webPath, baseUri, swagger, replace);
            }
        });
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public void generator(String fileName, String author, String basePackage, String modelPath, String servicePath,
                          String webPath, String baseUri, boolean swagger, boolean replace) {

    }

}
