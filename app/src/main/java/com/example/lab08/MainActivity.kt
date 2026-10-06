package com.example.lab08

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.lab08.data.Task
import com.example.lab08.data.TaskDatabase
import com.example.lab08.ui.theme.Lab08Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = Room.databaseBuilder(
            applicationContext,
            TaskDatabase::class.java,
            "task_db"
        ).build()

        val taskDao = database.taskDao()
        val viewModel = TaskViewModel(taskDao)

        setContent {
            Lab08Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun TaskScreen(viewModel: TaskViewModel) {

    val tasks by viewModel.tasks.collectAsState()

    var newTaskDescription by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = newTaskDescription,
            onValueChange = {
                newTaskDescription = it
            },
            label = {
                Text("Nueva tarea")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (newTaskDescription.isNotEmpty()) {
                    viewModel.addTask(newTaskDescription)
                    newTaskDescription = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Agregar tarea")
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(tasks) { task ->

                TaskRow(
                    task = task,
                    onToggle = {
                        viewModel.toggleTaskCompletion(task)
                    }
                )
            }
        }

        Button(
            onClick = {
                viewModel.deleteAllTasks()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Eliminar todas las tareas")
        }
    }
}

@Composable
fun TaskRow(
    task: Task,
    onToggle: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = task.description,
            modifier = Modifier
                .weight(1f)
                .padding(top = 12.dp)
        )

        Button(
            onClick = onToggle
        ) {
            Text(
                if (task.isCompleted)
                    "Completada"
                else
                    "Pendiente"
            )
        }
    }
}