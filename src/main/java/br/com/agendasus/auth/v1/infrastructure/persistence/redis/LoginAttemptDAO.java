package br.com.agendasus.auth.v1.infrastructure.persistence.redis;

import org.springframework.stereotype.Repository;

@Repository
public class LoginAttemptDAO extends RedisDAO {

    public static final String KEY_BASE = "LoginAttempt";
    public static final int EXPIRE_TIME_IN_SECONDS = 300;

    private String getKey(String user) {
        return new StringBuilder(KEY_BASE).append(":").append(user).toString();
    }

    public Integer getAttempt(String user) {
        String object = get(getKey(user));
        return (object != null) ? Integer.parseInt(object) : null;
    }

    @Override
    public void increment(String user) {
        String key = getKey(user);
        createIfNotExists(key);
        super.increment(key);
        expire(key, EXPIRE_TIME_IN_SECONDS);
    }

    private void createIfNotExists(String key) {
        Integer attempt = getAttempt(key);
        if(attempt == null) {
            set(key, "0");
        }
    }

    @Override
    public void del(String user) {
        super.del(getKey(user));
    }

}
