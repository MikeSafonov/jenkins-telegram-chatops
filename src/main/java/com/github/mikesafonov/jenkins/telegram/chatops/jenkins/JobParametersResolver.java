package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.RunJobJenkinsApiException;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.JobWithDetailsWithProperties;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mike Safonov
 */
@Service
public class JobParametersResolver {

    public Map<String, String> resolve(JobWithDetailsWithProperties job, Map<String, String> inputParameters) {
        var parametersDefinitionProperty = job.getParametersDefinitionProperty();
        if (parametersDefinitionProperty.isPresent()) {
            var parameters = parametersDefinitionProperty.get();
            var params = new HashMap<String, String>();
            for (var definition : parameters.getParameterDefinitions()) {
                if (inputParameters.containsKey(definition.getName())) {
                    params.put(definition.getName(), inputParameters.get(definition.getName()));
                } else {
                    var defaultValue = definition.getDefaultParameterValue();
                    if (defaultValue != null) {
                        params.put(defaultValue.getName(), defaultValue.getValue().toString());
                    } else {
                        throw new RunJobJenkinsApiException("Unable to run job " + job.getName()
                            + ": no default value for parameter " + definition.getName());
                    }
                }
            }
            return params;
        } else {
            return Collections.emptyMap();
        }
    }

}
