package com.betflow.graphql;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import graphql.schema.idl.RuntimeWiring;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

/**
 * Configurazione GraphQL per scalar types personalizzati
 */
@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return builder -> builder
                .scalar(uuidScalar())
                .scalar(ExtendedScalars.GraphQLBigDecimal)
                .scalar(ExtendedScalars.DateTime)
                .scalar(ExtendedScalars.Date);
    }

    private GraphQLScalarType uuidScalar() {
        return GraphQLScalarType.newScalar()
                .name("UUID")
                .description("UUID scalar type")
                .coercing(new graphql.schema.Coercing<java.util.UUID, String>() {
                    @Override
                    public String serialize(Object dataFetcherResult) {
                        if (dataFetcherResult instanceof java.util.UUID) {
                            return dataFetcherResult.toString();
                        }
                        throw new graphql.schema.CoercingSerializeException("Expected a UUID");
                    }

                    @Override
                    public java.util.UUID parseValue(Object input) {
                        if (input instanceof String) {
                            return java.util.UUID.fromString((String) input);
                        }
                        throw new graphql.schema.CoercingParseValueException("Expected a String");
                    }

                    @Override
                    public java.util.UUID parseLiteral(Object input) {
                        if (input instanceof graphql.language.StringValue) {
                            return java.util.UUID.fromString(((graphql.language.StringValue) input).getValue());
                        }
                        throw new graphql.schema.CoercingParseLiteralException("Expected a StringValue");
                    }
                })
                .build();
    }
}
