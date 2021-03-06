package betsy.common.model;

import betsy.common.engines.ProcessLanguage;
import com.google.common.base.Objects;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public final class Engine {

    public static final String DELIMITER = "--";
    private final String name;
    private final String version;
    private final List<String> configuration;
    private final ProcessLanguage language;

    public Engine(ProcessLanguage language, String name, String version) {
        this(language, name, version, Collections.emptyList());
    }

    public Engine(ProcessLanguage language, String name, String version, String configuration) {
        this(language, name, version, Collections.singletonList(configuration));
    }

    public Engine(ProcessLanguage language, String name, String version, List<String> configuration) {
        this.language = language;
        this.name = requireNonNull(name);
        this.version = requireNonNull(version);

        List<String> values = new LinkedList<>();
        values.addAll(configuration);
        this.configuration = requireNonNull(Collections.unmodifiableList(values));
    }

    public String toString() {
        return getNormalizedId();
    }

    public String getNormalizedId() {
        return getId().replaceAll(DELIMITER,"__").replaceAll("\\.","_");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Engine engine = (Engine) o;
        return Objects.equal(toString(), engine.toString());
    }

    public ProcessLanguage getLanguage() {
        return language;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(toString());
    }

    public String getId() {
        List<String> values = new LinkedList<>();
        values.add(name);
        values.add(version);
        values.addAll(configuration);

        return String.join(DELIMITER, values);
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public List<String> getConfiguration() {
        return configuration;
    }
}