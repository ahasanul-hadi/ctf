package com.cirt.ctf.enums;

public enum MarkingType {
    AUTO("auto"),
    MANUAL("manual");
    private final String type;
    MarkingType(String _type){
        this.type= _type;
    }
    public String getType(){
        return this.type;
    }
}
