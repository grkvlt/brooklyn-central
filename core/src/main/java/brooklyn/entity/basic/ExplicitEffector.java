package brooklyn.entity.basic;

import java.util.List;
import java.util.Map;

import brooklyn.entity.Entity;
import brooklyn.entity.ParameterType;

import com.google.common.collect.ImmutableList;

public abstract class ExplicitEffector<I,T> extends AbstractEffector<T> {
    public ExplicitEffector(String name, Class<T> type, String description) {
        this(name, type, ImmutableList.<ParameterType<?>>of(), description);
    }
    public ExplicitEffector(String name, Class<T> type, List<ParameterType<?>> parameters, String description) {
        super(name, type, parameters, description);
    }

    public T call(Entity entity, Map parameters) {
        return invokeEffector((I) entity, parameters );
    }

    public abstract T invokeEffector(I trait, Map<String,?> parameters);

}
