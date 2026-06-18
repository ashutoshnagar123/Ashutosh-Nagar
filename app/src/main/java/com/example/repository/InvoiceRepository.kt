package com.example.repository

import com.example.data.InvoiceDao
import com.example.data.InvoiceEntity
import kotlinx.coroutines.flow.Flow

class InvoiceRepository(private val invoiceDao: InvoiceDao) {
    val allInvoices: Flow<List<InvoiceEntity>> = invoiceDao.getAllInvoices()

    suspend fun insertInvoice(invoice: InvoiceEntity) {
        invoiceDao.insertInvoice(invoice)
    }

    suspend fun deleteInvoice(invoice: InvoiceEntity) {
        invoiceDao.deleteInvoice(invoice)
    }
}
