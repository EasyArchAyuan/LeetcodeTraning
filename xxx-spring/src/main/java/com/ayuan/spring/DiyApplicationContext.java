package com.ayuan.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ayuan
 * @Description: diy容器
 * @date 2023/5/8 15:02
 */
public class DiyApplicationContext {

    private Class configClass;

    //beanDefinitionMap->key:beanName,value:BeanDefinition
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    //单例池->key:beanName,value:Obj
    private Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    public DiyApplicationContext(Class configClass) {
        this.configClass = configClass;
        //判断类上有没有要扫描的路径
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            //1.获取扫描包名（com.ayuan.service）
            String path = componentScanAnnotation.value();
            //2.转换为相对路径名（com/ayuan/service）
            path = path.replace(".", "/");
            ClassLoader classLoader = DiyApplicationContext.class.getClassLoader();
            //3.通过classLoader转换为绝对路径
            URL resource = classLoader.getResource(path);
            File file = new File(resource.getFile());
            //4.过滤所有后缀为.class的文件
            if (file.isDirectory()) {
                for (File f : Objects.requireNonNull(file.listFiles())) {
                    String fileName = f.getAbsolutePath();
                    //5.判断类是不是一个bean（是否包含component注解）
                    if (fileName.endsWith(".class")) {
                        // 路径转换为包路径（com.ayuan.service）
                        String className = fileName.substring(fileName.indexOf("com"), fileName.lastIndexOf(".class"));
                        className = className.replace("/", ".");
                        try {
                            //Class<?> clazz = Class.forName(className);
                            Class<?> clazz = classLoader.loadClass(className);
                            if (clazz.isAnnotationPresent(Component.class)) {
                                //6.获取beanName
                                Component componentAnnotation = clazz.getAnnotation(Component.class);
                                String beanName = componentAnnotation.value();
                                if (beanName == null || "".equals(beanName)) {
                                    // 首字母小写驼峰
                                    beanName = Introspector.decapitalize(clazz.getSimpleName());
                                }
                                //7.实例化beanDefinition
                                BeanDefinition beanDefinition = new BeanDefinition();
                                beanDefinition.setType(clazz);
                                //8.判断作用域单例还是多例
                                if (clazz.isAnnotationPresent(Scope.class)) {
                                    Scope scopeAnnotation = clazz.getAnnotation(Scope.class);
                                    beanDefinition.setScope(scopeAnnotation.value());
                                } else {
                                    beanDefinition.setScope("singleton");
                                }
                                //9.放入beanDefinitionMap里面
                                beanDefinitionMap.put(beanName, beanDefinition);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        //10.初始化所有单例Bean
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if ("singleton".equals(beanDefinition.getScope())) {
                Object obj = this.createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, obj);
            }
        }
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        //1.找到当前要创建类的类型
        Class clazz = beanDefinition.getType();
        try {
            //2.根据类的构造方法反射拿到obj
            Object instance = clazz.getConstructor().newInstance();
            //3.依赖注入
            for (Field f : clazz.getDeclaredFields()) {
                if (f.isAnnotationPresent(Autowired.class)) {
                    // 去除私有权限
                    f.setAccessible(true);
                    f.set(instance, this.getBean(f.getName()));
                }
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new NullPointerException("Bean is null");
        }
        String scope = beanDefinition.getScope();
        if ("singleton".equals(scope)) {
            //单例,启动时全部生成
            Object bean = singletonObjects.get(beanName);
            if (bean == null) {
                Object newBean = this.createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, newBean);
            }
            return bean;
        }
        //多例
        return createBean(beanName, beanDefinition);
    }

}