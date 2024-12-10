package com.example.aufgabe3.ui.add

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.aufgabe3.model.BookingEntry
import com.example.aufgabe3.viewmodel.SharedViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    var name by remember { mutableStateOf("") }
    var arrivalDate by remember { mutableStateOf<LocalDate?>(null) }
    var departureDate by remember { mutableStateOf<LocalDate?>(null) }

    var showDateRangePicker by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Booking Entry") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = if (arrivalDate != null && departureDate != null) {
                    "${arrivalDate!!.format(dateFormatter)} - ${departureDate!!.format(dateFormatter)}"
                } else {
                    ""
                },
                onValueChange = {},
                label = { Text("Select Date Range") },
                enabled = false,
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDateRangePicker = true },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    nameError = name.isBlank()
                    // TODO Error handling and creating new BookingEntry and save in sharedViewModel
                    if (!nameError && arrivalDate != null && departureDate != null) {
                        val newBooking = BookingEntry(
                            name = name,
                            arrivalDate = arrivalDate!!,
                            departureDate = departureDate!!
                        )
                        sharedViewModel.addBookingEntry(newBooking)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
        if (nameError) {
            Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    // TODO implement DateRangePicker Dialog logic
    if (showDateRangePicker) {
        DateRangePickerModal(
            onDismiss = { showDateRangePicker = false },
            onDateSelected = { startDate, endDate ->
                arrivalDate = startDate
                departureDate = endDate
                showDateRangePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDismiss: () -> Unit,
    onDateSelected: (startDate: LocalDate, endDate: LocalDate) -> Unit
) {
    val context = LocalContext.current

    // States for storing selected dates
    val startDateState = rememberDatePickerState()
    val endDateState = rememberDatePickerState()
    var isPickingStartDate by remember { mutableStateOf(true) } // Toggle for start and end date

    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (isPickingStartDate) {
                    // Switch to picking end date
                    isPickingStartDate = false
                } else {
                    // Confirm and call onDateSelected
                    val selectedStartDateMillis = startDateState.selectedDateMillis
                    val selectedEndDateMillis = endDateState.selectedDateMillis

                    if (selectedStartDateMillis != null && selectedEndDateMillis != null) {
                        val startDate = LocalDate.ofEpochDay(selectedStartDateMillis / (24 * 60 * 60 * 1000))
                        val endDate = LocalDate.ofEpochDay(selectedEndDateMillis / (24 * 60 * 60 * 1000))

                        if (startDate <= endDate) {
                            onDateSelected(startDate, endDate)
                        } else {
                            Toast.makeText(context, "End date must be after start date!", Toast.LENGTH_SHORT).show()
                        }
                        onDismiss()
                    } else {
                        Toast.makeText(context, "Please select both start and end dates!", Toast.LENGTH_SHORT).show()
                    }
                }
            }) {
                Text(if (isPickingStartDate) "Next" else "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        content = {
            if (isPickingStartDate) {
                Column {
                    Text("Select Start Date", style = MaterialTheme.typography.titleMedium)
                    androidx.compose.material3.DatePicker(
                        state = startDateState,
                        title = { Text("Pick a start date") }
                    )
                }
            } else {
                Column {
                    Text("Select End Date", style = MaterialTheme.typography.titleMedium)
                    androidx.compose.material3.DatePicker(
                        state = endDateState,
                        title = { Text("Pick an end date") }
                    )
                }
            }
        }
    )
}

