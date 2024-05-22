package at.htl.todo.ui.layout

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import at.htl.todo.model.Model
import at.htl.todo.model.ModelStore
import at.htl.todo.model.Todo
import at.htl.todo.model.TodoService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainView @Inject constructor() {

    @Inject
    lateinit var store: ModelStore

    @Inject
    lateinit var todoService: TodoService

    @OptIn(ExperimentalMaterial3Api::class)
    fun buildContent(activity: ComponentActivity) {
        activity.enableEdgeToEdge()
        activity.setContent {
            val navController = rememberNavController()
            val viewModel = store
                .pipe
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeAsState(initial = Model())
                .value

            MaterialTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Todo App", Modifier.clickable { navController.navigate("home") }) },
                            navigationIcon = {
                                IconButton(onClick = { navController.navigate("todos") }) {
                                    Icon(Icons.Filled.Menu, contentDescription = "All Todos")
                                }
                            },

                            modifier = Modifier.background(MaterialTheme.colorScheme.primary)

                            
                        )
                    },
                    content = { padding ->
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            NavHost(navController = navController, startDestination = "home") {
                                composable("home") {
                                    HomeScreen(viewModel = viewModel,todoService = todoService,store = store, onNavigateToTodos = {
                                        navController.navigate("todos")
                                    })
                                }
                                composable("todos") {
                                    TodosScreen(viewModel = viewModel, todoService = todoService, store = store, navigator = navController)
                                }
                                composable("todo/{id}") {
                                    var id: Long? = navController.previousBackStackEntry?.arguments?.getString("id")?.toLong();
                                    id = 5L;
                                    Log.e("Todo", "Todo with id $id")

                                    viewModel.todos.find { todo -> todo.id == id }?.let { it1 ->
                                        Log.e("Todo", "Todo found with id $id")
                                        todoViewScreen(todo = it1, navigator = navController)
                                    }
                                }
                                composable("todo/{id}/edit") {
                                    val id = navController.previousBackStackEntry?.arguments?.getString("id")
                                    Text("Edit Todo with id $id")
                                    viewModel.todos.find { todo -> todo.id == id?.toLong() }?.let { it1 ->
                                        editTodoScreen(todo = it1, store = store, navigator = navController)
                                    }
                                }
                                composable("add"){
                                    addTodoScreen(store = store, navigator = navController)
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(viewModel: Model,todoService: TodoService,store: ModelStore, onNavigateToTodos: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { todoService.getAll()},
            modifier = Modifier
                .width(200.dp)
                .height(50.dp)) {
            Text(text = "load Todos")
        }
        Text(text = "You have ${viewModel.todos.size} todos")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onNavigateToTodos() },
            modifier = Modifier
                .width(200.dp)
                .height(50.dp)
        ) {
            Text("View Todos")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { store.cleanTodods()},
            modifier = Modifier
                .width(200.dp)
                .height(50.dp)) {
            Text(text = "Clean Todos")
        }
    }
}

@Composable
fun TodosScreen(viewModel: Model, todoService: TodoService, store: ModelStore, navigator: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                navigator.navigate("add")
            },
            modifier = Modifier
                .width(200.dp)
                .height(50.dp)
        ) {
            Text("Add Todo")
        }
        Todos(model = viewModel, modifier = Modifier.padding(all = 32.dp), store = store, navigator = navigator)
    }
}


@Composable
fun Todos(model: Model, modifier: Modifier = Modifier, store: ModelStore, navigator: NavHostController) {
    LazyColumn(
        modifier = modifier.padding(16.dp)
    ) {

        items(model.todos.size) { index ->
            TodoRow(todo  = model.todos[index], store = store, navigator = navigator)
        }
    }
}

@Composable
fun TodoRow(todo: Todo, store: ModelStore, navigator: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = todo.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .clickable { navigator.navigate("todo/${todo.id}") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = todo.id.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Checkbox(
                checked = todo.completed,
                onCheckedChange = {store.setDone(todo.id.toInt())}
            )
        }
    }
}

@Composable
fun todoViewScreen(todo: Todo, navigator: NavHostController){
    Card (
        Modifier
            .fillMaxSize()
            .padding(16.dp)){
        viewWholeToDo(todo = todo, navigator = navigator, modifier = Modifier
            .fillMaxSize()
            .padding(16.dp));
    }
}


@Composable
fun viewWholeToDo(todo: Todo, navigator: NavHostController, modifier: Modifier = Modifier.fillMaxSize()){
    Column (modifier = modifier){
        Text(text = todo.id.toString(), style = MaterialTheme.typography.bodyLarge)
        Text(text = todo.title.toString(), style = MaterialTheme.typography.bodyLarge)
        Text(text = todo.completed.toString(), style = MaterialTheme.typography.bodyLarge)
        Button(onClick = {navigator.navigate("todo/${todo.id}/edit")}) {
            Text(text = "Edit")
        }
    }
}

@Composable
fun addTodoScreen(store: ModelStore, navigator: NavHostController){
    Card (
        Modifier
            .fillMaxSize()
            .padding(8.dp)){
        addTodo(todo = {todo ->
            run {
                store.addTodo(todo);
                navigator.navigate("todos");
            }
        })
    }
}

@Composable
fun addTodo(todo: (Todo) -> Unit){
    var title by remember { mutableStateOf("") }
    var completed by remember { mutableStateOf(false) }
    var id: Long = 0;

    Column (Modifier.fillMaxSize()){
        Text(text = "Add Todo", style = MaterialTheme.typography.bodyLarge)
        TextField(value = title, onValueChange = {title = it }, label = {Text("Title")})
        Text(text = "Done", style = MaterialTheme.typography.bodyMedium)
        Checkbox(checked = completed, onCheckedChange = {completed = it})
        Button(onClick = { todo(Todo(id,id,title,completed))
            completed = false
            title = ""
        }) {
            Text(text = "Submit")
        }


    }
}

@Composable
fun editTodoScreen(todo: Todo, store: ModelStore, navigator: NavHostController){
    Card (
        Modifier
            .fillMaxSize()
            .padding(8.dp)){
        editTodo(todo = {todo ->
            run {
                store.editTodo(todo);
                navigator.navigate("todos");
            }
        })
    }
}

@Composable
fun editTodo(todo: (Todo) -> Unit){
    var title by remember { mutableStateOf("") }
    var completed by remember { mutableStateOf(false) }
    var id: Long = 0;

    Column (Modifier.fillMaxSize()) {
        Text(text = "Edit Todo", style = MaterialTheme.typography.bodyLarge)
        TextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        Text(text = "Done", style = MaterialTheme.typography.bodyMedium)
        Checkbox(checked = completed, onCheckedChange = { completed = it })
        Button(onClick = {
            todo(Todo(id, id, title, completed))
            completed = false
            title = ""
        }) {
            Text(text = "Submit")
        }
    }
}