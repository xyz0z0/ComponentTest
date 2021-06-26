package xyz.xyz0z0.arouter_annotations.bean;


import javax.lang.model.element.Element;

public class RouterBean {

    private TypeEnum typeEnum;
    private Element element;
    private Class<?> myClass;
    private String path;
    private String group;

    public RouterBean(TypeEnum typeEnum, Class<?> myClass, String path, String group) {
        this.typeEnum = typeEnum;
//        this.element = element;
        this.myClass = myClass;
        this.path = path;
        this.group = group;
    }

    private RouterBean(Builder builder) {
        this.typeEnum = builder.type;
        this.element = builder.element;
        this.myClass = builder.clazz;
        this.path = builder.path;
        this.group = builder.group;
    }

    public static RouterBean create(TypeEnum type, Class<?> clazz, String path, String group) {
        return new RouterBean(type, clazz, path, group);
    }

    public TypeEnum getTypeEnum() {
        return typeEnum;
    }

    public void setTypeEnum(TypeEnum typeEnum) {
        this.typeEnum = typeEnum;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Class<?> getMyClass() {
        return myClass;
    }

    public void setMyClass(Class<?> myClass) {
        this.myClass = myClass;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public enum TypeEnum {
        ACTIVITY
    }

    public static class Builder {

        private TypeEnum type;
        private Element element;
        private Class<?> clazz;
        private String path;
        private String group;

        public Builder addType(TypeEnum type) {
            this.type = type;
            return this;
        }

        public Builder addElement(Element element) {
            this.element = element;
            return this;
        }

        public Builder addClazz(Class<?> clazz) {
            this.clazz = clazz;
            return this;
        }

        public Builder addPath(String path) {
            this.path = path;
            return this;
        }

        public Builder addGroup(String group) {
            this.group = group;
            return this;
        }

        public RouterBean build() {
            if (path == null || path.length() == 0) {
                throw new IllegalArgumentException("path 为空");
            }
            return new RouterBean(this);
        }

    }
}
