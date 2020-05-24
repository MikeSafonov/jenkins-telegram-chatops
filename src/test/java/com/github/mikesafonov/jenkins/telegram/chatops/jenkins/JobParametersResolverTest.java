package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.RunJobJenkinsApiException;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.JobWithDetailsWithProperties;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.ParametersDefinitionProperty;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.parameters.BooleanParameterDefinition;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.parameters.BooleanParameterValue;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.parameters.ParameterDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
class JobParametersResolverTest {

    private JobParametersResolver resolver;
    private JobWithDetailsWithProperties job;

    @BeforeEach
    void setUp() {
        resolver = new JobParametersResolver();

        job = mock(JobWithDetailsWithProperties.class);
    }

    @Nested
    class WhenNoJobParameters {

        @Test
        void shouldReturnEmptyMap() {
            when(job.getParametersDefinitionProperty()).thenReturn(Optional.empty());

            assertThat(resolver.resolve(job, Collections.emptyMap())).isEmpty();
        }
    }

    @Nested
    class WhenJobParametersExist {

        @BeforeEach
        void setUp() {

        }

        @Nested
        class WhenInputParametersExists {

            private Map<String, String> inputParameters;

            @BeforeEach
            void serUp() {
                inputParameters = Map.of("Param", "true");
            }

            @Test
            void shouldReturnFromInputParameters() {
                ParametersDefinitionProperty parametersDefinitionProperty = mock(ParametersDefinitionProperty.class);
                BooleanParameterValue booleanParameterValue = new BooleanParameterValue("", "Param", false);
                BooleanParameterDefinition parameterDefinition = new BooleanParameterDefinition(booleanParameterValue);
                List<ParameterDefinition> parameterDefinitions = List.of(
                    parameterDefinition
                );
                parameterDefinition.setName("Param");

                when(job.getParametersDefinitionProperty()).thenReturn(Optional.of(parametersDefinitionProperty));
                when(parametersDefinitionProperty.getParameterDefinitions()).thenReturn(parameterDefinitions);

                Map<String, String> actual = resolver.resolve(job, inputParameters);
                assertThat(actual).containsOnlyKeys("Param");
                assertThat(actual).containsValue("true");
            }
        }

        @Nested
        class WhenNoInputParameters {

            private Map<String, String> inputParameters = Collections.emptyMap();

            @Nested
            class WhenDefaultValueExist {

                @Test
                void shouldReturnExpectedMap() {
                    ParametersDefinitionProperty parametersDefinitionProperty = mock(ParametersDefinitionProperty.class);
                    BooleanParameterValue booleanParameterValue = new BooleanParameterValue("", "Param", true);
                    BooleanParameterDefinition parameterDefinition = new BooleanParameterDefinition(booleanParameterValue);
                    List<ParameterDefinition> parameterDefinitions = List.of(
                        parameterDefinition
                    );

                    when(job.getParametersDefinitionProperty()).thenReturn(Optional.of(parametersDefinitionProperty));
                    when(parametersDefinitionProperty.getParameterDefinitions()).thenReturn(parameterDefinitions);

                    Map<String, String> actual = resolver.resolve(job, inputParameters);
                    assertThat(actual).containsOnlyKeys("Param");
                    assertThat(actual).containsValue("true");
                }
            }

            @Nested
            class WhenNoDefaultValue {

                @Test
                void shouldThrowRunJobJenkinsApiException() {
                    ParametersDefinitionProperty parametersDefinitionProperty = mock(ParametersDefinitionProperty.class);
                    List<ParameterDefinition> parameterDefinitions = List.of(
                        new BooleanParameterDefinition()
                    );

                    when(job.getParametersDefinitionProperty()).thenReturn(Optional.of(parametersDefinitionProperty));
                    when(parametersDefinitionProperty.getParameterDefinitions()).thenReturn(parameterDefinitions);

                    assertThrows(RunJobJenkinsApiException.class, () -> resolver.resolve(job, inputParameters));
                }
            }
        }
    }
}
