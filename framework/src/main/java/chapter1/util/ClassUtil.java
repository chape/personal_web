package chapter1.util;

import chapter1.helper.ConfigHelper;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by ChaoChao on 06/12/2017.
 */
public class ClassUtil {

    public static ClassLoader getClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }

    public static Class<?> loadClass(String className, boolean isInit){
        Class<?> cls;
        try{
            cls = Class.forName(className, isInit, getClassLoader());
        }catch (ClassNotFoundException cnfe){
            throw new RuntimeException(cnfe);
        }
        return cls;
    }

    public static Class<?> loadClass(String className){
        return loadClass(className, true);
    }

    public static void main(String[] args) {
        getClassSet(ConfigHelper.getAppBasePackage());
    }
    public static Set<Class<?>> getClassSet(String pakageName){
        Set<Class<?>> classSet = new HashSet<>();
        try{
            Enumeration<URL> urls = getClassLoader().getResources(pakageName.replace(".", "/"));
            while (urls.hasMoreElements()){
                URL url = urls.nextElement();
                if(null == url){
                    continue;
                }
                String protocol = url.getProtocol();
                if("file".equals(protocol)){
                    String packagePath = url.getPath().replaceAll("%20", " ");
                    addClass(classSet,packagePath,pakageName);
                }else if("jar".equals(protocol)){
                    JarURLConnection jarURLConnection = (JarURLConnection)url.openConnection();
                    if(null == jarURLConnection){
                        continue;
                    }
                    JarFile jarFile = jarURLConnection.getJarFile();
                    if(null == jarFile){
                        continue;
                    }
                    Enumeration<JarEntry> jarEntries = jarFile.entries();
                    while(jarEntries.hasMoreElements()){
                        JarEntry jarEntry = jarEntries.nextElement();
                        String jarEntryName = jarEntry.getName();
                        if(!jarEntryName.endsWith(".class")){
                            continue;
                        }
                        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf("."))
                                                        .replaceAll(".", "/");
                        doAddClass(classSet,className);
                    }
                }
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return classSet;
    }

    private static void addClass(Set<Class<?>> classSet, String packagePath, String packageName){
        File[] files = new File(packagePath)
                .listFiles(f -> (f.isFile() && f.getName().endsWith(".class")) || f.isDirectory());

        Arrays.stream(files).forEach(f -> {
            String fileName = f.getName();
            if(f.isFile()){
                String className = fileName.substring(0,fileName.lastIndexOf("."));
                if(StringUtil.isNotEmpty(packageName)){
                    className = packageName + "." + className;
                }
                doAddClass(classSet,className);
            }else{
                String subPackagePath = fileName;
                if(StringUtil.isNotEmpty(packagePath)){
                    subPackagePath = packagePath + "/" + subPackagePath;
                }
                String subPackageName = fileName;
                if(StringUtil.isNotEmpty(packageName)){
                    subPackageName = packageName + "." + subPackageName;
                }
                addClass(classSet, subPackagePath, subPackageName);
            }
        });
    }

    private static void doAddClass(Set<Class<?>> classSet, String className){
        Class<?> cls = loadClass(className, false);
        classSet.add(cls);
    }
}
