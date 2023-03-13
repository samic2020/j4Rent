/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcoes;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComException;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.DispatchEvents;
import com.jacob.com.InvocationProxy;
import com.jacob.com.LibraryLoader;
import com.jacob.com.Variant;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Samic
 */
public class Outlook {
    static ArrayList<String> accounts = null;
    static boolean send = false;
    
    public Outlook() {}
    public Outlook(boolean readJacob) { if (readJacob) LoadJacobDll(); }
    
    public static ArrayList<String> getAccounts() {
        LoadOutlookAccounts();
        return accounts;
    }

    public static boolean isSend() {
        return send;
    }
    
    private static String pad(int i) {
        StringBuffer sb = new StringBuffer();

        while (sb.length() < i) {
            sb.append(' ');
        }

        return sb.toString();
    }

    private static void recurseFolders(int iIndent, Dispatch o) {
        if (o == null) { return; }

        Dispatch oFolders = null;
        try { oFolders = Dispatch.get(o, "Folders").toDispatch(); } catch (Exception e) {}

        if (oFolders == null) { return; }

        Dispatch oFolder = Dispatch.get(oFolders, "GetFirst").toDispatch();
        do {
            Object oFolderName = null;
            try {
                oFolderName = Dispatch.get(oFolder, "Name");
            } catch (Exception e) {}
            if (null == oFolderName) {
                    break;
            }

            if (ValidaEmail(oFolderName.toString())) {
                accounts.add(oFolderName.toString());
                // Para debugar - System.out.println(pad(iIndent) + oFolderName);
            }			

            try {
                oFolder = Dispatch.get(oFolders, "GetNext").toDispatch();
            } catch (Exception e) {}
        } while (true);
    }
    
    static public void LoadOutlookAccounts() {
        ActiveXComponent axOutlook = null;
        try {            
            axOutlook = new ActiveXComponent("Outlook.Application");
            Dispatch oNameSpace = axOutlook.getProperty("Session").toDispatch();

            recurseFolders(0, oNameSpace);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {                    
            axOutlook.invoke("Quit", new Variant[] {});
        }        
    }
    
    private void LoadJacobDll() {
        String libFile = System.getProperty("user.dir") + "/" +
                         (System.getProperty("os.arch").equals("amd64") ? "jacob-1.20-x64.dll" :"jacob-1.20-x86.dll");
        try{
            /**
             * Reading jacob.dll file
             */
            InputStream inputStream = new FileInputStream(libFile);
            /**
             *  Step 1: Create temporary file under <%user.home%>\AppData\Local\Temp\jacob.dll 
             *  Step 2: Write contents of `inputStream` to that temporary file.
             */
            File temporaryDll = File.createTempFile("jacob", ".dll");
            FileOutputStream outputStream = new FileOutputStream(temporaryDll);
            byte[] array = new byte[8192];
            for (int i = inputStream.read(array); i != -1; i = inputStream.read(array)){
                outputStream.write(array, 0, i);
            }
            outputStream.close();
            /* Temporary file will be removed after terminating-closing-ending the application-program */
            System.setProperty(LibraryLoader.JACOB_DLL_PATH, temporaryDll.getAbsolutePath());
            LibraryLoader.loadJacobLibrary();
        }catch(IOException e){
            e.printStackTrace();
        }                    
    }
    
    static private boolean ValidaEmail(String email) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }    
    
    public void Send(String To, String Cc, String Subject, String Body, String[] Attachments) {
        ActiveXComponent oOutlook = new ActiveXComponent("Outlook.Application");
        try { 
            DispatchEvents events = new DispatchEvents( oOutlook,
                    new InvocationProxy() {
                        public Variant invoke(String name, Variant[] args) {
                            if (name.equalsIgnoreCase("ItemSend")) {
                                boolean item = Boolean.valueOf(args[1].toString());
                                send = !item;
                                // Para debugar - System.out.println("Problem in Send Email: " + item);
                            }
                            return null;
                        }
                    }
            );
        } catch (ComException cex) { 
            System.err.println("HR: " + Integer.toHexString(cex.getHResult())); 
        }
        
        try {
            Dispatch mail = Dispatch.invoke( oOutlook.getObject(), "CreateItem", Dispatch.Get, new Object[] { "0" }, new int[0]).toDispatch();

            Dispatch.put(mail, "To", MultEmail(To) );
            if (Cc != null) Dispatch.put(mail, "Cc", MultEmail(Cc) );
            Dispatch.put(mail, "Subject", delHTML(Subject) );
            Dispatch.put(mail, "Body", delHTML(Body) );
            Dispatch.put(mail, "ReadReceiptRequested", "true" );

            if(Attachments.length > 0) {
                Dispatch attachs = Dispatch.get(mail, "Attachments").toDispatch();
                for(Object attachment : Attachments) {
                    Dispatch.call(attachs, "Add", attachment);
                }
            }
            
            Dispatch.call(mail,"Send");
            oOutlook.safeRelease();
            ComThread.Release();
        } catch (Exception e) {
            send = false;
            e.printStackTrace();
        } finally {
            try { oOutlook.invoke("Quit", new Variant[] {}); } catch (Exception e) {}
	}              
    }
    
    private String MultEmail(String value) {
        String retorno = value.replace(",", ";");
        return retorno;
    }    
    
    private String delHTML(String value) {        
        return delTags(HtmlSanitizer.sanitize(value));
    }      
    
    private String delTags(String value) {
        return value.replaceAll("o&#[0-9][0-9][0-9];", "");
    }
}
