package ru.itis.mySpring.context;

import ru.itis.mySpring.demoFiles.helpers.DbDataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

public class ApplicationContextImpl implements ApplicationContext {

    private HashMap<String, Object> components;
    private HashMap<String, Object> classes;
    private Properties properties;
    private static ApplicationContextImpl context;

    static {
        context = new ApplicationContextImpl();
    }

    public ApplicationContextImpl() {
        properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/application.properties"));
            HashMap<String, Object> packages;
            components = new HashMap<>();
            classes = new HashMap<>();
            components.put("DataSource", DbDataSource.getDataSource());
            packages = loadPackages(properties);
            classes = loadRawComponents(packages);
            System.out.println("\nClasses:  " + classes.keySet() + "\n" + classes.values());
            System.out.println();
            fillInComponents(classes);
            System.out.println();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillInComponents(HashMap<String, Object> classes) {

        while (!classes.isEmpty()) {
            Set<String> keys = classes.keySet();
            String className = keys.iterator().next();
//            System.out.println(classes.get(className));
            Object object = fillInClass(classes.get(className));
            components.put(className, object);
            classes.remove(className);
        }
    }

    private Object fillInClass(Object o) {
        Object component = null;
        try {
            Class c = (Class) o;
            Constructor constructor = c.getConstructors()[0];

            component = c.newInstance();
            Field[] fields = c.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                String fieldType = field.getType().getSimpleName();
                if (components.containsKey(fieldType)) {
                    field.set(component, components.get(fieldType));
                } else if (classes.containsKey(fieldType)) {
                    Object object = fillInClass(classes.get(fieldType));
                    components.put(fieldType, object);
                    classes.remove(fieldType);

                    field.set(component, object);
                }
                fields[i] = field;
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return component;
    }


    private HashMap<String, Object> loadPackages(Properties properties) {
        HashMap<String, Object> packages = new HashMap<String, Object>();
        Enumeration enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String propertyName = (String) enumeration.nextElement();
            packages.put(propertyName, properties.getProperty(propertyName));
        }
        return packages;
    }

    private HashMap<String, Object> loadRawComponents(HashMap<String, Object> packages) {
        HashMap<String, Object> classes = new HashMap<>();
        for (Object pack : packages.values()) {
            try {
                List<Class> packageClasses = getClassesForPackage(pack.toString());
                packageClasses.forEach(o -> classes.put(o.getInterfaces()[0].getSimpleName(), o));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return classes;
    }

    public static ApplicationContextImpl getContext() {
        return context;
    }

    private static List<Class> getClassesForPackage(String pckgname) throws ClassNotFoundException {
        // This will hold a list of directories matching the pckgname. There may be more than one if a package is split over multiple jars/paths
        ArrayList<File> directories = new ArrayList<File>();
        String packageToPath = pckgname.replace('.', '/');
        try {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }

            // Ask for all resources for the packageToPath
            Enumeration<URL> resources = cld.getResources(packageToPath);
            while (resources.hasMoreElements()) {
                directories.add(new File(URLDecoder.decode(resources.nextElement().getPath(), "UTF-8")));
            }
        } catch (NullPointerException x) {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Null pointer exception)");
        } catch (UnsupportedEncodingException encex) {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Unsupported encoding)");
        } catch (IOException ioex) {
            throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + pckgname);
        }

        ArrayList<Class> classes = new ArrayList<Class>();
        // For every directoryFile identified capture all the .class files
        while (!directories.isEmpty()) {
            File directoryFile = directories.remove(0);
            if (directoryFile.exists()) {
                // Get the list of the files contained in the package
                File[] files = directoryFile.listFiles();

                for (File file : files) {
                    // we are only interested in .class files
                    if ((file.getName().endsWith(".class")) && (!file.getName().contains("$"))) {
                        // removes the .class extension
                        int index = directoryFile.getPath().indexOf(packageToPath);
                        String packagePrefix = directoryFile.getPath().substring(index).replace('/', '.');
                        try {
                            String className = packagePrefix + '.' + file.getName().substring(0, file.getName().length() - 6);
                            Class c = Class.forName(className);
                            if (!c.isInterface()) {
                                classes.add(c);
                            }
                        } catch (NoClassDefFoundError e) {
                            // do nothing. this class hasn't been found by the loader, and we don't care.
                        }
                    } else if (file.isDirectory()) { // If we got to a subdirectory
                        directories.add(new File(file.getPath()));
                    }
                }
            } else {
                throw new ClassNotFoundException(pckgname + " (" + directoryFile.getPath() + ") does not appear to be a valid package");
            }
        }
        return classes;
    }


    public <T> T getComponent(Class<T> componentClass) {
        for (Object component : components.values()) {
            if (componentClass.isAssignableFrom(component.getClass())) {
                return (T) component;
            }
        }
        return null;
    }
}