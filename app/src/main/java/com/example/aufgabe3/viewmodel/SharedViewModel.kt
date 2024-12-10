package com.example.aufgabe3.viewmodel

import androidx.lifecycle.ViewModel
import com.example.aufgabe3.model.BookingEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedViewModel: ViewModel() {
    private val _bookingsEntries = MutableStateFlow<List<BookingEntry>>(emptyList())
    val bookingsEntries: StateFlow<List<BookingEntry>> = _bookingsEntries

    fun addBookingEntry(entry: BookingEntry){
        // TODO create a new booking entry and save it
        _bookingsEntries.value += entry
    }

    fun deleteBookingEntry(entry: BookingEntry){
        // TODO delete a new booking entry
        _bookingsEntries.value -= entry
    }
}