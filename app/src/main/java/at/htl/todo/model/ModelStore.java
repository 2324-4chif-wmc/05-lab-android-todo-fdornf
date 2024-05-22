package at.htl.todo.model;

import android.os.Build;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import at.htl.todo.util.store.Store;

@Singleton
public class ModelStore extends Store<Model>  {

    @Inject
    ModelStore() {
        super(Model.class, new Model());
    }



    public void setTodos(Todo[] todos) {
        apply(model -> model.todos = todos);
    }

    public void addTodo(Todo todo){


        apply(model -> {
            todo.id = (long) model.todos.length + 1;
            todo.userId = 1L;

            List<Todo> todosList = new ArrayList<>(Arrays.asList(model.todos));
            todosList.add(todo);
            model.todos = todosList.toArray(new Todo[0]);
        });
    }

    public void cleanTodods(){
        apply(model -> {
            LinkedList<Todo> newTodos = new LinkedList<Todo>();
            for (int i = 0; i < model.todos.length; i++){
                if (!model.todos[i].completed){
                    newTodos.add(model.todos[i]);
                }
            }
            model.todos = newTodos.stream().toArray(Todo[]::new);
        });
    }

    public void setDone(int id){
        Log.e("twat", "iUHBSUGFCHJK" + id);
        apply(model -> {
            Todo todoSafe;
            Arrays.stream(model.todos).forEach(todo -> {if(todo.id == id){
                todo.completed = !todo.completed;
            }});

            //model.todos[id -1].completed = !model.todos[id -1].completed;
        });
    }

    public void editTodo(@NotNull Todo todo) {
        apply(model -> {
            for (int i = 0; i < model.todos.length; i++) {
                if (model.todos[i].id.equals(todo.id)) {
                    model.todos[i].userId = todo.userId;
                    model.todos[i].title = todo.title;
                    model.todos[i].completed = todo.completed;
                    break;
                }
            }
        });
    }
}


