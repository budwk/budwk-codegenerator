package com.budwk.code;

import com.budwk.code.ui.CodeGeneratorUIDialog;
import com.budwk.code.utils.Strings;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Locale;

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
        PsiJavaFile psiJavaFile = ((PsiJavaFile) file);
        String basePackage = psiJavaFile.getPackageName().replace(".modules", "").replace(".models", "");
        String fileName = file.getName();
        CodeGeneratorUIDialog dialog = new CodeGeneratorUIDialog(fileName, basePackage, new CodeGeneratorUIDialog.DialogCallBack() {
            @Override
            public void ok(String author, String projectName, String basePackage, String commonPath, String serverPath,
                           boolean openapi, boolean replace) {
                generator(fileName, projectName, author, basePackage, commonPath, serverPath,
                        openapi, replace);
            }
        });
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public void generator(String fileName, String projectName, String author, String basePackage, String commonPath, String serverPath,
                           boolean openapi, boolean replace) {
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        // cms
        String modelName = basePackage.substring(basePackage.lastIndexOf(".") + 1);
        // article
        String subName = fileName.toLowerCase(Locale.ROOT);
        if (subName.contains("_")) {
            subName = subName.substring(subName.indexOf("_") + 1).replaceAll("_", "");
        }
        // cms.article
        String permission = modelName + "." + subName;
        // /platform/cms/article
        String urlPath =  "/" + subName;
        // com.budwk.app
        String rootPackage = basePackage.substring(0, basePackage.indexOf("."));
        // com.budwk
        rootPackage = rootPackage.substring(0, rootPackage.indexOf("."));
        // CmsArticle
        String humpName = Strings.line2Hump(fileName);
        // cmsArticle
        String varName = Strings.lowerFirst(humpName);

        String content;
        try {
            // service 接口类生成
            content = readTemplateFile("service.txt");
            content = dealTemplateContent(content, fileName, author, urlPath, modelName, basePackage, rootPackage, humpName, varName, permission);
            writeToFile(content, getPath(projectName,serverPath, basePackage + ".services"), humpName + "Service.java", replace);
            // service 实现类生成
            content = readTemplateFile("service.impl.txt");
            content = dealTemplateContent(content, fileName, author, urlPath, modelName, basePackage, rootPackage, humpName, varName, permission);
            writeToFile(content, getPath(projectName,serverPath, basePackage + ".services.impl"), humpName + "ServiceImpl.java", replace);
            // controller 控制类生成
            if (openapi) {
                content = readTemplateFile("controller.openapi.txt");
            } else {
                content = readTemplateFile("controller.txt");
            }
            content = dealTemplateContent(content, fileName, author, urlPath, modelName, basePackage, rootPackage, humpName, varName, permission);
            writeToFile(content, getPath(projectName,serverPath, basePackage + ".controllers.admin"), humpName + "Controller.java", replace);
        } catch (Exception e) {
            Messages.showErrorDialog(project, e.getMessage(), "Error");
            return;
        }
        Messages.showInfoMessage(project, "Success!!\n" +
                "Waiting for IDEA file system to refresh", "Success");
    }

    private void writeToFile(String content, String classPath, String className, boolean replace) throws IOException {
        File floder = new File(classPath);
        if (!floder.exists()) {
            floder.mkdirs();
        }
        File file = new File(classPath + "/" + className);
        if (replace && file.exists()) {
            file.delete();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile(), Charset.forName("UTF-8"));
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content);
        bw.close();

    }

    private String dealTemplateContent(String content, String fileName, String author, String urlPath, String modelName, String basePackage,
                                       String rootPackage, String humpName, String varName, String permission) {
        content = content.replace("${fileName}", fileName);
        content = content.replace("${author}", getAuthor(author));
        content = content.replace("${urlPath}", urlPath);
        content = content.replace("${modelName}", modelName);
        content = content.replace("${basePackage}", basePackage);
        content = content.replace("${rootPackage}", rootPackage);
        content = content.replace("${humpName}", humpName);
        content = content.replace("${varName}", varName);
        content = content.replace("${permission}", permission);
        return content;
    }

    private String readTemplateFile(String fileName) throws IOException {
        InputStream in = null;
        Class a = this.getClass();
        in = this.getClass().getResourceAsStream("./template/" + fileName);
        String content = "";
        content = new String(readStream(in), "UTF-8");
        return content;
    }

    private byte[] readStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } finally {
            outputStream.close();
            inputStream.close();
        }
        return outputStream.toByteArray();
    }

    private String getPath(String projectName,String modelPath, String basePackage) {
        String packagePath = basePackage.replace(".", "/");
        String rootPath = basePath;
        if(!basePath.equals(projectName)){
            rootPath= basePath+"/"+projectName;
        }
        String appPath = rootPath + "/" + modelPath + "/src/main/java/" + packagePath + "/";
        return appPath;
    }

    private String getAuthor(String author) {
        if (author != null && author.length() > 0) {
            return "\n/**\n" +
                    " * @author " + author + "\n" +
                    " */";
        }
        return "";
    }

}
