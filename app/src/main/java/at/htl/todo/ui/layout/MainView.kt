package at.htl.todo.ui.layout

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
                            title = { Text("Todo App") },
                            Modifier.background(MaterialTheme.colorScheme.primary).clickable {
                                navController.navigate("home")
                            }
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
                                    HomeScreen(viewModel = viewModel, onNavigateToTodos = {
                                        navController.navigate("todos")
                                    })
                                }
                                composable("todos") {
                                    TodosScreen(viewModel = viewModel, todoService = todoService)
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
fun HomeScreen(viewModel: Model, onNavigateToTodos: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
    }
}

@Composable
fun TodosScreen(viewModel: Model, todoService: TodoService) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                todoService.getAll()
            },
            modifier = Modifier
                .width(200.dp)
                .height(50.dp)
        ) {
            Text("Load Todos now")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Todos(model = viewModel, modifier = Modifier.padding(all = 32.dp))
    }
}


@Composable
fun Todos(model: Model, modifier: Modifier = Modifier) {
    val todos = remember{mutableStateOf(model.todos)}
    LazyColumn(
        modifier = modifier.padding(16.dp)
    ) {

        items(todos.value.size) { index ->
            TodoRow(todo  = todos.value[index], model = model)
        }
    }
}

@Composable
fun TodoRow(todo: Todo, model: Model) {
    var completed = todo.completed
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
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = todo.id.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Checkbox(
                checked = completed,
                onCheckedChange = {  model.todos.find { it.id == todo.id }?.completed = it ; completed = !completed }
            )
        }
    }
}