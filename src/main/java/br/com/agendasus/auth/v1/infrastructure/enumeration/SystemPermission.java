package br.com.agendasus.auth.v1.infrastructure.enumeration;

import br.com.agendasus.auth.v1.domain.model.UserLogin;
import com.google.gson.Gson;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

@Getter
public enum SystemPermission {

    MANAGE_USER("enum.system.permission.gerenciar.usuario"),
    MANAGE_INTEGRATION("enum.system.permission.gerenciar.integracao"),
    USE_API("enum.system.permission.usar.api");

    private String label;


    SystemPermission(String label) {
        this.label = label;
    }


    public String getLabel() {
        return label;
    }


    public static Map<String, Boolean> getMapPermission(UserLogin loggedUser) {
        Map<String, Boolean> map;
        List<SystemPermission> permissions = new Gson().fromJson(loggedUser.getPermissions(), ArrayList.class);
        if(loggedUser.isAdmin()) {
            map = getMapFilled(true);
        } else if (permissions == null) {
            map = getMapFilled(false);
        } else {
            map = new HashMap<>();
            for(SystemPermission systemPermission : SystemPermission.values()) {
                if(permissions.contains(systemPermission.name())) {
                    map.put(systemPermission.name(), true);
                } else {
                    map.put(systemPermission.name(), false);
                }
            }
        }
        return map;
    }

    public static List<SimpleGrantedAuthority> getSimpleGrantedAuthorityPermissions(UserLogin user) {
        List<SystemPermission> permissions = new ArrayList<>();
        List roles = new ArrayList<SimpleGrantedAuthority>();
        roles.add(new SimpleGrantedAuthority(user.getUserType().name()));
        if(user.isAdmin()) {
            permissions = Arrays.asList(SystemPermission.values());
        } else if (permissions == null) {
            permissions = new ArrayList<>();
        } else {
            List<String> stringPermissions = new Gson().fromJson(user.getPermissions(), ArrayList.class);
            if(stringPermissions != null) {
                for (String p : stringPermissions) {
                    try {
                        permissions.add(SystemPermission.valueOf(p));
                    } catch (IllegalArgumentException e) {
                        System.out.println("O usuário " + user.getLogin() + " possui a permissão " + p + " que não existe no sistema");
                    }
                }
            }
        }
        for(SystemPermission permission : permissions) {
            roles.add(new SimpleGrantedAuthority(permission.name()));
        }
        return roles;
    }

    private static Map<String, Boolean> getMapFilled(Boolean value) {
        Map<String, Boolean> map = new HashMap<>();
        for(SystemPermission systemPermission : SystemPermission.values()) {
            map.put(systemPermission.name(), value);
        }
        map.put("MASTER", value);
        return map;
    }

}
