package com.devswing.sqlparser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Hashtable;
import java.util.TreeMap;

public class ColumnComment {
    private static final Logger LOGGER = LogManager.getLogger(ColumnComment.class);
    private String localName = null;
    private String descpiption = null;

    private TreeMap<String, String> enums = new TreeMap<>();

    private String enumRefTable = null;
    private String enumRefColumn = null;

    boolean parse(String commentStr) {
        String[] parts = commentStr.split(":");
        // trim each part
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }

        if (parts.length > 0)
            if (parts[0].length() > 0)
                this.localName = parts[0];
        if (parts.length > 1) {
            if (parts[1].length() > 0)
                this.descpiption = parts[1];
        }

        if (parts.length < 3) {
            return true;
        }

        // judge if the third part start with enumTable or enumVal and remove the enumTable or enumVal
        boolean isEnumValue;
        String enumStr;
        if (parts[2].startsWith("enumTable ")) {
            isEnumValue = false;
            enumStr = parts[2].substring("enumTable ".length());
        }
        else if (parts[2].startsWith("enumVal ")) {
            isEnumValue = true;
            enumStr = parts[2].substring("enumVal ".length());
        }
        else {
            LOGGER.error("Invalid enum part: " + parts[2]);
            return false;
        }

        String[] enumParts = enumStr.split(",");


        if (isEnumValue) {
            for (String enumPart : enumParts) {
                String[] enumKV = enumPart.split("-");
                // trim each part
                for (int i = 0; i < enumKV.length; i++) {
                    enumKV[i] = enumKV[i].trim();
                }
                if (enumKV.length == 2) {
                    this.enums.put(enumKV[0], enumKV[1]);
                } else {
                    LOGGER.warn("Invalid enum part: " + enumPart);
                    return false;
                }
            }
        }
        else {
            // enumTable tablename(columnname), get the tablename and columnname
            String[] enumTableParts = enumStr.split("\\(");
            // trim each part
            for (int i = 0; i < enumTableParts.length; i++) {
                enumTableParts[i] = enumTableParts[i].trim();
            }
            if (enumTableParts.length != 2) {
                LOGGER.error("Invalid enumTable part: " + enumStr);
                return false;
            }
            if (!enumTableParts[1].endsWith(")")) {
                LOGGER.error("Invalid enumTable part: " + enumStr);
                return false;
            }
            enumRefTable =  enumTableParts[0];
            enumRefColumn = enumTableParts[1].substring(0, enumTableParts[1].length() - 1);
        }
        return  true;
    }


    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getLocalName() {
        return localName;
    }

    public void setDescription(String descpiption) {
        this.descpiption = descpiption;
    }

    public String getDescription() {
        return descpiption;
    }

    public void setEnum(String name, String value) {
        enums.put(name, value);
    }

    public String getEnum(String name) {
        return enums.get(name);
    }

    public TreeMap<String, String> getEnums() {
        return enums;
    }

    public String getEnumRefTable() {
        return enumRefTable;
    }

    public String getEnumRefColumn() {
        return enumRefColumn;
    }
}
