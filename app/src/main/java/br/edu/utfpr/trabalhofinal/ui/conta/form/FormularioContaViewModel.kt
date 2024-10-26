package br.edu.utfpr.trabalhofinal.ui.conta.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import br.edu.utfpr.trabalhofinal.R
import br.edu.utfpr.trabalhofinal.data.AccountDatasource
import br.edu.utfpr.trabalhofinal.data.TipoContaEnum
import br.edu.utfpr.trabalhofinal.ui.Arguments
import java.math.BigDecimal
import java.time.LocalDate

class AccountFormViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val accountId: Int = savedStateHandle
        .get<String>(Arguments.ID_CONTA)
        ?.toIntOrNull() ?: 0

    var state: AccountFormState by mutableStateOf(AccountFormState(accountId = accountId))
        private set

    init {
        if (accountId > 0) {
            loadAccount()
        }
    }

    fun loadAccount() {
        state = state.copy(isLoading = true, hasErrorLoading = false)
        val account = AccountDatasource.instance.findOne(accountId)
        state = if (account == null) {
            state.copy(isLoading = false, hasErrorLoading = true)
        } else {
            state.copy(
                isLoading = false,
                account = account,
                description = state.description.copy(value = account.description),
                date = state.date.copy(value = account.date),
                amount = state.amount.copy(value = account.amount.toString()),
                paid = state.paid.copy(value = account.isPaid),
                type = state.type.copy(value = account.type.name)
            )
        }
    }

    fun onDescriptionChanged(newDescription: String) {
        if (state.description.value != newDescription) {
            state = state.copy(
                description = state.description.copy(
                    value = newDescription,
                )
            )
        }
    }

    private fun validateDescription(description: String): Int =
        if (description.isBlank()) R.string.descricao_obrigatoria else 0

    fun onDateChanged(newDate: LocalDate) {
        if (state.date.value != newDate) {
            state = state.copy(date = state.date.copy(value = newDate))
        }
    }



    fun onAmountChanged(newAmount: String) {
        if (state.amount.value != newAmount) {
            state = state.copy(amount = state.amount.copy(value = newAmount))
        }
    }

    fun onPaymentStatusChanged(newPaymentStatus: Boolean) {
        if (state.paid.value != newPaymentStatus) {
            state = state.copy(paid = state.paid.copy(value = newPaymentStatus))
        }
    }

    fun onTypeChanged(newType: String) {
        if (state.type.value != newType) {
            state = state.copy(type = state.type.copy(value = newType))
        }
    }

    fun saveAccount() {
        if (isFormValid()) {
            state = state.copy(isSaving = true)
            val account = state.account.copy(
                description = state.description.value,
                date = state.date.value,
                amount = BigDecimal(state.amount.value),
                isPaid = state.paid.value,
                type = TipoContaEnum.valueOf(state.type.value)
            )
            AccountDatasource.instance.save(account)
            state = state.copy(isSaving = false, accountPersistedOrRemoved = true)
        }
    }

    private fun isFormValid(): Boolean {
        return state.isFormValid
    }


    fun removeAccount() {
        state = state.copy(isDeleting = true)
        AccountDatasource.instance.remove(state.account)
        state = state.copy(isDeleting = false, accountPersistedOrRemoved = true)
    }

    fun onMessageDisplayed() {
        state = state.copy(messageCode = 0)
    }
}
