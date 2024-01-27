package org.acme.boundary;

import jakarta.enterprise.context.Dependent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import picocli.CommandLine;

@Dependent
public class VersionProvider implements CommandLine.IVersionProvider {

    @ConfigProperty(name="quarkus.application.version")
    String version;

    @Override
    public String[] getVersion() {
        return new String[]{version};
    }
}
