package at.htl.todo.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Todo {
    public Long userId;
    public Long id;
    public String title;
    public boolean completed;
    public Todo(){}

    public Todo(Long userId,@Nullable Long id, String title, boolean completed) {
        this.userId = userId;
        this.id = id;
        this.title = title;
        this.completed = completed;
    }
}
