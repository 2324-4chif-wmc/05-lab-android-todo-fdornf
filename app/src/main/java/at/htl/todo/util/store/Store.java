

package at.htl.todo.util.store;

import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import at.htl.todo.util.immer.Immer;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class Store<T> {
    public final BehaviorSubject<T> pipe;
    public final Immer<T> immer;

    protected Store(Class<? extends T> type, T initialState) {
        try {
            pipe = BehaviorSubject.createDefault(initialState);
            immer = new Immer<T>(type);
        } catch (Exception e) {
            throw new CompletionException(e);
        }
    }
    public void apply(Consumer<T> recipe) {
        pipe.onNext(immer.produce(pipe.getValue(), recipe));
    }
}

