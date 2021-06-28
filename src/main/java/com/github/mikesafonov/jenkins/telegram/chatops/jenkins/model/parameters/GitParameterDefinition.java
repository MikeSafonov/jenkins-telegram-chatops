package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.parameters;

/**
 * @author Mike Safonov
 */
public class GitParameterDefinition extends ParameterDefinition {
    static final String CLASS = "net.uaznia.lukanus.hudson.plugins.gitparameter.GitParameterDefinition";

    private GitParameterValue defaultParameterValue;

    @Override
    public GitParameterValue getDefaultParameterValue() {
        return defaultParameterValue;
    }

    @Override
    public String toString() {
        return getName() + "(" + getDescription() + ")" + "(git)";
    }
}
