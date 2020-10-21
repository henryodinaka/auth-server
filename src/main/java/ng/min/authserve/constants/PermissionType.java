package ng.min.authserve.constants;


import ng.min.authserve.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Unogwu Daniel on 21/07/2020.
 */
public enum PermissionType {
    CREATE_USER("CRU"),
    RESET_APIKEY("REA"),
    VIEW_USER("VIU"),
    UPDATE_USER("CRU"),
    EDIT_USER("EDU"),
    ASSIGN_ROLE("ASR"),
    ASSIGN_PERMISSION("ASP"),
    CREATE_PERMISSION("CRP"),
    EDIT_PERMISSION("EDP"),
    CREATE_ROLE("CRR"),
    EDIT_ROLE("EDR"),
    VIEW_ROLES("VIR"),
    ACTIVATE_ROLE("ACR"),
    ACTIVATE_PERMISSION("ACP"),
    VIEW_PERMISSION("VIP"),
    ACTIVATE_USER("ACU");


    String code;

    PermissionType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static PermissionType findByCode(String input) {
        for (PermissionType permissionType : values()) {
            if (permissionType.getCode().equalsIgnoreCase(input))
                return permissionType;
        }
        return null;
    }
    public static Map<String, String> getAllFront() {
        Map<String, String> types = new HashMap<>();

        for (PermissionType transactionType : values()) {
                types.put(transactionType.getCode(), transactionType.name().replace("_", " "));
        }
        return types;
    }
    public static HashMap<String, String> getAllBack() {
        HashMap<String, String> types = new HashMap<>();

        for (PermissionType transactionType : values()) {
            var replace = transactionType.name().replace("_", " ");
            types.put(transactionType.name(), CommonUtils.convertToTitleCase(replace));
        }
        return types;
    }
    public static List<String> getAllList() {
        List<String> types = new ArrayList<>();

        for (PermissionType transactionType : values()) {
                types.add(transactionType.name());
        }
        return types;
    }
}