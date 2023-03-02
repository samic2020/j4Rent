package Funcoes;

import java.io.File;
import java.util.UUID;

public class tempFile {
    private String sPath = "";
    private String sFileName = "";
    private String sFileExt = "";
    private String sPathNameExt = "";
    private String pathSeparator = System.getProperty("os.name").toUpperCase().trim().equals("LINUX") ? "//" : "\\";
    private String separator = ".";

    public tempFile() {}

    public tempFile(String extensao) {
        String sb = UUID.randomUUID().toString();
        String temp = "temp" + pathSeparator;
        String subPasta = "$" + VariaveisGlobais.usuario.trim().toLowerCase();
        if (!new File(temp).exists()) {
            new File(temp).mkdirs();
        }
        if (!new File(temp + subPasta).exists()) {
            new File(temp + subPasta).mkdirs();
        }

        this.sPath = temp + pathSeparator + subPasta + pathSeparator;
        this.sFileName = sb.toString() + separator + extensao;
        this.sFileExt = separator + extensao;
        this.sPathNameExt = temp + pathSeparator + subPasta + pathSeparator + sb.toString() + separator + extensao;
    }

    public String getsPath() { return sPath; }
    public String getsFileName() { return sFileName; }
    public String getsFileExt() { return sFileExt; }
    public String getsPathNameExt() { return sPathNameExt; }

    public String getTempPath() {
        String temp = "temp" + separator;
        String subPasta = "$" + VariaveisGlobais.usuario.trim().toLowerCase();
        return temp + pathSeparator + subPasta + pathSeparator;
    }

    public String getTempFileName(String sFileName) {
        int pos = -1;
        pos = sFileName.lastIndexOf(pathSeparator);
        return pos > -1 ? sFileName.substring(pos + 1) : "";
    }

    public String getTempFileExt(String sFileName) {
        int pos = -1;
        pos = sFileName.lastIndexOf(separator);
        return pos > -1 ? sFileName.substring(pos) : "";
    }

}
