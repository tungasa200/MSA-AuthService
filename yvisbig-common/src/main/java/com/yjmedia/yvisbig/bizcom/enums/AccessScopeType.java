package com.yjmedia.yvisbig.bizcom.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public enum AccessScopeType {
    SYSTEM("system", "admin"),
    PRIVATE("private", "authcheck"),
    PUBLIC("public", "openall");

    private final String level;
    private final String levelDescription;

    AccessScopeType(String level, String levelDescription){
        this.level	= level;
        this.levelDescription = levelDescription;

    }

    public String getLevel() {return this.level;}
    public String getLevelDescription() {return this.levelDescription;}

    public static AccessScopeType from(String set) {
        for(AccessScopeType each : AccessScopeType.values()) {
            if(each.getLevel().equals(set)) {
                return each;
            }
        }
        return AccessScopeType.PUBLIC;
    }

    public HashMap<String, Object> getMap(){
        HashMap<String, Object> map = new HashMap<>();

        map.put("levelDescription", this.getLevelDescription());
        map.put("level", this);
        return map;
    }

    public static List<HashMap<String, Object>> getList(){
        return Arrays.stream(AccessScopeType.values()).map(t -> t.getMap()).collect(Collectors.toList());
    }

}
