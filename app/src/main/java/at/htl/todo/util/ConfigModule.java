package at.htl.todo.util;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import io.smallrye.config.PropertiesConfigSource;
import io.smallrye.config.SmallRyeConfigBuilder;

@Module
@InstallIn(SingletonComponent.class)
public class ConfigModule {
    @Provides
    @Singleton
    public Config provideConfiguration() {
        //return new SmallRyeConfigBuilder().forClassLoader(classLoader).build(); <=== does not work

        var config = new SmallRyeConfigBuilder()
                .forClassLoader(getClass().getClassLoader())
                .addDefaultInterceptors()
                .setAddDefaultSources(false)
                .withSources(new FixMissingJavaNioJarFileProviderConfigSourceProvider())
                .build();
        return config;
    }
}
class FixMissingJavaNioJarFileProviderConfigSourceProvider implements ConfigSourceProvider {
    Iterable<ConfigSource> configSources;
    @Override
    public Iterable<ConfigSource> getConfigSources(ClassLoader forClassLoader) {
        if (configSources == null) {
            Function<URL, ConfigSource> createSource = url -> {
                try {
                    return new PropertiesConfigSource(url);
                } catch(IOException e) {
                    throw new CompletionException(e);
                }
            };
            configSources = Stream.of("application.properties", "META-INF/microprofile-config.properties")
                    .map(name -> getClass().getClassLoader().getResource(name))
                    .map(createSource)
                    .collect(Collectors.toList());
        }
        return configSources;
    }
}

