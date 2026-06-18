package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.InvoiceEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import com.example.repository.InvoiceRepository

@HiltViewModel
class InvoiceViewModel @Inject constructor(
    private val invoiceRepository: InvoiceRepository
) : ViewModel() {

    val invoices: StateFlow<List<InvoiceEntity>> = invoiceRepository.allInvoices
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveInvoice(clientName: String, description: String, amount: Double) {
        viewModelScope.launch {
            invoiceRepository.insertInvoice(
                InvoiceEntity(
                    clientName = clientName,
                    serviceDescription = description,
                    amount = amount
                )
            )
        }
    }
}
