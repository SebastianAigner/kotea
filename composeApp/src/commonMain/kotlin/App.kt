@file:OptIn(ExperimentalMaterialApi::class)

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.RoomDatabase
import androidx.room.util.TableInfo
import kotlinx.coroutines.launch
import mydatabase.AppDatabase
import mydatabase.Person
import mydatabase.TodoEntity
import mydatabase.getRoomDatabase
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@Composable
@Preview
fun App(databaseBuilder: RoomDatabase.Builder<AppDatabase>) {
    val database = remember { databaseBuilder.getRoomDatabase() }
    val dao = remember(database) { database.getDao() }

    val peopleWithTodos by dao.getPeopleWithTodos().collectAsState(emptyList())

    val scope = rememberCoroutineScope()
    var selectedPerson by remember { mutableStateOf<Person?>(null) }

    MaterialTheme {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            var isDropdownExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = it },
            ) {
                DropdownMenuItem({}) {
                    Text(
                        text = selectedPerson?.name ?: "[ No selection ]",
                        modifier = Modifier.weight(1f),
                    )
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                }

                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false },
                ) {
                    peopleWithTodos.forEach { personWithTodos ->
                        DropdownMenuItem(onClick = {
                            selectedPerson = personWithTodos.person
                            isDropdownExpanded = false
                        }) {
                            Text(text = personWithTodos.person.name)
                        }
                    }
                }
            }

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    scope.launch { dao.initUsers() }
                }) {
                    Text("Init people")
                }
                Button(onClick = {
                    scope.launch { dao.deleteAllPeople() }
                }) {
                    Text("Remove people")
                }
                Button(onClick = {
                    scope.launch {
                        dao.insert(
                            TodoEntity(
                                title = "I'm a todo",
                                content = "You complete me",
                                personId = selectedPerson?.id ?: 0L,
                            )
                        )
                    }
                }) {
                    Text("Insert")
                }
                Button(onClick = {
                    scope.launch {
                        selectedPerson?.id?.let {
                            dao.deleteAllTodosForUserId(it)
                        }
                    }
                }) {
                    Text("Delete todos for ${selectedPerson?.name}")
                }
                Button(onClick = {
                    scope.launch {
                        dao.deleteAll()
                    }
                }) {
                    Text("Delete all")
                }
            }

            for (value in peopleWithTodos) {
                key(value.person.id) {
                    Text(value.person.name, style = MaterialTheme.typography.h5)
                    Column {
                        value.todos.forEach {
                            key(it.id) {
                                Text("(${it.id}) ${it.title} - ${it.content}")
                            }
                        }
                    }
                }
            }
        }
    }
}
