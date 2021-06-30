package com.github.mikesafonov.jenkins.telegram.chatops.bot;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mike Safonov
 */
@Service
public class UserStateService {
    private final Map<Long, UserStateValues> stateMap = new HashMap<>();

    public UserState getState(Long user) {
        var current = stateMap.get(user);
        if (current == null) {
            return UserState.WAIT_COMMAND;
        }
        return current.state;
    }

    public String getJobName(Long user) {
        var current = stateMap.get(user);
        if (current == null) {
            return null;
        }
        return current.jobName;
    }

    public void update(Long user, UserState state) {
        stateMap.put(user, new UserStateValues(state));
    }

    public void update(Long user, UserState state, String jobName) {
        stateMap.put(user, new UserStateValues(state, jobName));
    }

    @Data
    private static class UserStateValues {
        private UserState state;
        private String jobName;

        public UserStateValues() {
            this(UserState.WAIT_COMMAND);
        }

        public UserStateValues(UserState state) {
            this.state = state;
        }

        public UserStateValues(UserState state, String jobName) {
            this.state = state;
            this.jobName = jobName;
        }
    }
}
