package at.htl.todo.util.immer;

import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Singleton;

import at.htl.todo.util.mapper.Mapper;


/** Immer simplifies handling immutable data structures.
 * @see <a>https://immerjs.github.io/immer/</a>
 * @see <a>https://redux.js.org/understanding/thinking-in-redux/motivation</a>
 *
 * @param <T> The type of the baseState
 */
@Singleton
public class Immer<T> {
    public final Mapper<T> mapper;

    @Inject
    public Immer(Class<? extends T> type) {
        mapper = new Mapper<T>(type);
    }

    /**
     * @param readonlyState the readonly readonlyState
     * @param recipe the callback function that modifies the cloned state
     * @return
     */
    public T produce(final T readonlyState, Consumer<T> recipe) {
        var nextState = mapper.clone(readonlyState);
        recipe.accept(nextState);
        return nextState;
    }
}
